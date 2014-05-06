package com.mengcraft.ChestSQL;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class DoCommand {
	DoChest doChest = new DoChest();

	public boolean chest(CommandSender sender, String[] args) {
		Plugin plugin = ChestSQL.plugin;
		if (!(sender instanceof Player)) {
			sender.sendMessage("不能执行该命令");
			return true;
		}
		if (!sender.hasPermission("chestsql.use")) {
			sender.sendMessage("你没有chestsql.use权限");
			return true;
		}
		String chestType = null;
		String chestName = null;
		Inventory inventory = null;
		if (args.length > 0) {
			if (args.length > 1) {
				sender.sendMessage("/chest [Name]");
				return true;
			} else {
				if (sender.hasPermission("chestsql.public." + args[0])) {
					chestType = "Public";
					chestName = args[0];
					inventory = plugin.getServer().createInventory(null, 45,
							"远程箱子·公共·" + chestName);
				} else {
					sender.sendMessage("你没有chestsql.public." + args[0] + "权限");
					return true;
				}
			}
		} else {
			chestType = "Private";
			chestName = sender.getName().toLowerCase();
			if (sender.hasPermission("chestsql.use.vip")) {
				inventory = plugin.getServer().createInventory(null, 45,
						"远程箱子·私有·" + chestName);
			} else {
				inventory = plugin.getServer().createInventory(null, 27,
						"远程箱子·私有·" + chestName);
			}
		}
		inventory = doChest.loadChest(chestType, chestName, inventory);
		if (inventory != null) {
			((HumanEntity) sender).openInventory(inventory);
		} else {
			sender.sendMessage("指定箱子已被他人锁定或由于其他原因而载入失败");
			return false;
		}
		return false;

	}

	public boolean chestadmin(CommandSender sender, String[] args) {
		if (sender.hasPermission("chestsql.admin")) {
			if (args.length > 0) {
				String chestType;
				String chestName;
				if (args[0].equalsIgnoreCase("lock")) {
					if (args.length > 1) {
						if (args[1].equalsIgnoreCase("public")) {
							if (args.length > 2) {
								chestType = "Public";
								chestName = args[2];
								if (doChest.lockChest(chestType,
										chestName.toLowerCase())) {
									sender.sendMessage("锁定公共箱子" + chestName
											+ "成功");
									return true;
								} else {
									sender.sendMessage("锁定公共箱子" + chestName
											+ "失败");
									return false;
								}
							}
							sender.sendMessage("/chestadmin lock public [*]");
							return false;
						}
						if (args[1].equalsIgnoreCase("private")) {
							if (args.length > 2) {
								chestType = "Private";
								chestName = args[2];
								if (doChest.lockChest(chestType,
										chestName.toLowerCase())) {
									sender.sendMessage("锁定私有箱子" + chestName
											+ "成功");
									return true;
								} else {
									sender.sendMessage("锁定私有箱子" + chestName
											+ "失败");
									return false;
								}
							}
							sender.sendMessage("/chestadmin lock private [*]");
							return false;
						}
						sender.sendMessage("/chestadmin lock public [*]");
						sender.sendMessage("/chestadmin lock private [*]");
						return false;
					}
					sender.sendMessage("/chestadmin lock public [*]");
					sender.sendMessage("/chestadmin lock private [*]");
					return false;
				}
				if (args[0].equalsIgnoreCase("unlock")) {
					if (args.length > 1) {
						if (args[1].equalsIgnoreCase("public")) {
							if (args.length > 2) {
								chestType = "Public";
								chestName = args[2];
								if (doChest.unlockChest(chestType,
										chestName.toLowerCase())) {
									sender.sendMessage("解锁公共箱子" + chestName
											+ "成功");
									return true;
								} else {
									sender.sendMessage("解锁公共箱子" + chestName
											+ "失败");
									return false;
								}
							}
							sender.sendMessage("/chestadmin unlock public [*]");
							return false;
						}
						if (args[1].equalsIgnoreCase("private")) {
							if (args.length > 2) {
								chestType = "Private";
								chestName = args[2];
								if (doChest.unlockChest(chestType,
										chestName.toLowerCase())) {
									sender.sendMessage("解锁私有箱子" + chestName
											+ "成功");
									return true;
								} else {
									sender.sendMessage("解锁私有箱子" + chestName
											+ "失败");
									return false;
								}
							}
							sender.sendMessage("/chestadmin unlock private [*]");
							return false;
						}
						sender.sendMessage("/chestadmin unlock public [*]");
						sender.sendMessage("/chestadmin unlock private [*]");
						return false;
					}
					sender.sendMessage("/chestadmin unlock public [*]");
					sender.sendMessage("/chestadmin unlock private [*]");
					return false;
				}
				sender.sendMessage("/chestadmin lock");
				sender.sendMessage("/chestadmin unlock");
				return false;
			}
			sender.sendMessage("/chestadmin lock");
			sender.sendMessage("/chestadmin unlock");
			return false;
		} else {
			sender.sendMessage("你没有chestsql.admin权限");
			return false;
		}
	}

}
