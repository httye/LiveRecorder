package com.liverecorder.manager;

import com.liverecorder.LiveRecorder;
import com.liverecorder.database.DatabaseManager;
import com.liverecorder.model.LiveLog;
import com.liverecorder.model.PrivacySetting;
import com.liverecorder.model.RecorderBinding;
import com.liverecorder.util.CameraGeometry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.util.Vector;

/**
 * LiveRecorder 核心管理器
 * 维护录制者绑定表，管理跟随任务和自动切换
 */
public class LiveCore {

    private final LiveRecorder plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, RecorderBinding> bindings;    // 录制者UUID -> 绑定
    private final Set<UUID> registeredTargets;             // 已注册的目标玩家
    private final Map<UUID, Set<UUID>> pendingRequests;    // 待确认的请求（目标UUID -> 录制者UUID集合）

    // 性能优化：缓存机制
    private final Map<UUID, Long> onlinePlayerCache = new ConcurrentHashMap<>();  // 在线玩家缓存
    private static final long CACHE_EXPIRY_TIME = 5000;  // 缓存过期时间（5秒）

    private BukkitTask followTask;       // 跟随任务
    private BukkitTask autoSwitchTask;   // 自动切换任务
    private BukkitTask actionBarTask;    // ActionBar 显示任务

    private boolean autoSwitchEnabled;
    private long autoSwitchInterval;     // 毫秒
    private AutoSwitchMode autoSwitchMode;

    public enum AutoSwitchMode {
        RANDOM,
        SEQUENTIAL
    }

    public LiveCore(LiveRecorder plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
        this.bindings = new ConcurrentHashMap<>();
        this.registeredTargets = ConcurrentHashMap.newKeySet();
        this.pendingRequests = new ConcurrentHashMap<>();
        loadConfig();
    }

    /**
     * 从配置文件加载设置
     */
    private void loadConfig() {
        autoSwitchEnabled = plugin.getConfig().getBoolean("auto-switch.enabled", true);
        autoSwitchInterval = plugin.getConfig().getLong("auto-switch.interval", 30) * 1000;
        String modeStr = plugin.getConfig().getString("auto-switch.mode", "RANDOM").toUpperCase();
        try {
            autoSwitchMode = AutoSwitchMode.valueOf(modeStr);
        } catch (IllegalArgumentException e) {
            autoSwitchMode = AutoSwitchMode.RANDOM;
        }
    }

    /**
     * 重载配置
     */
    public void reload() {
        loadConfig();
        // 重启任务以应用新配置
        shutdown();
        startTasks();
    }

    /**
     * 启动所有定时任务
     */
    public void startTasks() {
        // 跟随任务 - 每1 tick执行一次，实现真正的流式平滑移动
        // 使用插值计算中间位置，而不是依赖Velocity
        followTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateAllFollowers();
            }
        }.runTaskTimer(plugin, 1L, 1L);

        // 自动切换任务 - 每秒检查一次
        autoSwitchTask = new BukkitRunnable() {
            @Override
            public void run() {
                checkAutoSwitch();
            }
        }.runTaskTimer(plugin, 20L, 20L);

        // ActionBar 显示任务
        int actionBarInterval = plugin.getConfig().getInt("visual.actionbar-interval", 20);
        if (plugin.getConfig().getBoolean("visual.actionbar-enabled", true)) {
            actionBarTask = new BukkitRunnable() {
                @Override
                public void run() {
                    updateActionBar();
                }
            }.runTaskTimer(plugin, 0L, actionBarInterval);
        }
    }

    /**
     * 关闭所有任务并清理
     */
    public void shutdown() {
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }
        if (autoSwitchTask != null) {
            autoSwitchTask.cancel();
            autoSwitchTask = null;
        }
        if (actionBarTask != null) {
            actionBarTask.cancel();
            actionBarTask = null;
        }

        // 清理所有绑定
        for (RecorderBinding binding : bindings.values()) {
            cleanupBinding(binding);
        }
        bindings.clear();
        registeredTargets.clear();
        
        // 清理缓存
        clearAllCache();
    }

    // ========== 绑定管理 ==========

    /**
     * 绑定录制者到目标玩家
     *
     * @param recorder 录制者
     * @param target   目标玩家
     * @param mode     绑定模式
     * @return 绑定结果（0=成功，1=已绑定，2=拒绝，3=待确认）
     */
    public int bindRecorder(Player recorder, Player target, RecorderBinding.Mode mode) {
        // 参数验证
        if (recorder == null || target == null) {
            plugin.getLogger().warning("⚠ 绑定失败: 录制者或目标玩家为 null");
            return -1;
        }
        
        if (!recorder.isOnline() || !target.isOnline()) {
            plugin.getLogger().warning("⚠ 绑定失败: 玩家不在线 - 录制者: " + 
                (recorder.isOnline() ? "在线" : "离线") + ", 目标: " + 
                (target.isOnline() ? "在线" : "离线"));
            return -1;
        }
        
        if (mode == null) {
            plugin.getLogger().warning("⚠ 绑定失败: 绑定模式为 null，使用默认模式 AUTO");
            mode = RecorderBinding.Mode.AUTO;
        }
        
        UUID recorderId = recorder.getUniqueId();
        UUID targetId = target.getUniqueId();

        try {
            // 检查录制者是否已绑定
            if (bindings.containsKey(recorderId)) {
                plugin.getLogger().info("录制者 " + recorder.getName() + " 已经绑定到其他目标");
                return 1;
            }

            // 获取目标玩家的隐私设置
            PrivacySetting privacy = databaseManager.getPrivacySetting(targetId);

            // 如果隐私设置不存在，创建默认设置
            if (privacy == null) {
                privacy = new PrivacySetting(targetId, target.getName());
                databaseManager.savePrivacySetting(privacy);
                plugin.getLogger().info("为新玩家 " + target.getName() + " 创建隐私设置");
            }

            // 检查玩家是否拒绝直播
            if (privacy.hasDeclined()) {
                recorder.sendMessage("§6[LiveRecorder] §c玩家 " + target.getName() + " 已拒绝被直播");
                plugin.getLogger().info("玩家 " + target.getName() + " 拒绝了录制者 " + recorder.getName() + " 的直播请求");
                return 2;
            }

            // 如果玩家未设置隐私设置，发送确认请求
            if (privacy.needsPrompt()) {
                // 添加到待确认列表
                pendingRequests.computeIfAbsent(targetId, k -> ConcurrentHashMap.newKeySet()).add(recorderId);

                // 发送确认请求给目标玩家
                target.sendMessage("§6[LiveRecorder] §e录制者 " + recorder.getName() + " 请求直播您的视角");
                target.sendMessage("§6[LiveRecorder] §a输入 /lr accept §7同意直播");
                target.sendMessage("§6[LiveRecorder] §c输入 /lr decline §7拒绝直播");

                recorder.sendMessage("§6[LiveRecorder] §e已向 " + target.getName() + " 发送直播请求，请等待确认");
                
                plugin.getLogger().info("录制者 " + recorder.getName() + " 向 " + target.getName() + " 发送直播请求（待确认）");

                return 3;
            }

            // 玩家同意直播，执行绑定
            plugin.getLogger().info("执行绑定: " + recorder.getName() + " -> " + target.getName() + " (模式: " + mode + ")");
            return executeBind(recorder, target, mode);
            
        } catch (Exception e) {
            plugin.getLogger().severe("✗ 绑定录制者时发生错误: " + e.getMessage());
            e.printStackTrace();
            recorder.sendMessage("§6[LiveRecorder] §c绑定失败，请联系管理员");
            return -1;
        }
    }

    /**
     * 执行绑定操作
     *
     * @param recorder 录制者
     * @param target   目标玩家
     * @param mode     绑定模式
     * @return 是否绑定成功
     */
    private int executeBind(Player recorder, Player target, RecorderBinding.Mode mode) {
        UUID recorderId = recorder.getUniqueId();

        // 创建绑定
        RecorderBinding binding = new RecorderBinding(recorder, target, mode);
        bindings.put(recorderId, binding);
        registeredTargets.add(target.getUniqueId());

        // 初始化录制者位置 - 立即传送到镜头位置
        CameraGeometry geometry = plugin.getCameraGeometry();
        Location cameraLoc = geometry.calculateCameraLocation(target);
        recorder.teleport(cameraLoc);

        // 如果是观察者模式，切换到旁观者模式并设置跟随目标
        if (mode == RecorderBinding.Mode.SPECTATOR) {
            recorder.setGameMode(org.bukkit.GameMode.SPECTATOR);
            recorder.setSpectatorTarget(target);
        }

        // 设置录制者隐身（如果配置启用，且不是观察者模式）
        if (mode != RecorderBinding.Mode.SPECTATOR && plugin.getConfig().getBoolean("privacy.recorder-invisible.enabled", true)) {
            setRecorderInvisible(recorder, true);
        }

        // 记录日志
        LiveLog log = new LiveLog(
                0,
                LiveLog.LogType.START,
                recorderId,
                recorder.getName(),
                target.getUniqueId(),
                target.getName(),
                System.currentTimeMillis()
        );
        databaseManager.addLiveLog(log);

        plugin.getLogger().info(String.format(
                "录制者 %s 已绑定到目标 %s (模式: %s)",
                recorder.getName(), target.getName(), mode.name()
        ));

        return 0;
    }

    /**
     * 解除录制者绑定
     *
     * @param recorder 录制者
     * @return 是否解除成功
     */
    public boolean unbindRecorder(Player recorder) {
        UUID recorderId = recorder.getUniqueId();
        RecorderBinding binding = bindings.remove(recorderId);

        if (binding == null) {
            return false;
        }

        // 取消录制者隐身（如果不是观察者模式）
        RecorderBinding.Mode mode = binding.getMode();
        if (mode != RecorderBinding.Mode.SPECTATOR && plugin.getConfig().getBoolean("privacy.recorder-invisible.enabled", true)) {
            setRecorderInvisible(recorder, false);
        } else if (mode == RecorderBinding.Mode.SPECTATOR) {
            recorder.setSpectatorTarget(null);
        }

        // 记录日志
        Player target = binding.getTarget();
        if (target != null) {
            LiveLog log = new LiveLog(
                    0,
                    LiveLog.LogType.END,
                    recorderId,
                    recorder.getName(),
                    target.getUniqueId(),
                    target.getName(),
                    System.currentTimeMillis()
            );
            databaseManager.addLiveLog(log);
        }

        cleanupBinding(binding);

        plugin.getLogger().info(String.format(
                "录制者 %s 已解除绑定", recorder.getName()
        ));

        return true;
    }

    /**
     * 清理绑定资源
     */
    private void cleanupBinding(RecorderBinding binding) {
        binding.setActive(false);
        binding.setFollowing(false);

        // 移除目标玩家的发光效果
        Player target = binding.getTarget();
        if (target != null && target.isOnline()) {
            target.setGlowing(false);
        }

        // 检查是否还有其他录制者绑定到同一目标
        if (target != null) {
            boolean stillBound = bindings.values().stream()
                    .anyMatch(b -> b.getTarget() != null && b.getTarget().getUniqueId().equals(target.getUniqueId()));
            if (!stillBound) {
                registeredTargets.remove(target.getUniqueId());
            }
        }
    }

    /**
     * 获取录制者的绑定信息
     *
     * @param recorder 录制者
     * @return 绑定信息，不存在则返回 null
     */
    public RecorderBinding getBinding(Player recorder) {
        return bindings.get(recorder.getUniqueId());
    }

    /**
     * 检查玩家是否为录制者
     */
    public boolean isRecorder(Player player) {
        return bindings.containsKey(player.getUniqueId());
    }

    /**
     * 检查玩家是否为被跟拍的目标
     */
    public boolean isTarget(Player player) {
        return registeredTargets.contains(player.getUniqueId());
    }

    /**
     * 获取所有绑定
     */
    public Collection<RecorderBinding> getAllBindings() {
        return Collections.unmodifiableCollection(bindings.values());
    }

    /**
     * 获取绑定到指定目标的所有录制者
     */
    public List<RecorderBinding> getBindingsForTarget(Player target) {
        return bindings.values().stream()
                .filter(b -> b.getTarget() != null && b.getTarget().getUniqueId().equals(target.getUniqueId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有在线的目标玩家
     */
    public List<Player> getOnlineTargets() {
        return registeredTargets.stream()
                .filter(this::isPlayerOnlineCached)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ========== 跟随系统 ==========

private void updateAllFollowers() {
    CameraGeometry geometry = plugin.getCameraGeometry();

    for (RecorderBinding binding : bindings.values()) {
        if (!binding.isActive()) continue;

        Player recorder = binding.getRecorder();
        Player target = binding.getTarget();

        if (!recorder.isOnline() || target == null || !target.isOnline()) {
            continue;
        }

        // 无论何种模式都通过镜头几何计算跟随位，保证跟拍一致性
        // 观察者模式仅保留无碰撞/自由观察能力，不再锁定第一视角。
        if (binding.getMode() == RecorderBinding.Mode.SPECTATOR && recorder.getSpectatorTarget() != null) {
            recorder.setSpectatorTarget(null);
        }

        if (binding.getMode() == RecorderBinding.Mode.SPECTATOR) {
            if (recorder.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                recorder.setGameMode(org.bukkit.GameMode.SPECTATOR);
            }
        }

        // 计算目标相机位置
        Location cameraTarget = geometry.calculateCameraLocation(target);
        Location currentLoc = recorder.getLocation();

        // 当距离过大时直接传送，避免长时间延迟跟随
        if (geometry.needsTeleport(recorder, cameraTarget, 30.0)) {
            recorder.teleport(cameraTarget);
            binding.setFollowing(true);
            continue;
        }

        // 使用平滑插值计算中间位置
        double positionSmooth = plugin.getConfig().getDouble("camera.position-smooth", 0.12);
        double rotationSmooth = plugin.getConfig().getDouble("camera.rotation-smooth", 0.1);

        // 观察者模式给更柔和的阻尼，避免镜头“僵硬”或突兀。
        if (binding.getMode() == RecorderBinding.Mode.SPECTATOR) {
            positionSmooth = Math.max(0.04, positionSmooth * 0.7);
            rotationSmooth = Math.max(0.04, rotationSmooth * 0.65);
        }

        Location smoothed = geometry.calculateSmoothedState(currentLoc, cameraTarget, target, positionSmooth, rotationSmooth);
        recorder.teleport(smoothed);
        binding.setFollowing(true);
    }
}
   
    /**
     * 计算平滑速度：使用 S 曲线实现更自然的速度过渡
     * 近距离时速度很低，中等距离时速度适中，远距离时速度较高
     */
    private double calculateSmoothSpeed(double distance) {
    	if (distance < 1.0) {
    		// 极近距离：缓慢接近
    		return distance * 2.0;
    	} else if (distance < 3.0) {
    		// 近距离：平滑加速
    		double t = (distance - 1.0) / 2.0; // 归一化到 [0, 1]
    		return 2.0 + 4.0 * easeInOutQuad(t);
    	} else if (distance < 8.0) {
    		// 中等距离：稳定速度
    		double t = (distance - 3.0) / 5.0; // 归一化到 [0, 1]
    		return 6.0 + 4.0 * easeInOutQuad(t);
    	} else {
    		// 远距离：最大速度
    		double t = Math.min((distance - 8.0) / 12.0, 1.0); // 归一化到 [0, 1]
    		return 10.0 + 2.0 * easeOutQuad(t);
    	}
    }
   
    /**
     * 缓动函数：easeInOutQuad
     * 实现平滑的加速和减速
     */
    private double easeInOutQuad(double t) {
    	return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }
   
    /**
     * 缓动函数：easeOutQuad
     * 实现平滑的减速
     */
    private double easeOutQuad(double t) {
    	return t * (2 - t);
    }
   
    /**
     * 计算预测性偏移：根据目标的移动趋势调整方向
     * 让镜头能够更好地预判目标的移动
     */
    private Vector calculatePredictionOffset(Player target, Vector currentDirection, double speed) {
    	// 获取目标的当前速度向量
    	Vector targetVelocity = target.getVelocity();
    	
    	// 如果目标移动速度很小，不进行预测
    	if (targetVelocity.length() < 0.1) {
    		return new Vector(0, 0, 0);
    	}
   
    	// 计算目标移动方向与当前镜头方向的夹角
    	Vector targetDirection = targetVelocity.clone().normalize();
    	double dotProduct = currentDirection.dot(targetDirection);
    	
    	// 如果目标正在远离镜头，增加预测偏移
    	if (dotProduct > 0.3) {
    		// 预测偏移量基于目标速度和距离
    		double predictionStrength = Math.min(targetVelocity.length() * 0.5, 0.3);
    		return targetDirection.multiply(predictionStrength);
    	}
    	
    	return new Vector(0, 0, 0);
    }
   
    /**
     * 平滑视角更新：使用插值实现更自然的视角旋转
     */
    private void updateSmoothRotation(Player recorder, Player target, CameraGeometry geometry) {
    	Location currentLoc = recorder.getLocation();
    	Location targetLoc = target.getLocation();
    	
    	// 计算看向目标的方向
    	Vector toTarget = targetLoc.toVector().subtract(currentLoc.toVector());
    	
    	// 计算目标 yaw 和 pitch
    	float targetYaw = (float) Math.toDegrees(Math.atan2(-toTarget.getX(), toTarget.getZ()));
    	float targetPitch = (float) Math.toDegrees(Math.atan2(-toTarget.getY(),
    		Math.sqrt(toTarget.getX() * toTarget.getX() + toTarget.getZ() * toTarget.getZ())));
    	
    	// 使用几何工具类中的角度插值方法
    	float smoothYaw = geometry.interpolateAngle(currentLoc.getYaw(), targetYaw, (float) geometry.getRotationSmooth());
    	float smoothPitch = geometry.interpolateAngle(currentLoc.getPitch(), targetPitch, (float) geometry.getRotationSmooth());
    	
    	// 应用平滑后的视角
    	recorder.setRotation(smoothYaw, smoothPitch);
    }

    // ========== 自动切换 ==========

    /**
     * 检查并执行自动切换
     */
    private void checkAutoSwitch() {
        if (!autoSwitchEnabled) return;

        for (RecorderBinding binding : bindings.values()) {
            if (!binding.isActive()) continue;
            if (!binding.shouldAutoSwitch(autoSwitchInterval)) continue;

            // 获取可切换的目标列表（排除当前目标）
            List<Player> availableTargets = getOnlineTargets().stream()
                    .filter(p -> !p.getUniqueId().equals(binding.getTarget().getUniqueId()))
                    .collect(Collectors.toList());

            if (availableTargets.isEmpty()) continue;

            Player newTarget;
            if (autoSwitchMode == AutoSwitchMode.RANDOM) {
                newTarget = availableTargets.get(new Random().nextInt(availableTargets.size()));
            } else {
                // 顺序模式
                List<Player> allTargets = getOnlineTargets();
                int currentIndex = -1;
                for (int i = 0; i < allTargets.size(); i++) {
                    if (allTargets.get(i).getUniqueId().equals(binding.getTarget().getUniqueId())) {
                        currentIndex = i;
                        break;
                    }
                }
                int nextIndex = (currentIndex + 1) % allTargets.size();
                newTarget = allTargets.get(nextIndex);
            }

            // 执行切换
            Player oldTarget = binding.getTarget();
            binding.switchTarget(newTarget);

            // 更新目标注册表
            registeredTargets.add(newTarget.getUniqueId());
            if (oldTarget != null) {
                boolean stillBound = bindings.values().stream()
                        .anyMatch(b -> b.getTarget() != null && b.getTarget().getUniqueId().equals(oldTarget.getUniqueId()));
                if (!stillBound) {
                    registeredTargets.remove(oldTarget.getUniqueId());
                    oldTarget.setGlowing(false);
                }
            }

            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().info(String.format(
                        "[Debug] 自动切换: 录制者 %s 从 %s 切换到 %s",
                        binding.getRecorder().getName(),
                        oldTarget != null ? oldTarget.getName() : "null",
                        newTarget.getName()
                ));
            }
        }
    }

    /**
     * 手动切换录制者的目标
     *
     * @param recorder 录制者
     * @param newTarget 新目标
     * @return 是否切换成功
     */
    public boolean manualSwitch(Player recorder, Player newTarget) {
        RecorderBinding binding = bindings.get(recorder.getUniqueId());
        if (binding == null) return false;

        Player oldTarget = binding.getTarget();
        binding.switchTarget(newTarget);

        // 更新目标注册表
        registeredTargets.add(newTarget.getUniqueId());
        if (oldTarget != null) {
            boolean stillBound = bindings.values().stream()
                    .anyMatch(b -> b.getTarget() != null && b.getTarget().getUniqueId().equals(oldTarget.getUniqueId()));
            if (!stillBound) {
                registeredTargets.remove(oldTarget.getUniqueId());
                oldTarget.setGlowing(false);
            }
        }

        return true;
    }

    /**
     * 切换录制者绑定模式，并处理模式切换的副作用（隐身/旁观目标）
     */
    public boolean switchMode(Player recorder, RecorderBinding.Mode newMode) {
        RecorderBinding binding = bindings.get(recorder.getUniqueId());
        if (binding == null || newMode == null) {
            return false;
        }

        RecorderBinding.Mode oldMode = binding.getMode();
        if (oldMode == newMode) {
            return true;
        }

        boolean invisibleEnabled = plugin.getConfig().getBoolean("privacy.recorder-invisible.enabled", true);

        // manual/auto -> spectator：取消隐身，切旁观
        if (newMode == RecorderBinding.Mode.SPECTATOR) {
            if (oldMode != RecorderBinding.Mode.SPECTATOR && invisibleEnabled) {
                setRecorderInvisible(recorder, false);
            }
            recorder.setGameMode(org.bukkit.GameMode.SPECTATOR);
            recorder.setSpectatorTarget(null);
        } else {
            // spectator -> manual/auto：退出旁观目标，恢复隐身
            if (oldMode == RecorderBinding.Mode.SPECTATOR) {
                recorder.setSpectatorTarget(null);
            }
            if (invisibleEnabled) {
                setRecorderInvisible(recorder, true);
            }
        }

        binding.setMode(newMode);
        return true;
    }

    // ========== ActionBar 显示 ==========

    /**
     * 更新所有录制者和目标玩家的 ActionBar
     */
    private void updateActionBar() {
        for (RecorderBinding binding : bindings.values()) {
            if (!binding.isActive()) continue;

            Player recorder = binding.getRecorder();
            if (!recorder.isOnline()) continue;

            Player target = binding.getTarget();
            String targetName = target != null ? target.getName() : "无";

            // 录制者 ActionBar：显示跟随状态
            String status = binding.isFollowing() ? "§a● 跟随中" : "§7○ 待机";
            String modeStr;
            if (binding.getMode() == RecorderBinding.Mode.AUTO) {
                modeStr = "§e自动";
            } else if (binding.getMode() == RecorderBinding.Mode.SPECTATOR) {
                modeStr = "§d观察者";
            } else {
                modeStr = "§b手动";
            }

            String recorderMessage = String.format(
                    "§6LiveRecorder §7| %s §7| 目标: §f%s §7| 模式: %s",
                    status, targetName, modeStr
            );

            recorder.spigot().sendMessage(
                    net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    new net.md_5.bungee.api.chat.TextComponent(recorderMessage)
            );

            // 目标玩家 ActionBar：显示"您正在被直播"提示
            if (target != null && target.isOnline()) {
                if (plugin.getConfig().getBoolean("visual.target-actionbar", true)) {
                    // 统计跟拍该目标的录制者数量
                    long recorderCount = bindings.values().stream()
                            .filter(b -> b.isActive() && b.getTarget() != null
                                    && b.getTarget().getUniqueId().equals(target.getUniqueId()))
                            .count();

                    String targetMessage = String.format(
                            "§c§l🔴 您正在被直播 §7| §e%d 位录制者跟拍中",
                            recorderCount
                    );

                    target.spigot().sendMessage(
                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                            new net.md_5.bungee.api.chat.TextComponent(targetMessage)
                    );
                }
            }
        }
    }

    // ========== 玩家传送处理 ==========

    /**
     * 当目标玩家传送时，立即重新计算镜头位置并传送录制者
     *
     * @param target 传送的目标玩家
     */
    public void handleTargetTeleport(Player target) {
        List<RecorderBinding> targetBindings = getBindingsForTarget(target);
        CameraGeometry geometry = plugin.getCameraGeometry();

        for (RecorderBinding binding : targetBindings) {
            if (!binding.isActive()) continue;

            Player recorder = binding.getRecorder();
            if (!recorder.isOnline()) continue;

            Location cameraLoc = geometry.calculateCameraLocation(target);
            recorder.teleport(cameraLoc);

            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().info(String.format(
                        "[Debug] 目标 %s 传送，录制者 %s 已跟随传送",
                        target.getName(), recorder.getName()
                ));
            }
        }
    }

    // ========== Getter ==========

    public boolean isAutoSwitchEnabled() {
        return autoSwitchEnabled;
    }

    public void setAutoSwitchEnabled(boolean enabled) {
        this.autoSwitchEnabled = enabled;
    }

    public AutoSwitchMode getAutoSwitchMode() {
        return autoSwitchMode;
    }

    public void setAutoSwitchMode(AutoSwitchMode mode) {
        this.autoSwitchMode = mode;
    }

    // ========== 隐身功能 ==========

    /**
     * 设置录制者隐身状态
     *
     * @param recorder   录制者
     * @param invisible  是否隐身
     */
    private void setRecorderInvisible(Player recorder, boolean invisible) {
        if (invisible) {
            // 对所有其他玩家隐藏录制者
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(recorder)) {
                    other.hidePlayer(plugin, recorder);
                }
            }
        } else {
            // 对所有其他玩家显示录制者
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.equals(recorder)) {
                    other.showPlayer(plugin, recorder);
                }
            }
        }
    }

    /**
     * 当新玩家加入时，更新隐身状态
     *
     * @param player 新加入的玩家
     */
    public void handlePlayerJoin(Player player) {
        for (RecorderBinding binding : bindings.values()) {
            Player recorder = binding.getRecorder();
            if (recorder.isOnline() && plugin.getConfig().getBoolean("privacy.recorder-invisible.enabled", true)) {
                player.hidePlayer(plugin, recorder);
            }
        }
    }

    // ========== 隐私确认 ==========

    /**
     * 玩家同意直播
     *
     * @param player 玩家
     * @return 是否有待确认的请求被处理
     */
    public boolean acceptStreaming(Player player) {
        UUID playerId = player.getUniqueId();
        Set<UUID> requesters = pendingRequests.remove(playerId);

        if (requesters == null || requesters.isEmpty()) {
            return false;
        }

        // 更新隐私设置为同意
        PrivacySetting privacy = databaseManager.getPrivacySetting(playerId);
        if (privacy == null) {
            privacy = new PrivacySetting(playerId, player.getName());
        }
        privacy.setConsentStatus(PrivacySetting.ConsentStatus.ACCEPTED);
        databaseManager.savePrivacySetting(privacy);

        // 记录同意日志
        LiveLog log = new LiveLog(
                0,
                LiveLog.LogType.ACCEPTED,
                null,
                null,
                playerId,
                player.getName(),
                System.currentTimeMillis()
        );
        databaseManager.addLiveLog(log);

        // 处理所有待确认的请求
        for (UUID recorderId : requesters) {
            Player recorder = Bukkit.getPlayer(recorderId);
            if (recorder != null && recorder.isOnline()) {
                // 默认使用手动模式
                executeBind(recorder, player, RecorderBinding.Mode.MANUAL);
                player.sendMessage("§6[LiveRecorder] §a已同意录制者 " + recorder.getName() + " 的直播请求");
            }
        }

        return true;
    }

    /**
     * 玩家拒绝直播
     *
     * @param player 玩家
     * @return 是否有待确认的请求被处理
     */
    public boolean declineStreaming(Player player) {
        UUID playerId = player.getUniqueId();
        Set<UUID> requesters = pendingRequests.remove(playerId);

        if (requesters == null || requesters.isEmpty()) {
            return false;
        }

        // 更新隐私设置为拒绝
        PrivacySetting privacy = databaseManager.getPrivacySetting(playerId);
        if (privacy == null) {
            privacy = new PrivacySetting(playerId, player.getName());
        }
        privacy.setConsentStatus(PrivacySetting.ConsentStatus.DECLINED);
        databaseManager.savePrivacySetting(privacy);

        // 记录拒绝日志
        LiveLog log = new LiveLog(
                0,
                LiveLog.LogType.DECLINED,
                null,
                null,
                playerId,
                player.getName(),
                System.currentTimeMillis()
        );
        databaseManager.addLiveLog(log);

        // 通知所有录制者
        for (UUID recorderId : requesters) {
            Player recorder = Bukkit.getPlayer(recorderId);
            if (recorder != null && recorder.isOnline()) {
                recorder.sendMessage("§6[LiveRecorder] §c玩家 " + player.getName() + " 拒绝了直播请求");
            }
        }

        player.sendMessage("§6[LiveRecorder] §a已拒绝直播请求，以后也不会收到该录制者的请求");

        return true;
    }

    /**
     * 获取玩家的隐私设置
     *
     * @param player 玩家
     * @return 隐私设置
     */
    public PrivacySetting getPlayerPrivacy(Player player) {
        PrivacySetting privacy = databaseManager.getPrivacySetting(player.getUniqueId());
        if (privacy == null) {
            privacy = new PrivacySetting(player.getUniqueId(), player.getName());
            databaseManager.savePrivacySetting(privacy);
        }
        return privacy;
    }

    /**
     * 设置玩家的隐私状态
     *
     * @param player         玩家
     * @param consentStatus  同意状态
     * @return 是否成功
     */
    public boolean setPlayerPrivacy(Player player, PrivacySetting.ConsentStatus consentStatus) {
        PrivacySetting privacy = databaseManager.getPrivacySetting(player.getUniqueId());
        if (privacy == null) {
            privacy = new PrivacySetting(player.getUniqueId(), player.getName());
        }
        privacy.setConsentStatus(consentStatus);
        return databaseManager.savePrivacySetting(privacy);
    }

    /**
     * 获取直播日志
     *
     * @param limit 数量限制
     * @return 日志列表
     */
    public List<LiveLog> getLiveLogs(int limit) {
        return databaseManager.getLiveLogs(limit);
    }

    // ========== 性能优化：缓存管理方法 ==========

    /**
     * 检查玩家是否在线（使用缓存优化）
     * 
     * @param playerId 玩家 UUID
     * @return true 如果玩家在线
     */
    private boolean isPlayerOnlineCached(UUID playerId) {
        Long cacheTime = onlinePlayerCache.get(playerId);
        long now = System.currentTimeMillis();
        
        // 检查缓存是否过期
        if (cacheTime != null && (now - cacheTime) < CACHE_EXPIRY_TIME) {
            return true;
        }
        
        // 缓存过期或不存在，重新检查
        Player player = Bukkit.getPlayer(playerId);
        boolean online = player != null && player.isOnline();
        
        if (online) {
            onlinePlayerCache.put(playerId, now);
        } else {
            onlinePlayerCache.remove(playerId);
        }
        
        return online;
    }

    /**
     * 清理过期的缓存
     * 建议在低峰期定期调用
     */
    public void cleanupExpiredCache() {
        long now = System.currentTimeMillis();
        onlinePlayerCache.entrySet().removeIf(entry -> 
            (now - entry.getValue()) >= CACHE_EXPIRY_TIME
        );
    }

    /**
     * 清除所有缓存
     * 在插件禁用时调用
     */
    public void clearAllCache() {
        onlinePlayerCache.clear();
    }

    /**
     * 获取缓存统计信息
     * 
     * @return 缓存大小
     */
    public int getCacheSize() {
        return onlinePlayerCache.size();
    }
}
