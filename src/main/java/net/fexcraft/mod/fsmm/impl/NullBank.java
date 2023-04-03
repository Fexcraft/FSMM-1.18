package net.fexcraft.mod.fsmm.impl;

import java.util.TreeMap;

import com.google.gson.JsonObject;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.fsmm.api.Account;
import net.fexcraft.mod.fsmm.api.Bank;
import net.minecraft.commands.CommandSource;

/** Return instance of this, incase no bank found, to prevent NPE's in generic code without nullchecks **/
public class NullBank extends Bank {
	
	public static final NullBank INSTANCE = new NullBank("null", "Generic Null Bank", 0, null, null);

	public NullBank(String id, String name, long balance, JsonObject data, TreeMap<String, String> map){
		super(id, name, balance, data, map);
	}

	@Override
	public boolean processAction(Action action, CommandSource log, Account sender, long amount, Account receiver, boolean fee_included){
		Print.chat(log, "BANK NOT FOUND >>> NULL BANK;"); return false;
	}

	@Override
	public boolean isNull(){
		return true;
	}

}
