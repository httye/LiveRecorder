package com.liverecorder.command;

import com.liverecorder.LiveRecorder;
import com.liverecorder.manager.LiveCore;
import com.liverecorder.model.RecorderBinding;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * LiveRecorder 主命令处理器
 * 
 * 用法:
 * /lr bind <录制者> <目标> [auto|manual]  - 绑定录制者到目标
 * /lr unbind <录制者>                     - 解除录制者绑定
 * /lr list                                - 列出所有绑定
 * /lr mode <录制者> <auto|manual>         - 切换绑定模式
 * /lr switch <录制者> <新目标>            - 手动切换目标
 * /lr reload                              - 重载配置
 */
public class LiveRecorderCommand implements CommandExecutor {

    private final LiveRecorder plugin;

    public LiveRecorderCommand(LiveRecorder plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("liverecorder.admin")) {
            sender.sendMessage("§6[LiveRecorder] §c你没有权限使用此命令");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "bind":
                handleBind(sender, args);
                break;
            case "unbind":
                handleUnbind(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            case "mode":
                handleMode(sender, args);
                break;
            case "switch":
                handleSwitch(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    /**
     * 绑定录制者到目标玩家
     * /lr bind <录制者> <目标> [auto|manual]
     */
    private void handleBind(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§6[LiveRecorder] §c用法: /lr bind <录制者> <目标> [auto|manual]");
            return;
        }

        Player recorder = Bukkit.getPlayer(args[1]);
        Player target = Bukkit.getPlayer(args[2]);

        if (recorder == null || !recorder.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c录制者玩家 " + args[1] + " 不在线");
            return;
        }

        if (target == null || !target.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c目标玩家 " + args[2] + " 不在线");
            return;
        }

        if (recorder.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage("§6[LiveRecorder] §c录制者不能绑定到自己");
            return;
        }

        // 解析模式
        RecorderBinding.Mode mode = RecorderBinding.Mode.AUTO;
        if (args.length >= 4) {
            try {
                mode = RecorderBinding.Mode.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§6[LiveRecorder] §c无效的模式: " + args[3] + "，可选: auto, manual");
                return;
            }
        }

        LiveCore liveCore = plugin.getLiveCore();

        // 检查录制者是否已绑定
        if (liveCore.isRecorder(recorder)) {
            sender.sendMessage("§6[LiveRecorder] §c录制者 " + recorder.getName() + " 已有绑定，请先解绑");
            return;
        }

        // 执行绑定
        boolean success = liveCore.bindRecorder(recorder, target, mode);

        if (success) {
            sender.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a已绑定到目标 §f" + target.getName() + " §a(模式: §e" + mode.name() + "§a)");
            recorder.sendMessage("§6[LiveRecorder] §a你已被绑定为录制者，目标: §f" + target.getName() + " §a(模式: §e" + mode.name() + "§a)");
            target.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a已开始跟随你");
        } else {
            sender.sendMessage("§6[LiveRecorder] §c绑定失败");
        }
    }

    /**
     * 解除录制者绑定
     * /lr unbind <录制者>
     */
    private void handleUnbind(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§6[LiveRecorder] §c用法: /lr unbind <录制者>");
            return;
        }

        Player recorder = Bukkit.getPlayer(args[1]);

        if (recorder == null || !recorder.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c录制者玩家 " + args[1] + " 不在线");
            return;
        }

        LiveCore liveCore = plugin.getLiveCore();

        if (!liveCore.isRecorder(recorder)) {
            sender.sendMessage("§6[LiveRecorder] §c玩家 " + recorder.getName() + " 不是录制者");
            return;
        }

        RecorderBinding binding = liveCore.getBinding(recorder);
        String targetName = binding.getTarget() != null ? binding.getTarget().getName() : "无";

        boolean success = liveCore.unbindRecorder(recorder);

        if (success) {
            sender.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a已解除绑定 (原目标: §f" + targetName + "§a)");
            recorder.sendMessage("§6[LiveRecorder] §a你的录制者绑定已解除");
        } else {
            sender.sendMessage("§6[LiveRecorder] §c解绑失败");
        }
    }

    /**
     * 列出所有绑定
     * /lr list
     */
    private void handleList(CommandSender sender) {
        LiveCore liveCore = plugin.getLiveCore();
        java.util.Collection<RecorderBinding> bindings = liveCore.getAllBindings();

        if (bindings.isEmpty()) {
            sender.sendMessage("§6[LiveRecorder] §7当前没有活跃的录制者绑定");
            return;
        }

        sender.sendMessage("§6§l========== LiveRecorder 绑定列表 ==========");
        sender.sendMessage("§7共 §f" + bindings.size() + " §7个录制者");

        int index = 1;
        for (RecorderBinding binding : bindings) {
            String recorderName = binding.getRecorder().getName();
            String targetName = binding.getTarget() != null ? binding.getTarget().getName() : "§c无";
            String mode = binding.getMode() == RecorderBinding.Mode.AUTO ? "§e自动" : "§b手动";
            String status = binding.isActive() ? "§a活跃" : "§c暂停";

            sender.sendMessage(String.format(
                    "§7%d. §f%s §7→ §f%s §7| 模式: %s §7| 状态: %s",
                    index++, recorderName, targetName, mode, status
            ));
        }

        sender.sendMessage("§6§l===========================================");
    }

    /**
     * 切换绑定模式
     * /lr mode <录制者> <auto|manual>
     */
    private void handleMode(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§6[LiveRecorder] §c用法: /lr mode <录制者> <auto|manual>");
            return;
        }

        Player recorder = Bukkit.getPlayer(args[1]);

        if (recorder == null || !recorder.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c录制者玩家 " + args[1] + " 不在线");
            return;
        }

        LiveCore liveCore = plugin.getLiveCore();

        if (!liveCore.isRecorder(recorder)) {
            sender.sendMessage("§6[LiveRecorder] §c玩家 " + recorder.getName() + " 不是录制者");
            return;
        }

        RecorderBinding.Mode mode;
        try {
            mode = RecorderBinding.Mode.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§6[LiveRecorder] §c无效的模式: " + args[2] + "，可选: auto, manual");
            return;
        }

        RecorderBinding binding = liveCore.getBinding(recorder);
        binding.setMode(mode);

        sender.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a的模式已切换为: §e" + mode.name());
        recorder.sendMessage("§6[LiveRecorder] §a你的跟随模式已切换为: §e" + mode.name());
    }

    /**
     * 手动切换目标
     * /lr switch <录制者> <新目标>
     */
    private void handleSwitch(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§6[LiveRecorder] §c用法: /lr switch <录制者> <新目标>");
            return;
        }

        Player recorder = Bukkit.getPlayer(args[1]);
        Player newTarget = Bukkit.getPlayer(args[2]);

        if (recorder == null || !recorder.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c录制者玩家 " + args[1] + " 不在线");
            return;
        }

        if (newTarget == null || !newTarget.isOnline()) {
            sender.sendMessage("§6[LiveRecorder] §c目标玩家 " + args[2] + " 不在线");
            return;
        }

        LiveCore liveCore = plugin.getLiveCore();

        if (!liveCore.isRecorder(recorder)) {
            sender.sendMessage("§6[LiveRecorder] §c玩家 " + recorder.getName() + " 不是录制者");
            return;
        }

        RecorderBinding binding = liveCore.getBinding(recorder);
        String oldTargetName = binding.getTarget() != null ? binding.getTarget().getName() : "无";

        boolean success = liveCore.manualSwitch(recorder, newTarget);

        if (success) {
            sender.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a已从 §f" + oldTargetName + " §a切换到 §f" + newTarget.getName());
            recorder.sendMessage("§6[LiveRecorder] §a你的跟拍目标已切换为: §f" + newTarget.getName());
            newTarget.sendMessage("§6[LiveRecorder] §a录制者 §f" + recorder.getName() + " §a已开始跟随你");
        } else {
            sender.sendMessage("§6[LiveRecorder] §c切换失败");
        }
    }

    /**
     * 重载配置
     * /lr reload
     */
    private void handleReload(CommandSender sender) {
        plugin.reloadPluginConfig();
        sender.sendMessage("§6[LiveRecorder] §a配置已重载");
    }

    /**
     * 发送帮助信息
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l========== LiveRecorder 帮助 ==========");
        sender.sendMessage("§e/lr bind <录制者> <目标> [auto|manual] §7- 绑定录制者");
        sender.sendMessage("§e/lr unbind <录制者> §7- 解除录制者绑定");
        sender.sendMessage("§e/lr list §7- 列出所有绑定");
        sender.sendMessage("§e/lr mode <录制者> <auto|manual> §7- 切换绑定模式");
        sender.sendMessage("§e/lr switch <录制者> <新目标> §7- 手动切换目标");
        sender.sendMessage("§e/lr reload §7- 重载配置");
        sender.sendMessage("§6§l======================================");
    }
}