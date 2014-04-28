package com.mengcraft.chestsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Chest {
	Tools tools = new Tools();
	
	public Inventory loadChest(String isChest,Inventory inventory,String chestName,Statement statement,CommandSender sender)
	{
		try {
			String sql = "SELECT Locked, Inventory FROM " + isChest + "Chest WHERE ChestName = '" + chestName + "';";
			ResultSet result = statement.executeQuery(sql);
			if(result.last())	{
				int Locked = result.getInt(1);
				if (Locked > 0) {
					return null;
				}
				String inventoryData = result.getString(2);
					try {
						sql = "UPDATE " + isChest + "Chest SET Locked = 1 WHERE ChestName = '" + chestName	+ "';";
						statement.executeUpdate(sql);
					}
					catch (Exception e) {
						return null;
					}
					String[] itemDatas = tools.SplitText(inventoryData, ";");
					int size;
					if (inventory.getSize() > itemDatas.length) {
						size = itemDatas.length;    		
					}
					else {
						size = inventory.getSize();
					}
					ItemStack[] itemStacks = new ItemStack[size];
					for(int i = 0; i < size; i++) {
						if(itemDatas[i].length() > 0) {
							String[] itemData = tools.SplitText(itemDatas[i], "/");
							if(itemData.length == 5) {
								int itemType = Integer.parseInt(itemData[0]);
								int itemAmount = Integer.parseInt(itemData[1]);
								short itemDurable = Short.parseShort(itemData[2]);
								String metaDisplayName = tools.hexToText(itemData[3]);
								String itemEnchant = itemData[4];
								Material materialType = Material.getMaterial(itemType);
								ItemStack itemStack = new ItemStack(materialType, itemAmount, itemDurable);
								ItemMeta itemMeta = itemStack.getItemMeta();
								if(metaDisplayName.length() > 0){
									itemMeta.setDisplayName(metaDisplayName);
								}
								if(itemEnchant.length() > 0) {
									String[] enchantArray = tools.SplitText(itemEnchant,"#");
									for(int j = 0; j < enchantArray.length; j++) {
										String[] enchantData = tools.SplitText(enchantArray[j],"_");
										if(enchantData.length == 2) {
											int enchantType = Integer.parseInt(enchantData[0]);
											int enchantLevel = Integer.parseInt(enchantData[1]);
											Enchantment enchantmentType = Enchantment.getById(enchantType);
											itemMeta.addEnchant(enchantmentType, enchantLevel, false);
											}
										}
									}
								itemStack.setItemMeta(itemMeta);
								if(i < size)
									itemStacks[i] = itemStack;		       					
								}
							}
						}		
					inventory.setContents(itemStacks);
					return inventory;
					}
			else {
				try {
					sql = "INSERT INTO " + isChest + "Chest(ChestName, Locked) VALUES('" + chestName + "', 1);";
					statement.executeUpdate(sql);
					} 
				catch (Exception e) {
					return null;
					}
				return inventory;
				}
			}
		catch (SQLException e) {
			return null;
			}		
		}
	
	public Boolean saveChest(String isChest,Inventory inventory,String chestName,Statement statement)
	{
		StringBuilder inventoryDateBuilder = new StringBuilder();
		for (int i = 0; i < inventory.getSize(); i++) {
			if (i > 0) inventoryDateBuilder.append(";");
			String itemData = "";
			ItemStack itemStack = inventory.getItem(i);
			if (itemStack != null) {
				StringBuilder itemDataBuilder = new StringBuilder();
				int itemType = itemStack.getTypeId();
				itemDataBuilder.append(Integer.toString(itemType));
				itemDataBuilder.append("/");
				int itemAmount = itemStack.getAmount();
				itemDataBuilder.append(Integer.toString(itemAmount));
				itemDataBuilder.append("/");
				short itemDurable = itemStack.getDurability();
				itemDataBuilder.append(Short.toString(itemDurable));
				itemDataBuilder.append("/");
				ItemMeta itemMeta = itemStack.getItemMeta();
				String metaDisplayName = tools.textToHex(itemMeta.getDisplayName());
				itemDataBuilder.append(metaDisplayName);
				itemDataBuilder.append("/");
				Map<Enchantment, Integer> enchant = itemMeta.getEnchants();
				String itemEnchant = "";
				if(enchant.size() > 0) {
					StringBuilder enchantBuilder = new StringBuilder();
					Object[] enchantObject = enchant.entrySet().toArray();
					for(int j = 0; j < enchant.size(); j++) {
						if(j > 0) enchantBuilder.append("#");
						Entry enchantEntry = (Entry)(enchantObject[j]);
						Enchantment enchantTypeObject = (Enchantment)(enchantEntry.getKey());
						int enchantType = enchantTypeObject.getId();
						int enchantLevel = (int) enchantEntry.getValue();
						enchantBuilder.append(Integer.toString(enchantType) + "_" + Integer.toString(enchantLevel));
						}
					enchantBuilder.toString();
					}
				itemDataBuilder.append(itemEnchant);
				itemData = itemDataBuilder.toString();
				}
			inventoryDateBuilder.append(itemData);
			}
		String inventoryData = inventoryDateBuilder.toString();	
		try {
			String sql = "UPDATE " + isChest	+ "Chest SET Locked = 0, Inventory = '" + inventoryData	+ "' WHERE ChestName = '" + chestName + "';";
			statement.executeUpdate(sql);
			return true;
			}
		catch (SQLException e) {
				return false;
				}
		}
	}
