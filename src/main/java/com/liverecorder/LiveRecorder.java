package com.liverecorder;

import com.liverecorder.command.LiveRecorderCommand;
import com.liverecorder.command.LiveRecorderTabCompleter;
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
    private LiveCore liveCore;
    private CameraGeometry cameraGeometry;

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置
        saveDefaultConfig();

        // 初始化镜头几何计算器
        cameraGeometry = new CameraGeometry(this);

        // 初始化核心管理器
        liveCore = new LiveCore(this);

        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(new CameraListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RecorderRestrictionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VisualListener(this), this);

        // 注册命令
        getCommand("liverecorder").setExecutor(new LiveRecorderCommand(this));
        getCommand("liverecorder").setTabCompleter(new LiveRecorderTabCompleter());

        // 启动核心任务
        liveCore.startTasks();

        getLogger().info("============================================");
        getLogger().info("  LiveRecorder 无人机式录制系统已启动");
        getLogger().info("  版本: " + getDescription().getVersion());
        getLogger().info("============================================");
    }

    @Override
    public void onDisable() {
        if (liveCore != null) {
            liveCore.shutdown();
        }
        getLogger().info("LiveRecorder 已停止");
    }

    /**
     * 重载配置
     */
    public void reloadPluginConfig() {
        reloadConfig();
        cameraGeometry.reload();
        liveCore.reload();
    }

    public static LiveRecorder getInstance() {
        return instance;
    }

    public LiveCore getLiveCore() {
        return liveCore;
    }

    public CameraGeometry getCameraGeometry() {
        return cameraGeometry;
    }
}