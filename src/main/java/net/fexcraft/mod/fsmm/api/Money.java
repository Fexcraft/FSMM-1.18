package net.fexcraft.mod.fsmm.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface Money {

	public long getWorth();
	
	public ItemStack getItemStack();

	public ResourceLocation getRegistryName();
	
	//
	
	public static interface Item {
		
		public Money getType();
		
		/** Singular worth, do not multiply by count! **/
		public long getWorth(ItemStack stack);
		
	}

}
