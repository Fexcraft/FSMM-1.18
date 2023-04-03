package net.fexcraft.mod.fsmm.util;

import java.beans.EventHandler;
import java.io.File;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.mod.fsmm.FSMM;
import net.fexcraft.mod.fsmm.api.Money;
import net.fexcraft.mod.fsmm.impl.GenericBank;
import net.fexcraft.mod.fsmm.impl.GenericMoney;
import net.fexcraft.mod.fsmm.impl.GenericMoneyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLPaths;

public class Config {

    private static String COMMA = ",", DOT = ".";
    private static String GENERAL = "General", DISPLAY = "Display/Logging";
    //
    public static int STARTING_BALANCE, UNLOAD_FREQUENCY, MIN_SEARCH_CHARS;
    public static String DEFAULT_BANK, CURRENCY_SIGN, THOUSAND_SEPARATOR;
    public static boolean NOTIFY_BALANCE_ON_JOIN, INVERT_COMMA, SHOW_CENTESIMALS, SHOW_DECIMALS, ENABLE_BANK_CARDS;
    public static boolean SHOW_ITEM_WORTH_IN_TOOLTIP = true, PARTIAL_ACCOUNT_NAME_SEARCH = true;
    private static JsonArray DEF_BANKS;
    //
    public static SyncableConfig LOCAL = new SyncableConfig(), REMOTE;
    /** Acts as a copy when disconnecting or connecting to a server. */
    public static class SyncableConfig {

        public int starting_balance, unload_frequency, min_search_chars;
        public String default_bank, currency_sign, thousand_separator;
        public boolean notify_balance_on_join, invert_comma, show_centesimals, show_decimals, enable_bank_cards;
        public boolean show_item_worth_in_tooltip = true, partial_account_name_search = true;

        public CompoundTag toTag(){
            CompoundTag compound = new CompoundTag();
            compound.putInt("starting_balance", starting_balance);
            compound.putInt("unload_frequency", unload_frequency);
            compound.putString("default_bank", default_bank);
            compound.putString("currency_sign", currency_sign);
            compound.putBoolean("notify_balance_on_join", notify_balance_on_join);
            compound.putBoolean("invert_comma", invert_comma);
            compound.putBoolean("show_centesimals", show_centesimals);
            compound.putBoolean("enable_bank_cards", enable_bank_cards);
            compound.putBoolean("show_item_worth_in_tooltip", show_item_worth_in_tooltip);
            compound.putBoolean("partial_account_name_search", partial_account_name_search);
            if(thousand_separator != null) compound.putString("thousand_separator", thousand_separator);
            compound.putBoolean("show_decimals", show_decimals);
            compound.putInt("min_search_chars", min_search_chars);
            return compound;
        }

        public static SyncableConfig fromTag(CompoundTag compound){
            SyncableConfig config = new SyncableConfig();
            config.starting_balance = compound.getInt("starting_balance");
            config.unload_frequency = compound.getInt("unload_frequency");
            config.default_bank = compound.getString("default_bank");
            config.currency_sign = compound.getString("currency_sign");
            config.notify_balance_on_join = compound.getBoolean("notify_balance_on_join");
            config.invert_comma = compound.getBoolean("invert_comma");
            config.show_centesimals = compound.getBoolean("show_centesimals");
            config.enable_bank_cards = compound.getBoolean("enable_bank_cards");
            config.show_item_worth_in_tooltip = compound.getBoolean("show_item_worth_in_tooltip");
            config.partial_account_name_search = compound.getBoolean("partial_account_name_search");
            config.thousand_separator = compound.contains("thousand_separator") ? compound.getString("thousand_separator") : null;
            config.show_decimals = compound.getBoolean("show_decimals");
            config.min_search_chars = compound.getInt("min_search_chars");
            return config;
        }

        public void apply(){
            STARTING_BALANCE = starting_balance;
            UNLOAD_FREQUENCY = unload_frequency;
            DEFAULT_BANK = default_bank;
            CURRENCY_SIGN = currency_sign;
            NOTIFY_BALANCE_ON_JOIN = notify_balance_on_join;
            INVERT_COMMA = invert_comma;
            SHOW_CENTESIMALS = show_centesimals;
            ENABLE_BANK_CARDS = enable_bank_cards;
            SHOW_ITEM_WORTH_IN_TOOLTIP = show_item_worth_in_tooltip;
            PARTIAL_ACCOUNT_NAME_SEARCH = partial_account_name_search;
            THOUSAND_SEPARATOR = thousand_separator;
            SHOW_DECIMALS = show_decimals;
            MIN_SEARCH_CHARS = min_search_chars;
        }
    }
    private static final TreeMap<String, Long> DEFAULT = new TreeMap<String, Long>();
    static {
        DEFAULT.put("1cent", 10l);
        DEFAULT.put("2cent", 20l);
        DEFAULT.put("5cent", 50l);
        DEFAULT.put("10cent", 100l);
        DEFAULT.put("20cent", 200l);
        DEFAULT.put("50cent", 500l);
        DEFAULT.put("1foney", 1000l);
        DEFAULT.put("2foney", 2000l);
        DEFAULT.put("5foney", 5000l);
        DEFAULT.put("10foney", 10000l);
        DEFAULT.put("20foney", 20000l);
        DEFAULT.put("50foney", 50000l);
        DEFAULT.put("100foney", 100000l);
        DEFAULT.put("200foney", 200000l);
        DEFAULT.put("500foney", 500000l);
        DEFAULT.put("1000foney", 1000000l);
        DEFAULT.put("2000foney", 2000000l);
        DEFAULT.put("5000foney", 5000000l);
        DEFAULT.put("10000foney", 10000000l);
        DEFAULT.put("20000foney", 20000000l);
        DEFAULT.put("50000foney", 50000000l);
        DEFAULT.put("100kfoney", 100000000l);
        DEFAULT.put("200kfoney", 200000000l);
        DEFAULT.put("500kfoney", 500000000l);
    }
    private static TreeMap<ResourceLocation, Long> EXTERNAL_ITEMS = new TreeMap<>();
    private static TreeMap<String, Long> EXTERNAL_ITEMS_METAWORTH = new TreeMap<>();
    private static File CFG_FILE;
    private static boolean resave;

    public static void load(){
        CFG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "fsmm.json");
        if(!CFG_FILE.exists()) JsonHandler.print(CFG_FILE, new JsonMap(), JsonHandler.PrintOption.SPACED);
        refresh();
        //
        File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "/fsmm_items.json");
        if(!file.exists()){
            JsonUtil.write(file, getDefaultContent());
        }
        JsonObject obj = JsonUtil.get(file);
        if(obj.has("Items")){
            obj.get("Items").getAsJsonArray().forEach((elm) -> {
                GenericMoney money = new GenericMoney(elm.getAsJsonObject(), true);
                FSMM.CURRENCY.put(money.getRegistryName(), money);
                //TODO itemreg FCLRegistry.getAutoRegistry("fsmm").addItem(money.getRegistryName().getPath(), new GenericMoneyItem(money), 1, null);
                //TODO itemreg money.stackload(FCLRegistry.getItem("fsmm:" + money.getRegistryName().getPath()), elm.getAsJsonObject(), true);
            });
            GenericMoneyItem.sort();
        }
        loadExternalItems(obj);
        //
        if(obj.has("Banks")){
            DEF_BANKS = obj.get(("Banks")).getAsJsonArray();
        }
    }

    private static void refresh(){
        JsonMap config = JsonHandler.parse(CFG_FILE).asMap();
        if(!config.has("format")){
            config.add("format", 1);
            JsonMap general = new JsonMap();
            general.add("desc", "General FSMM Settings");
            config.add("general", general);
            JsonMap display = new JsonMap();
            display.add("desc", "Client Settings");
            config.add("display", display);
            resave = true;
        }
        LOCAL.starting_balance = STARTING_BALANCE = getInt(config, "general", "starting_balance", 100000, 0, Integer.MAX_VALUE, "Starting balance for a new player. (1000 == 1F$)");
        LOCAL.default_bank = DEFAULT_BANK = getStr(config, "general", "default_bank", "00000000", "Default Bank the player will have an account in.!");
        LOCAL.notify_balance_on_join = NOTIFY_BALANCE_ON_JOIN = getBool(config, "display", "notify_balance_on_join", true, "Should the player be notified about his current balance when joining the game?");
        LOCAL.currency_sign = CURRENCY_SIGN = getStr(config, "display", "currency_sign", "F$", "So now you can even set a custom Currency Sign.");
        LOCAL.invert_comma = INVERT_COMMA = getBool(config, "display", "invert_comma", false, "Invert ',' and '.' display.");
        LOCAL.show_centesimals = SHOW_CENTESIMALS = getBool(config, "display", "show_centesimals", false, "Should centesimals be shown? E.g. '29,503' instead of '29.50'.");
        LOCAL.show_item_worth_in_tooltip = SHOW_ITEM_WORTH_IN_TOOLTIP = getBool(config, "display", "show_item_worth", true, "Should the Item's Worth be shown in the tooltip?");
        LOCAL.unload_frequency = UNLOAD_FREQUENCY = getInt(config, "general", "unload_frequency", 600000, Static.dev() ? 30000 : 60000, 86400000 / 2, "Frequency of how often it should be checked if (temporarily loaded) accounts/banks should be unloaded. Time in milliseconds.");
        LOCAL.partial_account_name_search = PARTIAL_ACCOUNT_NAME_SEARCH = getBool(config, "general", "partial_account_name_search", true, "If true, accounts can be searched by inputting only part of the name, otherwise on false, the full ID/Name is required.");
        String thosep = getStr(config, "display", "thousand_separator", "null", "Custom thousand separator sign, leave as 'null' for default behaviour.");
        LOCAL.thousand_separator = THOUSAND_SEPARATOR = thosep.equals("null") ? null : thosep;
        LOCAL.show_decimals = getBool(config, "display", "show_decimals", true, "Should decimals be shown when zero? e.g. '234.00'");
        LOCAL.min_search_chars = getInt(config, "general", "min_search_chars", 3, 1, 1000, "Minimum characters to enter in the 'Name/ID' search bar for search to work.");
        //
        COMMA = INVERT_COMMA ? "." : ","; DOT = INVERT_COMMA ? "," : ".";
        //
        if(resave){
            JsonHandler.print(CFG_FILE, config, JsonHandler.PrintOption.SPACED);
            resave = false;
        }
    }

    private static int getInt(JsonMap config, String cat, String key, int def, int min, int max, String desc){
        config = config.getMap(cat);
        if(!config.has(key)){
            JsonMap entry = new JsonMap();
            entry.add("info", desc);
            entry.add("range", "between " + min + " and " + (max == Integer.MAX_VALUE ? "unlimited" : max));
            entry.add("default", def);
            entry.add("value", def);
            config.add(key, entry);
            resave = true;
        }
        int val = config.getMap(key).getInteger("value", def);
        if(val > max) val = max;
        if(val < min) val = min;
        return val;
    }

    private static String getStr(JsonMap config, String cat, String key, String def, String desc){
        config = config.getMap(cat);
        if(!config.has(key)){
            JsonMap entry = new JsonMap();
            entry.add("info", desc);
            entry.add("default", def);
            entry.add("value", def);
            config.add(key, entry);
            resave = true;
        }
        return config.getMap(key).getString("value", def);
    }

    private static boolean getBool(JsonMap config, String cat, String key, boolean def, String desc){
        config = config.getMap(cat);
        if(!config.has(key)){
            JsonMap entry = new JsonMap();
            entry.add("info", desc);
            entry.add("default", def);
            entry.add("value", def);
            config.add(key, entry);
            resave = true;
        }
        return config.getMap(key).getBoolean("value", def);
    }

    public static void loadDefaultBanks(){
        if(DEF_BANKS == null) return;
        DEF_BANKS.forEach((elm) -> {
            String uuid = elm.getAsJsonObject().get("uuid").getAsString();
            File file = new File(DataManager.BANK_DIR, uuid + ".json");
            if(!file.exists() && !DataManager.getBanks().containsKey(uuid)){
                DataManager.addBank(new GenericBank(elm.getAsJsonObject()));
            }
        });
    }

    private static JsonObject getDefaultContent(){
        JsonObject obj = new JsonObject();
        JsonArray items = new JsonArray();
        DEFAULT.forEach((id, worth) -> {
            JsonObject jsn = new JsonObject();
            jsn.addProperty("id", id);
            jsn.addProperty("worth", worth);
            items.add(jsn);
        });
        obj.add("Items", items);
        //
        JsonArray banks = new JsonArray();
        JsonObject def = new JsonObject();
        def.addProperty("uuid", DEFAULT_BANK);
        def.addProperty("name", "Default Server Bank");
        def.add("data", new JsonObject());
        banks.add(def);
        obj.add("Banks", banks);
        //
        JsonObject extexp = new JsonObject();
        JsonArray ext = new JsonArray();
        extexp.addProperty("id", "minecraft:nether_star");
        extexp.addProperty("worth", 100000);
        extexp.addProperty("register", false);
        ext.add(extexp);
        obj.add("ExternalItems", ext);
        //
        return obj;
    }

    public static void loadExternalItems(JsonObject obj){
        if(obj.has("ExternalItems")){
            obj.get("ExternalItems").getAsJsonArray().forEach(elm -> {
                JsonObject jsn = elm.getAsJsonObject();
                ResourceLocation rs = new ResourceLocation(jsn.get("id").getAsString());
                long worth = jsn.get("worth").getAsLong();
                int meta = jsn.has("meta") ? jsn.get("meta").getAsInt() : -1;
                //
                if(meta >= 0){
                    EXTERNAL_ITEMS_METAWORTH.put(rs.toString() + ":" + meta, worth);
                    if(!EXTERNAL_ITEMS.containsKey(rs)){
                        EXTERNAL_ITEMS.put(rs, 0l);
                    }
                }
                else{
                    EXTERNAL_ITEMS.put(rs, worth);
                }
                if(jsn.has("register") && jsn.get("register").getAsBoolean()){
                    GenericMoney money = new GenericMoney(jsn, false);
                    FSMM.CURRENCY.put(money.getRegistryName(), money);
                }
            });
        }
    }

    public static final String getWorthAsString(long value){
        return getWorthAsString(value, true, false);
    }

    public static final String getWorthAsString(long value, boolean append){
        return getWorthAsString(value, append, false);
    }

    public static final String getWorthAsString(long value, boolean append, boolean ignore){
        String str = value + "";
        if(value < 1000){
            if(!SHOW_DECIMALS && (value == 0 || (!SHOW_CENTESIMALS && !ignore && value < 100))) return "0" + (append ? CURRENCY_SIGN : "");
            str = value + "";
            str = str.length() == 1 ? "00" + str : str.length() == 2 ? "0" + str : str;
            return ((str = "0" + COMMA + str).length() == 5 && (ignore ? false : !SHOW_CENTESIMALS) ? str.substring(0, 4) : str) + (append ? CURRENCY_SIGN : "");
        }
        else{
            try{
                str = new StringBuilder(str).reverse().toString();
                String[] arr = str.split("(?<=\\G...)");
                str = arr[0] + COMMA;
                for(int i = 1; i < arr.length; i++){
                    str += arr[i] + ((i >= arr.length - 1) ? "" : THOUSAND_SEPARATOR == null ? DOT : THOUSAND_SEPARATOR);
                }
                str = new StringBuilder(str).reverse().toString();
                return (str = SHOW_DECIMALS ? SHOW_CENTESIMALS || ignore ? str : str.substring(0, str.length() - 1) : str.substring(0, str.lastIndexOf(COMMA))) + (append ? CURRENCY_SIGN : "");
            }
            catch(Exception e){
                e.printStackTrace();
                return value + "ERR";
            }
        }
    }

    public static final long getItemStackWorth(ItemStack stack){
        if(stack.getItem() instanceof Money.Item){
            return ((Money.Item)stack.getItem()).getWorth(stack);
        }
        if(EXTERNAL_ITEMS_METAWORTH.containsKey(stack.getItem().getRegistryName() + ":" + stack.getDamageValue())){
            return EXTERNAL_ITEMS_METAWORTH.get(stack.getItem().getRegistryName() + ":" + stack.getDamageValue());
        }
        if(EXTERNAL_ITEMS.containsKey(stack.getItem().getRegistryName())){
            return EXTERNAL_ITEMS.get(stack.getItem().getRegistryName());
        }
        return 0;
    }

    public static boolean containsAsExternalItemStack(ItemStack stack){
        try{
            return EXTERNAL_ITEMS.containsKey(stack.getItem().getRegistryName())
                    || EXTERNAL_ITEMS_METAWORTH.containsKey(stack.getItem().getRegistryName() + ":" + stack.getDamageValue());
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getComma(){
        return COMMA;
    }

    public static String getDot(){
        return DOT;
    }

}
