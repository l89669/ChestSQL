package com.mengcraft.ChestSQL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSQL extends JavaPlugin implements Listener {
	public static Plugin plugin;
	DoSQL doSQL = new DoSQL();
	DoChest doChest = new DoChest();
	DoCommand doCommand = new DoCommand();

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		if (getConfig().getBoolean("use")) {
			plugin = this;
			if (doSQL.openConnect()) {
				getLogger().info("数据库连接成功");
				if (doSQL.createTables()) {
					getServer().getPluginManager().registerEvents(this, this);
					getLogger().info("数据表效验成功");
				} else {
					getLogger().info("数据表效验失败");
					setEnabled(false);
				}
			} else {
				getLogger().info("数据库连接失败");
				setEnabled(false);
			}
		} else {
			getLogger().info("请在配置文件中启用插件");
			setEnabled(false);
		}
	}

	@Override
	public void onDisable() {
		if (!getConfig().getBoolean("use")) {
			return;
		}
		if (doSQL.openConnect()) {
			if (doChest.saveAllChest()) {
				getLogger().info("保存所有打开的远程箱子成功");
			} else {
				getLogger().info("保存所有打开的远程箱子失败");
			}
			if (doSQL.closeConnect()) {
				getLogger().info("关闭数据库连接成功");
			} else {
				getLogger().info("关闭数据库连接失败");
			}
		}
		getLogger().info("Disabled ChestSQL!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("chestadmin")) {
			return doCommand.chestadmin(sender, args);
		}

		if (cmd.getName().equalsIgnoreCase("chest")) {
			return doCommand.chest(sender, args);
		}
		return false;
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		String[] title = event.getInventory().getTitle().split("·");
		if (title[0].equals("远程箱子")) {
			String chestType;
			if (title[1].equals("私有")) {
				chestType = "Private";
			} else {
				chestType = "Public";
			}
			String chestName = title[2];
			Inventory inventory = event.getInventory();
			if (doChest
					.saveChest(chestType, chestName.toLowerCase(), inventory)) {
			} else {
				getLogger().info("远程箱子保存失败");
			}
		}
	}
}
