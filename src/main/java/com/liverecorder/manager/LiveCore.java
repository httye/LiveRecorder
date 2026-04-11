package com.liverecorder.manager;

import com.liverecorder.LiveRecorder;
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

/**
 * LiveRecorder 核心管理器
 * 维护录制者绑定表，管理跟随任务和自动切换
 */
public class LiveCore {

    private final LiveRecorder plugin;
    private final Map<UUID, RecorderBinding> bindings;    // 录制者UUID -> 绑定
    private final Set<UUID> registeredTargets;             // 已注册的目标玩家

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
        this.bindings = new ConcurrentHashMap<>();
        this.registeredTargets = ConcurrentHashMap.newKeySet();
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
        // 跟随任务 - 每 tick 执行，确保流畅跟随
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
    }

    // ========== 绑定管理 ==========

    /**
     * 绑定录制者到目标玩家
     *
     * @param recorder 录制者
     * @param target   目标玩家
     * @param mode     绑定模式
     * @return 是否绑定成功
     */
    public boolean bindRecorder(Player recorder, Player target, RecorderBinding.Mode mode) {
        UUID recorderId = recorder.getUniqueId();

        // 检查录制者是否已绑定
        if (bindings.containsKey(recorderId)) {
            return false;
        }

        // 创建绑定
        RecorderBinding binding = new RecorderBinding(recorder, target, mode);
        bindings.put(recorderId, binding);
        registeredTargets.add(target.getUniqueId());

        // 初始化录制者位置 - 立即传送到镜头位置
        CameraGeometry geometry = plugin.getCameraGeometry();
        Location cameraLoc = geometry.calculateCameraLocation(target);
        recorder.teleport(cameraLoc);

        // 设置录制者为旁观者模式（如果需要）
        // recorder.setGameMode(GameMode.SPECTATOR);

        plugin.getLogger().info(String.format(
                "录制者 %s 已绑定到目标 %s (模式: %s)",
                recorder.getName(), target.getName(), mode.name()
        ));

        return true;
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
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .filter(Player::isOnline)
                .collect(Collectors.toList());
    }

    // ========== 跟随系统 ==========

    /**
     * 更新所有录制者的跟随位置
     */
    private void updateAllFollowers() {
        CameraGeometry geometry = plugin.getCameraGeometry();

        for (RecorderBinding binding : bindings.values()) {
            if (!binding.isActive()) continue;

            Player recorder = binding.getRecorder();
            Player target = binding.getTarget();

            // 检查玩家在线状态
            if (!recorder.isOnline() || target == null || !target.isOnline()) {
                continue;
            }

            // 计算镜头目标位置
            Location cameraTarget = geometry.calculateCameraLocation(target);

            // 检查是否需要传送（距离过远或不同世界）
            if (geometry.needsTeleport(recorder, cameraTarget, 30.0)) {
                recorder.teleport(cameraTarget);
                if (plugin.getConfig().getBoolean("debug", false)) {
                    plugin.getLogger().info(String.format(
                            "[Debug] 录制者 %s 传送到目标 %s 镜头位置",
                            recorder.getName(), target.getName()
                    ));
                }
                continue;
            }

            // 使用 setVelocity 实现平滑跟随
            Location followResult = geometry.calculateFollowVelocity(recorder, cameraTarget);
            if (followResult != null) {
                // 计算速度向量
                double vx = followResult.getX() - recorder.getLocation().getX();
                double vy = followResult.getY() - recorder.getLocation().getY();
                double vz = followResult.getZ() - recorder.getLocation().getZ();

                recorder.setVelocity(new org.bukkit.util.Vector(vx, vy, vz));

                // 更新录制者朝向（看向目标）
                Location lookDir = recorder.getLocation().clone();
                lookDir.setDirection(followResult.getDirection());
                recorder.teleport(lookDir);

                binding.setFollowing(true);
            } else {
                binding.setFollowing(false);
            }
        }
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
            String modeStr = binding.getMode() == RecorderBinding.Mode.AUTO ? "§e自动" : "§b手动";

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
}