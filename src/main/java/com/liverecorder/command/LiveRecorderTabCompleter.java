package com.liverecorder.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LiveRecorder 命令 Tab 补全器
 */
public class LiveRecorderTabCompleter implements TabCompleter {

    private static final List<String> SUB_COMMANDS = Arrays.asList(
            "bind", "unbind", "list", "mode", "switch", "reload"
    );

    private static final List<String> MODES = Arrays.asList(
            "auto", "manual"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("liverecorder.admin")) {
            return completions;
        }

        if (args.length == 1) {
            // 补全子命令
            String input = args[0].toLowerCase();
            for (String sub : SUB_COMMANDS) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "bind":
                case "unbind":
                case "mode":
                case "switch":
                    // 补全在线玩家名
                    String input = args[1].toLowerCase();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(input)) {
                            completions.add(player.getName());
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "bind":
                case "switch":
                    // 补全在线玩家名（目标玩家）
                    String input = args[2].toLowerCase();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(input)) {
                            completions.add(player.getName());
                        }
                    }
                    break;
                case "mode":
                    // 补全模式
                    String modeInput = args[2].toLowerCase();
                    for (String mode : MODES) {
                        if (mode.startsWith(modeInput)) {
                            completions.add(mode);
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if ("bind".equals(subCommand)) {
                // 补全模式
                String modeInput = args[3].toLowerCase();
                for (String mode : MODES) {
                    if (mode.startsWith(modeInput)) {
                        completions.add(mode);
                    }
                }
            }
        }

        return completions;
    }
}