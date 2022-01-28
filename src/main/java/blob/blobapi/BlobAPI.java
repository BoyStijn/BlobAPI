package blob.blobapi;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlobAPI extends JavaPlugin {
	
	public static JavaPlugin Instance;
	public static Api API;
	
	@Override
	public void onEnable() {
		Instance = this;
		API = new Api();
		
		this.getServer().getPluginManager().registerEvents(new MainListener(), this);
        
        BukkitRunnable br = new BukkitRunnable() {

			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					for (ItemStack i : p.getInventory().getContents()) {
						if (i == null) continue;
						if (i.getType() == Material.ENCHANTED_BOOK) {
							i.setItemMeta(API.ApplyCustomLore(i.getItemMeta(), ((EnchantmentStorageMeta)i.getItemMeta()).getStoredEnchants()));
						} else {
							i.setItemMeta(API.ApplyCustomLore(i.getItemMeta(), i.getEnchantments()));
						}
					}
				}
			}
        	
        };
        br.runTaskTimer(Instance, 0, 7);
	}
}
