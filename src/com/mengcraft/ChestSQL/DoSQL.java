package com.mengcraft.ChestSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.Plugin;

public class DoSQL {
	public static Connection connection;

	String[] getSQLConfig() {
		Plugin plugin = ChestSQL.plugin;
		String[] sqlConfig = { plugin.getConfig().getString("mysql.host"),
				plugin.getConfig().getString("mysql.port"),
				plugin.getConfig().getString("mysql.database"),
				plugin.getConfig().getString("mysql.username"),
				plugin.getConfig().getString("mysql.password") };
		return sqlConfig;
	}

	Boolean getConnect() {
		if (connection != null) {
			try {
				if (connection.isClosed()) {
					connection = null;
					return false;
				} else {
					return true;
				}
			} catch (SQLException e) {
			}
		} else {
			return false;
		}
		return false;
	}

	Boolean closeConnect() {
		if (getConnect()) {
			try {
				connection.close();
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}

	Boolean openConnect() {
		if (getConnect()) {
			return true;
		} else {
			String[] sqlConfig = getSQLConfig();
			String addr = sqlConfig[0];
			String port = sqlConfig[1];
			String data = sqlConfig[2];
			String user = sqlConfig[3];
			String pass = sqlConfig[4];
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + addr
						+ ":" + port + "/" + data, user, pass);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}

	Boolean createTables() {
		if (openConnect()) {
			try {
				Statement statement = connection.createStatement();
				String[] sql = {
						"CREATE TABLE IF NOT EXISTS PrivateChest "
								+ "(Id int NOT NULL AUTO_INCREMENT, ChestName text, Locked int NOT NULL, Inventory text, PRIMARY KEY (Id));",
						"CREATE TABLE IF NOT EXISTS PublicChest "
								+ "(Id int NOT NULL AUTO_INCREMENT, ChestName text, Locked int NOT NULL, Inventory text, PRIMARY KEY (Id));" };
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
