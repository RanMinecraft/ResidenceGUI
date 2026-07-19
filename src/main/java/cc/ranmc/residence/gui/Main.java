package cc.ranmc.residence.gui;

import cc.ranmc.residence.gui.command.ResguiCommand;
import cc.ranmc.residence.gui.listener.GUIListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

import static cc.ranmc.residence.gui.util.BasicUtil.color;
import static cc.ranmc.residence.gui.util.BasicUtil.print;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        print(color("&e-----------------------"));
        print(color("&dBy Ranica"));
        print(color("&bVersion: " + getPluginMeta().getVersion()));
        print(color("&chttps://www.ranmc.cn/"));
        print(color("&e-----------------------"));

        // 注册指令
        PluginCommand cmd = Objects.requireNonNull(getCommand("resgui"));
        cmd.setExecutor(new ResguiCommand());
        cmd.setTabCompleter((sender, command, alias, args) -> {
            if (args.length == 1 && sender.hasPermission("resgui.admin")) {
                return java.util.List.of("help", "reload", "perm");
            }
            return java.util.Collections.emptyList();
        });

        // 加载配置
        loadConfig();

        // 注册监听器
        Bukkit.getPluginManager().registerEvents(new GUIListener(), this);

        super.onEnable();
    }

    @Override
    public void onDisable() {
        print(color("&a插件已经成功卸载"));
        super.onDisable();
    }

    public void loadConfig() {
        if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();

        if (Bukkit.getPluginManager().getPlugin("Residence") != null) {
            print(color("&a成功加载Residence"));
        } else {
            print(color("&c无法找到Residence"));
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
