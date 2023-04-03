package net.fexcraft.mod.fsmm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import net.fexcraft.mod.fsmm.api.Money;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GenericMoneyItem extends Item implements Money.Item {

	public static final ArrayList<GenericMoneyItem> sorted = new ArrayList<>();
	private final Money type;
	
	public GenericMoneyItem(Money money){
		super(new Properties().stacksTo(50));
		type = money;
		sorted.add(this);
		//TODO creative tab
	}

	public static void sort(){
		Collections.sort(sorted, new Comparator<GenericMoneyItem>(){
			@Override
			public int compare(GenericMoneyItem o1, GenericMoneyItem o2){
				if(o1.type.getWorth() == o2.type.getWorth()) return o1.getRegistryName().compareTo(o2.getRegistryName());
				return o1.type.getWorth() > o2.type.getWorth() ? -1 : 1;
			}
	    });
	}

	@Override
	public Money getType(){
		return type;
	}

	@Override
	public long getWorth(ItemStack stack){
		return type.getWorth()/* * stack.getCount()*/;
	}
	
}