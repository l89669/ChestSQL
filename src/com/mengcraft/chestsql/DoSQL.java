package com.mengcraft.chestsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.Plugin;

public class DoSQL {
	public static Connection connection;
	
	String[] getSQLConfig()
	{
		Plugin plugin = Main.getPlugin();
		String[] sqlConfig = {
				plugin.getConfig().getString("mysql.addr"),
				plugin.getConfig().getString("mysql.port"),
				plugin.getConfig().getString("mysql.data"),
				plugin.getConfig().getString("mysql.user"),
				plugin.getConfig().getString("mysql.pass")
		};
			return sqlConfig;
	}
	
	Boolean getConnect()
	{
		if (connection != null) {
			try {
				if (connection.isClosed()) {
					connection = null;
					return false;
				}
				else {
					return true;
				}
			}
			catch (SQLException e) {
			}
		}
		else {
			return false;
		}
		return false;
	}
	
	Boolean closeConnect()
	{
		if (getConnect()) {
			try {
				connection.close();
				return true;
			} 
			catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	Boolean openConnect() 
	{
		if (getConnect()) {
			return true;
		}
		else {
			String [] sqlConfig = getSQLConfig();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + sqlConfig[0] + ":" + sqlConfig[1] + "/" + sqlConfig[2], sqlConfig[3], sqlConfig[4]);
				return true;
			}
			catch (Exception e) {
				return false;
			}
		}
	}
	
	Boolean createTables()
	{
		if (openConnect()) {
			try {
				Statement statement = connection.createStatement();
				String [] sql = {
						"CREATE TABLE IF NOT EXISTS SelfChest "
						+ "(Id int NOT NULL AUTO_INCREMENT, ChestName text, Locked int NOT NULL, Inventory text, PRIMARY KEY (Id));", 
						"CREATE TABLE IF NOT EXISTS PublicChest "
						+ "(Id int NOT NULL AUTO_INCREMENT, ChestName text, Locked int NOT NULL, Inventory text, PRIMARY KEY (Id));"
						};
    			statement.executeUpdate(sql[0]);
    			statement.executeUpdate(sql[1]);
    			statement.close();
    			return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return null;
	}
	

}
