package com.mengcraft.chestsql;

import java.sql.Connection;
import java.sql.DriverManager;
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
	static Connection connection;
	Chest chest = new Chest();
	
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
		if (!this.getConfig().getBoolean("use"))
			setEnabled(false);		
		if (openConnect()) {
			getLogger().info("连接成功");
			if (createTable()) {
				getLogger().info("检验成功");
				getLogger().info("开发者: min梦梦");
				getLogger().info("服务器出租店: http://shop105595113.taobao.com");
			}
			else {
				getLogger().info("检验失败");
				if (closeConnect()) {
					getLogger().info("关闭连接");
				}
				setEnabled(false);
			}
		}
		else {
			getLogger().info("连接失败");
			if (closeConnect()) {
				getLogger().info("关闭连接");
			}
			setEnabled(false);
		}
	}
	
	@Override
	public void onDisable()
	{
		HandlerList.unregisterAll((Plugin) this);
		if (openConnect())
			if (closeConnect())
				getLogger().info("关闭连接");
		getLogger().info("开发者: min梦梦");
		getLogger().info("服务器出租店: http://shop105595113.taobao.com");
	}
	
	@Override
	public boolean onCommand(CommandSender sender,Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("chest")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("chestsql.use")) {
					if (args.length < 2)	{
						boolean isSelfChest = false;
						Inventory inventory = null;
						String chestName = null;
						if (args.length < 1) {
							if (sender.hasPermission("chestsql.self")) {
								isSelfChest = true;
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
								isSelfChest = false;
								inventory = getServer().createInventory(null, 45, "远程箱子·公共·" + chestName);
							}
							else {
								sender.sendMessage("你没有使用公共箱子" + chestName + "的权限");
								return false;
							}
						}
						if (openConnect()) {
							try {
								Statement statement = connection.createStatement();
								inventory = chest.loadChest(isSelfChest, inventory, chestName, statement, sender);
								if (inventory != null) {
									((HumanEntity) sender).openInventory(inventory);
								}
							}
							catch (SQLException e) {
								sender.sendMessage("打开远程箱子失败");
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
		String title[] = event.getInventory().getTitle().split("·");
		if (title[0].equals("远程箱子")) {
			boolean isSelfChest;
			if (title[1].equals("私有"))
				isSelfChest = true;
			else
				isSelfChest = false;
			String chestName = title[2];
			if (openConnect()) {				
				Inventory inventory = event.getInventory();
				Player player = (Player) event.getPlayer();
				Statement statement;
				try {
					statement = connection.createStatement();
					if (chest.saveChest(isSelfChest, inventory, chestName, statement)) 
						player.sendMessage("远程箱子已保存");
					else
						getLogger().info("远程箱子保存失败");
					statement.close();
				} 
				catch (SQLException e) {}
				}
			}
		}
	
	boolean closeConnect()
	{
		boolean connectStatus = getConnect();
		if (connectStatus) {
			try {
				connection.close();
			}
	    	catch(Exception exception) {
	    		connectStatus = false;
	    	}
		}
		else {
			connectStatus = true;
		}
		return connectStatus;
	}
	
	boolean openConnect()
	{
		boolean connectStatus = getConnect();
		if(!connectStatus) {
			String address = this.getConfig().getString("address");
			String port = this.getConfig().getString("port");
			String dataBase = this.getConfig().getString("dataBase");
			String userName = this.getConfig().getString("userName");
			String passWord = this.getConfig().getString("passWord");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + dataBase, userName, passWord);
				connectStatus = true;
			}
			catch(Exception exception) {
				connection = null;
			}
		}
		return connectStatus;
	}
	
	boolean getConnect()
	{
		boolean closeStatus = true;
		boolean ConnectStatus = false;
		if(connection != null) {
			try {
				closeStatus = connection.isClosed();
			}
	    	catch(Exception exception) {}
			if(closeStatus) {
				connection = null;
			}
			else {
				ConnectStatus = true;
			}
		}
		return ConnectStatus;
	}
	
	boolean createTable()
	{
		boolean CreateStatus = false;
    	try {
    		if(openConnect())
    		{
    			Statement statement = connection.createStatement();
    			String sqlOrder = "CREATE TABLE IF NOT EXISTS PrivateChest(ChestName text,Inventory text);";
    			statement.executeUpdate(sqlOrder);
    			sqlOrder = "CREATE TABLE IF NOT EXISTS PublicChest(ChestName text,Inventory text);";
    			statement.executeUpdate(sqlOrder);
    			statement.close();
    			CreateStatus = true;
    		}
    	}
    	catch(Exception exception) {}
    	return CreateStatus;
    }
	
}