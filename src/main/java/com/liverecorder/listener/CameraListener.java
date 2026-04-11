package com.liverecorder.listener;

import com.liverecorder.LiveRecorder;
import com.liverecorder.manager.LiveCore;
import com.liverecorder.model.RecorderBinding;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 镜头跟随事件监听器
 * 处理 PlayerMoveEvent 触发同步移动，以及传送事件处理
 */
public class CameraListener implements Listener {

    private final LiveRecorder plugin;

    public CameraListener(LiveRecorder plugin) {
        this.plugin = plugin;
    }

    /**
     * 玩家移动事件
     * 当目标玩家移动时，标记需要更新镜头位置
     * 实际的跟随逻辑在 LiveCore 的定时任务中处理
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // 检查是否实际移动了位置（忽略仅转头）
        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();
        LiveCore liveCore = plugin.getLiveCore();

        // 如果移动的是目标玩家，触发录制者位置更新
        if (liveCore.isTarget(player)) {
            // 跟随逻辑由 LiveCore 的 tick 任务处理
            // 这里只做标记或额外处理
        }

        // 阻止录制者自主移动
        if (liveCore.isRecorder(player)) {
            RecorderBinding binding = liveCore.getBinding(player);
            if (binding != null && binding.isActive()) {
                // 取消录制者的自主移动，由系统控制位置
                event.setCancelled(true);
            }
        }
    }

    /**
     * 玩家传送事件
     * 当目标玩家传送时，立即重新计算镜头位置并传送录制者
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        LiveCore liveCore = plugin.getLiveCore();

        // 目标玩家传送
        if (liveCore.isTarget(player)) {
            // 延迟 1 tick 处理，确保目标玩家已到达新位置
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                liveCore.handleTargetTeleport(player);
            }, 1L);
        }

        // 阻止录制者自主传送
        if (liveCore.isRecorder(player)) {
            RecorderBinding binding = liveCore.getBinding(player);
            if (binding != null && binding.isActive()) {
                // 允许系统传送，阻止玩家自行传送
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND
                        || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
                    // 如果是插件/命令引起的，检查是否来自 LiveRecorder
                    // 这里简单处理：允许插件传送
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * 玩家重生事件
     * 目标玩家重生时，重新定位录制者
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LiveCore liveCore = plugin.getLiveCore();

        if (liveCore.isTarget(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                liveCore.handleTargetTeleport(player);
            }, 2L);
        }
    }

    /**
     * 玩家退出事件
     * 清理绑定关系
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LiveCore liveCore = plugin.getLiveCore();

        // 如果退出的是录制者，解除绑定
        if (liveCore.isRecorder(player)) {
            liveCore.unbindRecorder(player);
        }

        // 如果退出的是目标玩家，通知绑定的录制者
        if (liveCore.isTarget(player)) {
            for (RecorderBinding binding : liveCore.getAllBindings()) {
                if (binding.getTarget() != null && binding.getTarget().getUniqueId().equals(player.getUniqueId())) {
                    // 切换到其他在线目标，或标记为非活跃
                    java.util.List<Player> onlineTargets = liveCore.getOnlineTargets().stream()
                            .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                            .collect(java.util.stream.Collectors.toList());

                    if (!onlineTargets.isEmpty()) {
                        binding.switchTarget(onlineTargets.get(0));
                        binding.getRecorder().sendMessage("§6[LiveRecorder] §e目标玩家 " + player.getName() + " 已下线，自动切换到 " + onlineTargets.get(0).getName());
                    } else {
                        binding.setActive(false);
                        binding.getRecorder().sendMessage("§6[LiveRecorder] §c目标玩家 " + player.getName() + " 已下线，无可用目标，跟随已暂停");
                    }
                }
            }
        }
    }
}