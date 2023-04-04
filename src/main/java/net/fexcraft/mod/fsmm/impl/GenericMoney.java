package net.fexcraft.mod.fsmm.impl;

import java.util.Optional;

import com.google.gson.JsonObject;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.mod.fsmm.FSMM;
import net.fexcraft.mod.fsmm.api.Money;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GenericMoney implements Money {

	private ResourceLocation regname;
	private ItemStack stack;
	private long worth;
	
	public GenericMoney(JsonObject obj, boolean internal){
		regname = new ResourceLocation((internal ? "fsmm:" : "") + JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + Time.getDate()));
		worth = JsonUtil.getIfExists(obj, "worth", -1).longValue();
		int meta = JsonUtil.getIfExists(obj, "meta", -1).intValue();
		if(meta >= 0 && !internal){ regname = new ResourceLocation(regname.toString() + "_" + meta); }
		if(!internal){
			stackload(null, obj, false);
		}
	}
	
	public void stackload(net.minecraft.world.item.Item item, JsonObject obj, boolean internal){
		if(item == null || !internal){
			String id = JsonUtil.getIfExists(obj, "id", "invalid_" + obj.toString() + "_" + Time.getDate());
			Optional<Holder<net.minecraft.world.item.Item>> holder = ForgeRegistries.ITEMS.getHolder(new ResourceLocation(internal ? "fsmm:" + id : id));
			if(holder.isEmpty()){
				FSMM.LOGGER.error("External Item with ID '" + regname.toString() + "' couldn't be found! This is bad!");
				Static.halt();
			}
		}
		CompoundTag compound = null;
		/*if(obj.has("nbt")){
			try{
				compound = JsonToNBT.getTagFromJson(obj.get("nbt").getAsString());
			}
			catch(NBTException e){
				FSMM.LOGGER.error("Could not load NBT from config of '" + regname.toString() + "'! This is bad!");
				Static.halt();
			}
		}*///TODO
		//
		stack = new ItemStack(item, 1);
		if(compound != null){ stack.setTag(compound); }
	}

	@Override
	public ResourceLocation getRegistryName(){
		return regname;
	}

	@Override
	public long getWorth(){
		return worth;
	}
	
	@Override
	public String toString(){
		return super.toString() + "#" + this.getWorth();
	}

	@Override
	public ItemStack getItemStack(){
		return stack;
	}
	
}