package blob.blobapi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancements;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentDurability;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.WorldGenStage.Decoration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.StructureSettingsFeature;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureVillageConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableRegistry;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;


public class Api {

	//world gen
	
	public WorldGenFeatureDefinedStructurePoolTemplate registerJigSawPool(WorldGenFeatureDefinedStructurePoolTemplate var0) {
		MinecraftServer serv = ((CraftServer)Bukkit.getServer()).getServer();
		IRegistryCustom irc = serv.aV();
		IRegistry<WorldGenFeatureDefinedStructurePoolTemplate> irw = irc.d(IRegistry.aQ);
		return IRegistry.a(irw, var0.b(), var0);
	}
	
	public void registerStructure(NamespacedKey key,StructureGenerator<WorldGenFeatureVillageConfiguration> structure,WorldGenFeatureVillageConfiguration config, Decoration decorationtype, World world, List<ResourceKey<BiomeBase>> biomes, StructureSettingsFeature settings) {
		try {
			ChunkGenerator c = ((CraftWorld)world).getHandle().k().g();
			StructureSettings set = c.d();
			
			Field eField = StructureSettings.class.getDeclaredField("e");
			eField.setAccessible(true);
			
			//structure boime override
			StructureFeature<?, ? extends StructureGenerator<?>> configed = new StructureFeature<WorldGenFeatureVillageConfiguration, StructureGenerator<WorldGenFeatureVillageConfiguration>>(structure, config);
			
			HashMap<StructureGenerator<?>, ImmutableMultimap.Builder<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> arg2 = new HashMap<>();
			registerBiomes(arg2, configed, biomes);
			ImmutableMap<? extends StructureGenerator<?>, ? extends ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> toadd = arg2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((ImmutableMultimap.Builder<StructureFeature<?, ?>, ResourceKey<BiomeBase>>)$$0.getValue()).build()));
			
			
			HashMap<StructureGenerator<?>, ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> strucconf = new HashMap<StructureGenerator<?>, ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>>>();
			strucconf.putAll(toadd);
			strucconf.putAll((Map<? extends StructureGenerator<?>, ? extends ImmutableMultimap<StructureFeature<?, ?>, ResourceKey<BiomeBase>>>) eField.get(set));
			
			eField.set(set, ImmutableMap.copyOf(strucconf));
			
			for(Entry<StructureFeature<?, ?>, Collection<ResourceKey<BiomeBase>>> ent : set.b(structure).asMap().entrySet()) {
				BlobAPI.Instance.getLogger().log(Level.INFO, ent.getKey().toString());
				for (ResourceKey<BiomeBase> k : ent.getValue()) {
					BlobAPI.Instance.getLogger().log(Level.INFO, k.toString());
				}
			}
			
			
			
			//structure settings override
			set.a().put(structure, settings);
			
			//structure registry
			Class<?>[] args = new Class[3];
			args[0] = String.class;
			args[1] = StructureGenerator.class;
			args[2] = WorldGenStage.Decoration.class;
			Method aMeth = StructureGenerator.class.getDeclaredMethod("a", args);
			aMeth.setAccessible(true);
			aMeth.invoke(null, key.toString(), structure, decorationtype);
		   	
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	private void registerBiomes(HashMap<StructureGenerator<?>, Builder<StructureFeature<?, ?>, ResourceKey<BiomeBase>>> arg2, StructureFeature<?, ?> $$1, List<ResourceKey<BiomeBase>> $$2) {
		arg2.put($$1.d, new ImmutableMultimap.Builder<StructureFeature<?, ?>, ResourceKey<BiomeBase>>());
		for (ResourceKey<BiomeBase> k : $$2) {
			arg2.get($$1.d).put($$1, k);
		}
	}
	
	public void addLootTable(NamespacedKey key, LootTable table) {
		MinecraftServer serv = ((CraftServer)Bukkit.getServer()).getServer();
		LootTableRegistry lt = serv.aG();
		
		try {
			Field mapField = LootTableRegistry.class.getDeclaredField("lootTableToKey");
			mapField.setAccessible(true);
			
			ImmutableMap.Builder<MinecraftKey, LootTable> builder = ImmutableMap.builder();
			MinecraftKey mk = new MinecraftKey(key.toString());
			
			builder.put(mk, table);
			builder.putAll((Map<? extends MinecraftKey, ? extends LootTable>) mapField.get(lt));
			
			mapField.set(lt, builder.build());
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
	}
	
	//custom advancements
	public void addAdvancement(NamespacedKey key, Advancement.SerializedAdvancement a) {
		MinecraftServer serv = ((CraftServer)Bukkit.getServer()).getServer();		
		Advancements al = serv.ax().c;
		HashMap<MinecraftKey, Advancement.SerializedAdvancement> map = new HashMap<MinecraftKey, Advancement.SerializedAdvancement>();
		map.put(new MinecraftKey(key.toString()), a);
		
		al.a(map);
	}
	
	public void addAdvancements(NamespacedKey key, HashMap<MinecraftKey, Advancement.SerializedAdvancement> map) {
		MinecraftServer serv = ((CraftServer)Bukkit.getServer()).getServer();		
		Advancements al = serv.ax().c;
		al.a(map);
	}
	
	public Advancement getAdvancement(NamespacedKey key) {
		MinecraftServer serv = ((CraftServer)Bukkit.getServer()).getServer();		
		Advancements al = serv.ax().c;
		return al.a(new MinecraftKey(key.toString()));
	}
	
	public void grandAdvancement(Player play, NamespacedKey key) {
		EntityPlayer p = ((CraftPlayer)play).getHandle();
		AdvancementDataPlayer advdata = p.M();
		Advancement ad = this.getAdvancement(key);
		AdvancementProgress prog = advdata.b(ad);
		for (String s : prog.e()) {
			advdata.a(ad, s);
		}
	}
	//enchantlib
	
	private ArrayList<Enchantment> CustomEnch = new ArrayList<Enchantment>();
	private HashMap<Enchantment, String> NameMap = new HashMap<Enchantment, String>();
	
	public void registerEnchants(NamespacedKey key, Enchantment ench, String name) {
	    try{
	        try {
	            Field f = org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew");
	            f.setAccessible(true);
	            f.set(null, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	        	IRegistry.a(IRegistry.Y, key.toString(), ench);
	        	org.bukkit.enchantments.Enchantment.registerEnchantment((org.bukkit.enchantments.Enchantment)new CraftEnchantment(ench)); 
	            NameMap.put(ench, name);
	            CustomEnch.add(ench);
	            
	            BlobAPI.Instance.getLogger().log(Level.INFO, "Registered enchantment " + ench.toString());
	            BlobAPI.Instance.getLogger().log(Level.INFO, "Registered key " + key.toString());
	        } catch (IllegalArgumentException e){
	        	e.printStackTrace();
	        }
	    }catch(Exception e){
	        e.printStackTrace();
	    }

	}
	
	
	public Boolean isCustomEnchant(Enchantment ench) {
		if (CustomEnch.contains(ench)) return true;
		return false;
	}
	
	public ItemMeta ApplyCustomLore(ItemMeta meta, Map<org.bukkit.enchantments.Enchantment, Integer> enchs) {
		ArrayList<String> lore = new ArrayList<String>();
		for (Enchantment ce : CustomEnch) {
			org.bukkit.enchantments.Enchantment e = getBukkitEnchant(ce);
			if (enchs.containsKey(e)) {
				ChatColor c = ChatColor.GRAY;
				if (ce.c()) c = ChatColor.RED;
				if (ce.a() == 1) {
					lore.add(c + NameMap.get(ce));
				} else {
					lore.add(c + NameMap.get(ce) + " " + new TranslatableComponent("enchantment.level." + enchs.get(e)).toPlainText());
				}
			}
		}
		if (meta.hasLore()) {
			List<String> tosort = meta.getLore();
			ArrayList<String> toRemove = new ArrayList<String>();
			for (String s : tosort) {
				for (Enchantment ce : CustomEnch) {
					if (s.contains(NameMap.get(ce))) {
						toRemove.add(s);
					}
				}
			}
			tosort.removeAll(toRemove);
			lore.addAll(tosort);
		}
		meta.setLore(lore);
		return meta;
	}
	
	public void damageItem(int i, ItemStack item) {
		int j;

        if (i > 0) {
            j = item.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY);
            int k = 0;

            for (int l = 0; j > 0 && l < i; ++l) {
                if (EnchantmentDurability.a(CraftItemStack.asNMSCopy(item), j, new Random())) {
                    ++k;
                }
            }

            i -= k;
            if (i <= 0) {
                return;
            }
        }
        
        Damageable meta = ((Damageable)item.getItemMeta());

        j = meta.getDamage() + i;
        meta.setDamage(j);
        item.setItemMeta(meta);
	}
	
	public ArrayList<Enchantment> getEnchants() {
		return this.CustomEnch;
	}
	
	public org.bukkit.enchantments.Enchantment getBukkitEnchant(Enchantment e) {
		return new CraftEnchantment(e);
	}
	
	public EntityCreature getNSMEntity(LivingEntity e) {
		return (EntityCreature) ((EntityInsentient)((CraftEntity)e).getHandle());
	}
	
	public void overrideGoals(LivingEntity e, Map<PathfinderGoal, Integer> goals, Map<PathfinderGoal, Integer> targets) {
		EntityCreature c = (EntityCreature) ((EntityInsentient)((CraftEntity)e).getHandle());
		try {
		     Field dField = PathfinderGoalSelector.class.getDeclaredField("d");
		     dField.setAccessible(true);
		     dField.set(c.bR, Sets.newLinkedHashSet());
		     dField.set(c.bS, Sets.newLinkedHashSet());
		 } catch (Exception exc) {exc.printStackTrace();}
		 for (Entry<PathfinderGoal, Integer> eset : goals.entrySet()) {
			 c.bR.a(eset.getValue(), eset.getKey());
		 }
		 for (Entry<PathfinderGoal, Integer> eset : targets.entrySet()) {
			 c.bS.a(eset.getValue(), eset.getKey());
		 }
	}
	
	public void addAttributes(LivingEntity e, AttributeBase b, double value) {
		EntityCreature c = (EntityCreature) ((EntityInsentient)((CraftEntity)e).getHandle());
		try {
			 Field bQField = EntityLiving.class.getDeclaredField("bQ");
	    	 bQField.setAccessible(true);
	    	 AttributeMapBase base = (AttributeMapBase) bQField.get(c);
		     Field bField = AttributeMapBase.class.getDeclaredField("b");
		     bField.setAccessible(true);
	    	 @SuppressWarnings("unchecked")
			 Map<AttributeBase, AttributeModifiable> bmap = (Map<AttributeBase, AttributeModifiable>) bField.get(base);
	    	 if (bmap.containsKey(b)) {
	    		 bmap.get(b).a(value);
	    	 } else {
		    	 AttributeModifiable m = new AttributeModifiable(b, mod -> {});
		    	 m.a(value);
			     bmap.put(b, m);
	    	 }
		 } catch (Exception exc) {exc.printStackTrace();}
	}
	
	public Optional<DefinedStructure> GetStructure(InputStream var2, DefinedStructureManager var5) {
	        Optional<DefinedStructure> optional;
			try {
				optional = Optional.of(strucfromstream(var2, var5));
				var2.close();
				return optional;
			} catch (IOException e) {
				e.printStackTrace();
				return Optional.absent();
			}
	  }
	  
	  private DefinedStructure strucfromstream(InputStream var0, DefinedStructureManager var2) throws IOException {
	    NBTTagCompound var1 = NBTCompressedStreamTools.a(var0);
	    return var2.a(var1);
	  }
}
