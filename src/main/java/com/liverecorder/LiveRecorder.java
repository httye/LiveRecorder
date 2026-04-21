package com.liverecorder;

import com.liverecorder.command.LiveRecorderCommand;
import com.liverecorder.command.LiveRecorderTabCompleter;
import com.liverecorder.database.DatabaseManager;
import com.liverecorder.listener.CameraListener;
import com.liverecorder.listener.RecorderRestrictionListener;
import com.liverecorder.listener.VisualListener;
import com.liverecorder.manager.LiveCore;
import com.liverecorder.util.CameraGeometry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * LiveRecorder 主插件类
 * 无人机式自由录制者管理系统
 */
public class LiveRecorder extends JavaPlugin {

    private static LiveRecorder instance;
    private DatabaseManager databaseManager;
    private LiveCore liveCore;
    private CameraGeometry cameraGeometry;

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        // 打印启动横幅
        printBanner();

        getLogger().info("正在初始化插件...");

        // 保存默认配置
        saveDefaultConfig();
        
        // 验证配置
        if (!validateConfig()) {
            getLogger().severe("✗ 配置验证失败，插件将禁用");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("✓ 配置文件已加载并验证通过");

        // 初始化数据库管理器
        databaseManager = new DatabaseManager(this);
        getLogger().info("✓ 数据库管理器已初始化");

        // 初始化镜头几何计算器
        cameraGeometry = new CameraGeometry(this);
        getLogger().info("✓ 镜头几何计算器已初始化");

        // 初始化核心管理器
        liveCore = new LiveCore(this);
        getLogger().info("✓ 核心管理器已初始化");

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new CameraListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RecorderRestrictionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VisualListener(this), this);
        getLogger().info("✓ 事件监听器已注册 (3 个)");

        // 注册命令
        getCommand("liverecorder").setExecutor(new LiveRecorderCommand(this));
        getCommand("liverecorder").setTabCompleter(new LiveRecorderTabCompleter());
        getLogger().info("✓ 命令处理器已注册");

        // 启动核心任务
        liveCore.startTasks();
        getLogger().info("✓ 核心任务已启动");

        // 执行自检
        runSelfCheck();

        // 计算启动时间
        long loadTime = System.currentTimeMillis() - startTime;

        // 打印启动完成信息
        getLogger().info("");
        getLogger().info("============================================");
        getLogger().info("  LiveRecorder 无人机式录制系统已启动");
        getLogger().info("  版本: " + getDescription().getVersion());
        getLogger().info("  启动耗时: " + loadTime + "ms");
        getLogger().info("============================================");
        getLogger().info("  © 七月 | 七月个人制作组");
        getLogger().info("  开源协议: MIT License");
        getLogger().info("  GitHub: https://github.com/httye/LiveRecorder");
        getLogger().info("============================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("正在关闭 LiveRecorder...");

        if (liveCore != null) {
            liveCore.shutdown();
            getLogger().info("✓ 核心任务已停止");
        }

        if (databaseManager != null) {
            databaseManager.close();
            getLogger().info("✓ 数据库连接已关闭");
        }

        getLogger().info("");
        getLogger().info("============================================");
        getLogger().info("  LiveRecorder 已安全关闭");
        getLogger().info("  感谢使用!");
        getLogger().info("============================================");
    }

    /**
     * 重载配置
     */
    public void reloadPluginConfig() {
        reloadConfig();
        cameraGeometry.reload();
        liveCore.reload();
    }

    /**
     * 打印启动横幅
     */
    private void printBanner() {
        getLogger().info("");
        getLogger().info("============================================");
        getLogger().info("");
        getLogger().info("  ■         ■■■■");
        getLogger().info("  ■         ■    ■");
        getLogger().info("  ■         ■■■■");
        getLogger().info("  ■         ■    ■");
        getLogger().info("  ■■■■■     ■    ■");
        getLogger().info("");
        getLogger().info("      LiveRecorder");
        getLogger().info("      无人机式自由录制者管理系统");
        getLogger().info("");
        getLogger().info("============================================");
    }

    /**
     * 执行自检
     */
    private void runSelfCheck() {
        getLogger().info("");
        getLogger().info("正在执行系统自检...");

        // 检查数据库
        try {
            if (databaseManager != null && databaseManager.getConnection() != null) {
                getLogger().info("✓ 数据库连接正常");
            } else {
                getLogger().warning("⚠ 数据库连接异常");
            }
        } catch (Exception e) {
            getLogger().severe("✗ 数据库连接失败: " + e.getMessage());
        }

        // 检查配置
        try {
            if (getConfig() != null && !getConfig().getKeys(false).isEmpty()) {
                getLogger().info("✓ 配置文件加载正常");
            } else {
                getLogger().warning("⚠ 配置文件为空");
            }
        } catch (Exception e) {
            getLogger().severe("✗ 配置文件加载失败: " + e.getMessage());
        }

        // 检查服务器版本
        try {
            String serverVersion = Bukkit.getVersion();
            getLogger().info("✓ 服务器版本: " + serverVersion);
        } catch (Exception e) {
            getLogger().warning("⚠ 无法获取服务器版本");
        }

        // 检查在线玩家
        try {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            getLogger().info("✓ 在线玩家: " + onlinePlayers);
        } catch (Exception e) {
            getLogger().warning("⚠ 无法获取在线玩家数");
        }

        // 检查内存
        try {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
            long maxMemory = runtime.maxMemory() / 1024 / 1024;
            getLogger().info("✓ 内存使用: " + usedMemory + "MB / " + maxMemory + "MB");
        } catch (Exception e) {
            getLogger().warning("⚠ 无法获取内存信息");
        }

        getLogger().info("✓ 系统自检完成");
    }

    public static LiveRecorder getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LiveCore getLiveCore() {
        return liveCore;
    }

    public CameraGeometry getCameraGeometry() {
        return cameraGeometry;
    }

    /**
     * 验证配置文件的有效性和合理性
     * 
     * @return true 如果配置有效，false 如果存在严重错误
     */
    private boolean validateConfig() {
        boolean isValid = true;
        
        // ========== 镜头设置验证 ==========
        try {
            double pitch = getConfig().getDouble("camera.pitch", 30.0);
            if (pitch < 0 || pitch > 90) {
                getLogger().warning("⚠ 配置警告: camera.pitch (" + pitch + ") 超出范围 [0-90]，已重置为默认值 30.0");
                getConfig().set("camera.pitch", 30.0);
                isValid = false;
            }
            
            double distance = getConfig().getDouble("camera.distance", 5.0);
            if (distance < 1.0 || distance > 20.0) {
                getLogger().warning("⚠ 配置警告: camera.distance (" + distance + ") 超出推荐范围 [1.0-20.0]，已重置为默认值 5.0");
                getConfig().set("camera.distance", 5.0);
                isValid = false;
            }
            
            double heightOffset = getConfig().getDouble("camera.height-offset", 0.0);
            if (heightOffset < -5.0 || heightOffset > 5.0) {
                getLogger().warning("⚠ 配置警告: camera.height-offset (" + heightOffset + ") 超出范围 [-5.0-5.0]，已重置为默认值 0.0");
                getConfig().set("camera.height-offset", 0.0);
                isValid = false;
            }
            
            double positionSmooth = getConfig().getDouble("camera.position-smooth", 0.12);
            if (positionSmooth < 0.01 || positionSmooth > 1.0) {
                getLogger().warning("⚠ 配置警告: camera.position-smooth (" + positionSmooth + ") 超出范围 [0.01-1.0]，已重置为默认值 0.12");
                getConfig().set("camera.position-smooth", 0.12);
                isValid = false;
            }
            
            double rotationSmooth = getConfig().getDouble("camera.rotation-smooth", 0.1);
            if (rotationSmooth < 0.01 || rotationSmooth > 1.0) {
                getLogger().warning("⚠ 配置警告: camera.rotation-smooth (" + rotationSmooth + ") 超出范围 [0.01-1.0]，已重置为默认值 0.1");
                getConfig().set("camera.rotation-smooth", 0.1);
                isValid = false;
            }
        } catch (Exception e) {
            getLogger().severe("✗ 镜头配置验证失败: " + e.getMessage());
            isValid = false;
        }
        
        // ========== 自动切换设置验证 ==========
        try {
            long interval = getConfig().getLong("auto-switch.interval", 30);
            if (interval < 5 || interval > 300) {
                getLogger().warning("⚠ 配置警告: auto-switch.interval (" + interval + "秒) 超出推荐范围 [5-300]，已重置为默认值 30");
                getConfig().set("auto-switch.interval", 30);
                isValid = false;
            }
            
            String mode = getConfig().getString("auto-switch.mode", "RANDOM");
            if (!mode.equalsIgnoreCase("RANDOM") && !mode.equalsIgnoreCase("SEQUENTIAL")) {
                getLogger().warning("⚠ 配置警告: auto-switch.mode (" + mode + ") 无效，已重置为默认值 RANDOM");
                getConfig().set("auto-switch.mode", "RANDOM");
                isValid = false;
            }
        } catch (Exception e) {
            getLogger().severe("✗ 自动切换配置验证失败: " + e.getMessage());
            isValid = false;
        }
        
        // ========== 视觉反馈设置验证 ==========
        try {
            String glowColor = getConfig().getString("visual.glow-color", "YELLOW");
            // 验证颜色是否有效
            try {
                org.bukkit.Color.class.getDeclaredField(glowColor.toUpperCase());
            } catch (NoSuchFieldException e) {
                getLogger().warning("⚠ 配置警告: visual.glow-color (" + glowColor + ") 不是有效的颜色名称，已重置为默认值 YELLOW");
                getConfig().set("visual.glow-color", "YELLOW");
                isValid = false;
            }
            
            int actionbarInterval = getConfig().getInt("visual.actionbar-interval", 20);
            if (actionbarInterval < 1 || actionbarInterval > 100) {
                getLogger().warning("⚠ 配置警告: visual.actionbar-interval (" + actionbarInterval + ") 超出范围 [1-100]，已重置为默认值 20");
                getConfig().set("visual.actionbar-interval", 20);
                isValid = false;
            }
        } catch (Exception e) {
            getLogger().severe("✗ 视觉反馈配置验证失败: " + e.getMessage());
            isValid = false;
        }
        
        // ========== 隐私设置验证 ==========
        try {
            int keepCount = getConfig().getInt("privacy.live-logs.keep-count", 100);
            if (keepCount < 10 || keepCount > 1000) {
                getLogger().warning("⚠ 配置警告: privacy.live-logs.keep-count (" + keepCount + ") 超出推荐范围 [10-1000]，已重置为默认值 100");
                getConfig().set("privacy.live-logs.keep-count", 100);
                isValid = false;
            }
            
            long timeout = getConfig().getLong("privacy.consent-prompt.timeout", 60);
            if (timeout < 10 || timeout > 300) {
                getLogger().warning("⚠ 配置警告: privacy.consent-prompt.timeout (" + timeout + "秒) 超出范围 [10-300]，已重置为默认值 60");
                getConfig().set("privacy.consent-prompt.timeout", 60);
                isValid = false;
            }
        } catch (Exception e) {
            getLogger().severe("✗ 隐私设置验证失败: " + e.getMessage());
            isValid = false;
        }
        
        // 如果有修改，保存配置
        if (!isValid) {
            saveConfig();
            getLogger().info("✓ 已自动修复无效配置项");
        }
        
        return true; // 即使有警告也允许插件启动
    }
}