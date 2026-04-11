package com.liverecorder.listener;

import com.liverecorder.LiveRecorder;
import com.liverecorder.manager.LiveCore;
import com.liverecorder.model.RecorderBinding;
import com.liverecorder.util.CameraGeometry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

/**
 * 视觉反馈监听器
 * 处理目标玩家发光效果、镜头位置粒子展示
 */
public class VisualListener implements Listener {

    private final LiveRecorder plugin;
    private BukkitTask particleTask;

    public VisualListener(LiveRecorder plugin) {
        this.plugin = plugin;
        startParticleTask();
    }

    /**
     * 启动粒子展示任务
     */
    private void startParticleTask() {
        if (!plugin.getConfig().getBoolean("visual.camera-particle", false)) {
            return;
        }

        // 每 5 tick 展示一次粒子
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnCameraParticles();
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    /**
     * 在镜头位置生成粒子
     */
    private void spawnCameraParticles() {
        if (!plugin.getConfig().getBoolean("visual.camera-particle", false)) return;

        CameraGeometry geometry = plugin.getCameraGeometry();
        LiveCore liveCore = plugin.getLiveCore();
        Collection<RecorderBinding> bindings = liveCore.getAllBindings();

        String particleTypeName = plugin.getConfig().getString("visual.particle-type", "END_ROD");
        Particle particleType;
        try {
            particleType = Particle.valueOf(particleTypeName);
        } catch (IllegalArgumentException e) {
            particleType = Particle.END_ROD;
        }

        for (RecorderBinding binding : bindings) {
            if (!binding.isActive()) continue;

            Player recorder = binding.getRecorder();
            Player target = binding.getTarget();

            if (!recorder.isOnline() || target == null || !target.isOnline()) continue;

            // 计算镜头位置
            Location cameraLoc = geometry.calculateCameraLocation(target);

            // 在镜头位置生成粒子
            cameraLoc.getWorld().spawnParticle(
                    particleType,
                    cameraLoc,
                    1,      // 数量
                    0.1, 0.1, 0.1,  // 偏移
                    0.0     // 速度
            );
        }
    }

    /**
     * 玩家加入服务器时，更新发光效果
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LiveCore liveCore = plugin.getLiveCore();

        // 延迟处理，确保玩家完全加载
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            updateGlowEffect(player, liveCore);
        }, 10L);
    }

    /**
     * 更新目标玩家的发光效果
     */
    private void updateGlowEffect(Player target, LiveCore liveCore) {
        if (!plugin.getConfig().getBoolean("visual.target-glow", true)) {
            return;
        }

        if (liveCore.isTarget(target)) {
            target.setGlowing(true);
            // 注意：设置发光颜色需要使用团队机制
            applyGlowColor(target);
        }
    }

    /**
     * 应用发光颜色
     * 使用记分板团队机制设置发光颜色
     */
    private void applyGlowColor(Player target) {
        String colorName = plugin.getConfig().getString("visual.glow-color", "YELLOW");
        org.bukkit.ChatColor glowColor;
        try {
            glowColor = org.bukkit.ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            glowColor = org.bukkit.ChatColor.YELLOW;
        }

        // 获取或创建团队
        String teamName = "LR_Glow";
        org.bukkit.scoreboard.Scoreboard scoreboard = target.getScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setColor(glowColor);
        team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY,
                org.bukkit.scoreboard.Team.OptionStatus.ALWAYS);

        // 添加玩家到团队
        team.addEntry(target.getName());

        // 确保目标使用此记分板
        target.setScoreboard(scoreboard);
    }

    /**
     * 移除目标玩家的发光效果
     */
    public void removeGlowEffect(Player target) {
        target.setGlowing(false);

        // 从团队中移除
        org.bukkit.scoreboard.Scoreboard scoreboard = target.getScoreboard();
        org.bukkit.scoreboard.Team team = scoreboard.getTeam("LR_Glow");
        if (team != null) {
            team.removeEntry(target.getName());
            // 如果团队为空，删除团队
            if (team.getEntries().isEmpty()) {
                team.unregister();
            }
        }
    }

    /**
     * 刷新所有目标玩家的发光效果
     */
    public void refreshAllGlowEffects() {
        LiveCore liveCore = plugin.getLiveCore();
        boolean glowEnabled = plugin.getConfig().getBoolean("visual.target-glow", true);

        for (RecorderBinding binding : liveCore.getAllBindings()) {
            Player target = binding.getTarget();
            if (target == null || !target.isOnline()) continue;

            if (glowEnabled && binding.isActive()) {
                target.setGlowing(true);
                applyGlowColor(target);
            } else {
                removeGlowEffect(target);
            }
        }
    }

    /**
     * 停止粒子任务
     */
    public void shutdown() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
    }
}