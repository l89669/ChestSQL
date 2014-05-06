package com.mengcraft.ChestSQL;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.utility.StreamSerializer;

public class DoChest {
	DoSQL doSQL = new DoSQL();

	public Boolean saveChest(String chestType, String chestName,
			Inventory inventory) {
		if (!doSQL.openConnect()) {
			return false;
		}
		ItemStack[] itemStacks = inventory.getContents();
		StringBuilder inventoryDataBuilder = new StringBuilder();
		for (int i = 0; i < inventory.getSize(); i++) {
			if (i > 0) {
				inventoryDataBuilder.append(";");
			}
			try {
				if (itemStacks[i] != null && itemStacks[i].getType() != Material.AIR) {
					inventoryDataBuilder.append(StreamSerializer.getDefault()
							.serializeItemStack(itemStacks[i]));
				} else {
					continue;
				}
			} catch (IOException e) {
				return false;
			}
		}
		try {
			Statement statement = DoSQL.connection.createStatement();
			statement.executeUpdate("UPDATE " + chestType
					+ "Chest SET Locked = 0, Inventory = '"
					+ inventoryDataBuilder.toString() + "' WHERE ChestName = '"
					+ chestName + "';");
			statement.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public Inventory loadChest(String chestType, String chestName,
			Inventory inventory) {
		if (!doSQL.openConnect()) {
			return null;
		}
		try {
			Statement statement = DoSQL.connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT Locked, Inventory FROM " + chestType
							+ "Chest " + "WHERE ChestName = '" + chestName
							+ "';");
			if (result.last()) {
				int Locked = result.getInt(1);
				if (Locked > 0) {
					return null;
				} else {
					lockChest(chestType, chestName);
				}
				String inventoryData = result.getString(2);
				if (inventoryData == null) {
					return inventory;
				}
				String[] itemDatas = inventoryData.split(";");
				int size;
				if (itemDatas.length < inventory.getSize()) {
					size = itemDatas.length;
				} else {
					size = inventory.getSize();
				}
				ItemStack[] itemStacks = new ItemStack[size];
				for (int i = 0; i < size; i++) {
					if (!itemDatas[i].equals("")) {
						itemStacks[i] = StreamSerializer.getDefault()
								.deserializeItemStack(itemDatas[i]);
					} else {
						continue;
					}
				}
				inventory.setContents(itemStacks);
				statement.close();
				return inventory;
			} else {
				statement.executeUpdate("INSERT INTO " + chestType
						+ "Chest(ChestName, Locked, Inventory) VALUES('"
						+ chestName + "', 1, ';');");
				statement.close();
				return inventory;
			}
		} catch (SQLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public Boolean unlockChest(String chestType, String chestName) {
		if (doSQL.openConnect()) {
			try {
				Statement statement = DoSQL.connection.createStatement();
				statement.executeUpdate("UPDATE " + chestType
						+ "Chest SET Locked = 0 WHERE ChestName = '"
						+ chestName + "';");
				statement.close();
				return true;
			} catch (SQLException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public Boolean lockChest(String chestType, String chestName) {
		if (doSQL.openConnect()) {
			try {
				Statement statement = DoSQL.connection.createStatement();
				statement.executeUpdate("UPDATE " + chestType
						+ "Chest SET Locked = 1 WHERE ChestName = '"
						+ chestName + "';");
				statement.close();
				return true;
			} catch (SQLException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public Boolean saveAllChest() {
		Plugin plugin = ChestSQL.plugin;
		boolean b = true;
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			String[] titles = player.getOpenInventory().getTitle().split("·");
			if (titles[0].equals("远程箱子")) {
				String chestType;
				String chestName = titles[2];
				Inventory inventory = player.getOpenInventory()
						.getTopInventory();
				player.closeInventory();
				if (titles[1].equals("私有")) {
					chestType = "Private";
				} else {
					chestType = "Public";
				}
				player.sendMessage("远程箱子插件被禁用");
				if (!saveChest(chestType, chestName, inventory)) {
					b = false;
				}
			}
		}
		return b;
	}

}
