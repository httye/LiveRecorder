package com.liverecorder.listener;

import com.liverecorder.LiveRecorder;
import com.liverecorder.manager.LiveCore;
import com.liverecorder.model.RecorderBinding;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

/**
 * 录制者操作限制监听器
 * 阻止录制者打开背包/容器、丢弃物品、交互等
 * 保证录制画面不会被背包/GUI 遮挡
 */
public class RecorderRestrictionListener implements Listener {

    private final LiveRecorder plugin;

    public RecorderRestrictionListener(LiveRecorder plugin) {
        this.plugin = plugin;
    }

    /**
     * 检查玩家是否为活跃的录制者
     */
    private boolean isActiveRecorder(Player player) {
        LiveCore liveCore = plugin.getLiveCore();
        if (!liveCore.isRecorder(player)) return false;
        RecorderBinding binding = liveCore.getBinding(player);
        return binding != null && binding.isActive();
    }

    /**
     * 阻止打开背包/容器
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        if (!isActiveRecorder(player)) return;

        // 检查是否阻止打开容器
        if (plugin.getConfig().getBoolean("recorder-restrictions.block-container", true)) {
            event.setCancelled(true);
            return;
        }

        // 检查是否阻止打开背包
        if (plugin.getConfig().getBoolean("recorder-restrictions.block-inventory", true)) {
            String title = event.getView().getTitle().toLowerCase();
            // 判断是否为玩家背包
            if (isPlayerInventory(event)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 判断是否为玩家自身背包界面
     */
    private boolean isPlayerInventory(InventoryOpenEvent event) {
        return event.getInventory().getType().name().equals("PLAYER")
                || event.getView().getTitle().contains("背包")
                || event.getView().getTitle().toLowerCase().contains("inventory");
    }

    /**
     * 阻止在背包中点击物品
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-inventory", true)
                || plugin.getConfig().getBoolean("recorder-restrictions.block-container", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止丢弃物品
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-drop-item", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止交互（拉杆、按钮、门等）
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-interact", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止攻击实体
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-attack", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止放置方块
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-place", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止破坏方块
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!isActiveRecorder(player)) return;

        if (plugin.getConfig().getBoolean("recorder-restrictions.block-break", true)) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止使用命令（白名单除外）
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!isActiveRecorder(player)) return;

        if (!plugin.getConfig().getBoolean("recorder-restrictions.block-command", true)) {
            return;
        }

        // 提取命令名
        String message = event.getMessage();
        String command = message.split(" ")[0].substring(1).toLowerCase(); // 去掉 "/"

        // 检查白名单
        List<String> whitelist = plugin.getConfig().getStringList("recorder-restrictions.command-whitelist");
        for (String allowed : whitelist) {
            if (command.equalsIgnoreCase(allowed)) {
                return; // 允许执行
            }
        }

        // 阻止执行
        event.setCancelled(true);
        player.sendMessage("§6[LiveRecorder] §c录制者无法使用该命令");
    }
}