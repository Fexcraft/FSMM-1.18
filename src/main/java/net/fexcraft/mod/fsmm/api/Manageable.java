package net.fexcraft.mod.fsmm.api;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSource;

/**
 * Internal Usage Class, do not bother with.
 * 
 * @author Ferdinand Calo' (FEX___96)
 */

public interface Manageable {
	
	public void modifyBalance(Action action, long amount, CommandSource log);
	
	public static enum Action {
		ADD, SUB, SET;
	}
	
}