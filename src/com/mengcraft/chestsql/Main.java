package com.mengcraft.chestsql;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{
	private static Plugin plugin;
	private DoSQL doSQL = new DoSQL();
	DoChest doChest = new DoChest();
	
	@Override
	public void onEnable()
	{
		plugin = this;
		getServer().getPluginManager().registerEvents(this, this);
		
		plugin.saveDefaultConfig();
		if (!plugin.getConfig().getBoolean("use")) {
			setEnabled(false);
		}
		if (doSQL.openConnect()) {
			getLogger().info("连接成功");
			if (doSQL.createTables()) {
				getLogger().info("检验成功");
				getLogger().info("开发者: min梦梦");
				getLogger().info("服务器出租店: http://shop105595113.taobao.com");
			}
			else {
				getLogger().info("检验失败");
				if (doSQL.closeConnect()) {
					getLogger().info("关闭连接");
				}
				else {
					getLogger().info("关闭连接失败");
				}
				setEnabled(false);
			}
		}
		else {
			getLogger().info("连接失败");
			if (doSQL.closeConnect()) {
				getLogger().info("关闭连接");
			}
			else {
				getLogger().info("关闭连接失败");
			}
			setEnabled(false);
		}
	}
	
	@Override
	public void onDisable()
	{
		if (doSQL.openConnect())
			if (doSQL.closeConnect()) {
				getLogger().info("开发者: min梦梦");
				getLogger().info("服务器出租店: http://shop105595113.taobao.com");
			}
			else {
				getLogger().info("关闭连接失败");
			}
	}
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("chestadmin")) {
			if (sender.hasPermission("chestsql.admin")) {
				if (args.length > 0){
					String chestType;
					String chestName;
					if (args[0].equalsIgnoreCase("lock")) {
						if (args.length > 1) {
							if (args[1].equalsIgnoreCase("public")) {
								if (args.length > 2) {
									chestType = "Public";
									chestName = args[2];
									if (doChest.lockChest(chestType, chestName)) {
										sender.sendMessage("锁定公共箱子"
												+chestName
												+ "成功");
										return true;
									}
									else {
										sender.sendMessage("锁定公共箱子"
												+chestName
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
									if (doChest.lockChest(chestType, chestName)) {
										sender.sendMessage("锁定私有箱子"
												+chestName
												+ "成功");
										return true;
									}
									else {
										sender.sendMessage("锁定私有箱子"
												+chestName
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
									if (doChest.unlockChest(chestType, chestName)) {
										sender.sendMessage("解锁公共箱子"
												+chestName
												+ "成功");
										return true;
									}
									else {
										sender.sendMessage("解锁公共箱子"
												+chestName
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
									if (doChest.unlockChest(chestType, chestName)) {
										sender.sendMessage("解锁私有箱子"
												+chestName
												+ "成功");
										return true;
									}
									else {
										sender.sendMessage("解锁私有箱子"
												+chestName
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
			}
			else {
				sender.sendMessage("你没有chestsql.admin权限");
				return false;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("chest")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("chestsql.use")) {
					if (args.length < 2)	{
						String chestType = null;
						Inventory inventory = null;
						String chestName = null;
						if (args.length < 1) {
							if (sender.hasPermission("chestsql.self")) {
								chestType = "Private";
								chestName = sender.getName();
								if (sender.hasPermission("chestsql.self.vip") ||
										sender.hasPermission("chestsql.admin")) {
									inventory = getServer().createInventory(null, 45, "远程箱子·私有·" + chestName);
								}
								else {
									inventory = getServer().createInventory(null, 27, "远程箱子·私有·" + chestName);
								}
							}
							else {
								sender.sendMessage("你没有使用私有箱子的权限");
								return false;
							}
						}
						else {
							chestName = args[0];
							if (sender.hasPermission("chestsql.public." + chestName)) {
								chestType = "Public";
								inventory = getServer().createInventory(null, 45, "远程箱子·公共·" + chestName);
							}
							else {
								sender.sendMessage("你没有使用公共箱子" + chestName + "的权限");
								return false;
							}
						}
						if (doSQL.openConnect()) {
							inventory = doChest.loadChest(chestType, chestName.toLowerCase(), inventory);
							if (inventory != null) {
								((HumanEntity) sender).openInventory(inventory);
							}
							else {
								sender.sendMessage("指定箱子已被他人载入或载入失败");
								return false;
							}								
						}
						else {
							sender.sendMessage("数据库连接失败请联系管理员");
							return false;
						}
					}
					else {
						getLogger().info("指令参数过多");
						return false;
						}
				}
				else {
					sender.sendMessage("你没有使用此命令的权限");
				}
			}
			else {
				sender.sendMessage("不能在控制台使用");
				return false;
				}
			}
		return false; 
		}
	
	@EventHandler
	public void closeInventory(InventoryCloseEvent event)
	{
		String[] title = event.getInventory().getTitle().split("·");
		if (title[0].equals("远程箱子")) {
			String chestType;
			if (title[1].equals("私有")) {
				chestType = "Private";
			}
			else {
				chestType = "Public";
			}
			String chestName = title[2];
			if (doSQL.openConnect()) {				
				Inventory inventory = event.getInventory();
				Player player = (Player) event.getPlayer();
				if (doChest.saveChest(chestType, chestName, inventory)) {
					player.sendMessage("远程箱子已保存");
				}
				else {
					getLogger().info("远程箱子保存失败");					
				}
				}
			}
		}
	
	public static Plugin getPlugin() {		
		return plugin;
	}
	
}