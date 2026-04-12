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
        getLogger().info("✓ 配置文件已加载");

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
        getLogger().info("    ███████╗ █████╗ ███████╗██╗   ██╗");
        getLogger().info("    ██╔════╝██╔══██╗██╔════╝██║   ██║");
        getLogger().info("    ███████╗███████║███████╗██║   ██║");
        getLogger().info("    ╚════██║██╔══██║╚════██║██║   ██║");
        getLogger().info("    ███████║██║  ██║███████║╚██████╔╝");
        getLogger().info("    ╚══════╝╚═╝  ╚═╝╚══════╝ ╚═════╝");
        getLogger().info("");
        getLogger().info("           无人机式自由录制者管理系统");
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
}