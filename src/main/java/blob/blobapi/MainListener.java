package blob.blobapi;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.Material; 

public class MainListener implements Listener {
	
	@EventHandler
    public void onInv(InventoryOpenEvent event) {
		BlobAPI.Instance.getLogger().log(Level.INFO, "OpenEvent");
		ItemStack[] top = event.getView().getTopInventory().getContents();
		ItemStack[] bottom = null;
		if (event.getView().getBottomInventory() != null) bottom =  event.getView().getBottomInventory().getContents();
		
		for (ItemStack i : top) {
			if (i == null) continue;
			if (i.getType() == Material.ENCHANTED_BOOK) {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), ((EnchantmentStorageMeta)i.getItemMeta()).getStoredEnchants()));
			} else {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), i.getEnchantments()));
			}
		}
		
		if (bottom != null)
			for (ItemStack i : bottom) {
				if (i == null) continue;
				if (i.getType() == Material.ENCHANTED_BOOK) {
					i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), ((EnchantmentStorageMeta)i.getItemMeta()).getStoredEnchants()));
				} else {
					i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), i.getEnchantments()));
				}
			}
	}
	
	
	@EventHandler
    public void onInv(PrepareSmithingEvent event) {
		BlobAPI.Instance.getLogger().log(Level.INFO, "SmithEvent");
		ItemStack i = event.getResult();
		
		if (i != null) {
			if (i.getType() == Material.ENCHANTED_BOOK) {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), ((EnchantmentStorageMeta)i.getItemMeta()).getStoredEnchants()));
			} else {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), i.getEnchantments()));
			}
			event.setResult(i);
		}
	}
	
	@EventHandler
    public void onInv(PrepareAnvilEvent event) {
		BlobAPI.Instance.getLogger().log(Level.INFO, "AnvilEvent");
		ItemStack i = event.getResult();
		
		if (i != null) {
			if (i.getType() == Material.ENCHANTED_BOOK) {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), ((EnchantmentStorageMeta)i.getItemMeta()).getStoredEnchants()));
			} else {
				i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), i.getEnchantments()));
			}
			event.setResult(i);
		}
	}
	
	@EventHandler
    public void onInv(EnchantItemEvent event) {
		BlobAPI.Instance.getLogger().log(Level.INFO, "AnvilEvent");
		ItemStack i = event.getItem();
		
		if (i != null) {
			i.setItemMeta(BlobAPI.API.ApplyCustomLore(i.getItemMeta(), event.getEnchantsToAdd()));
		}
	}
}
