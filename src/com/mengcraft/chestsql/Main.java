package com.mengcraft.chestsql;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
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
		HandlerList.unregisterAll((Plugin) this);
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
		if (cmd.getName().equalsIgnoreCase("chest")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("chestsql.use")) {
					if (args.length < 2)	{
						String chestType = null;
						Inventory inventory = null;
						String chestName = null;
						if (args.length < 1) {
							if (sender.hasPermission("chestsql.self")) {
								chestType = "Self";
								chestName = sender.getName();
								if (sender.hasPermission("chestsql.self.vip")) {
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
<<<<<<< HEAD
							inventory = doChest.loadChest(chestType, chestName.toLowerCase(), inventory);
							if (inventory != null) {
								((HumanEntity) sender).openInventory(inventory);
=======
							try {
								Statement statement = doSQL.connection.createStatement();
								inventory = doChest.loadChest(isChest, inventory, chestName.toLowerCase(), statement, sender);
								if (inventory != null) {
									((HumanEntity) sender).openInventory(inventory);
								}
								else {
									sender.sendMessage("指定箱子已被他人载入或载入失败");
									return false;
								}
>>>>>>> dc5b37057883b2d534683384440dca7003ce7b6b
							}
							else {
								sender.sendMessage("指定箱子已被他人载入或载入失败");
								return false;
							}								
						}
					}
					else {
						getLogger().info("指令参数过多");
						return false;
						}
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
			if (title[1].equals("私有"))
				chestType = "Self";
			else
				chestType = "Public";
			String chestName = title[2];
			if (doSQL.openConnect()) {				
				Inventory inventory = event.getInventory();
				Player player = (Player) event.getPlayer();
<<<<<<< HEAD
				if (doChest.saveChest(chestType, chestName, inventory))
					player.sendMessage("远程箱子已保存");
				else
					getLogger().info("远程箱子保存失败");					
=======
				Statement statement;
				try {
					statement = doSQL.connection.createStatement();
					if (doChest.saveChest(isChest, inventory, chestName.toLowerCase(), statement)) 
						player.sendMessage("远程箱子已保存");
					else
						getLogger().info("远程箱子保存失败");
					statement.close();
				} 
				catch (SQLException e) {}
>>>>>>> dc5b37057883b2d534683384440dca7003ce7b6b
				}
			}
		}
	
	public static Plugin getPlugin() {		
		return plugin;
	}
	
}