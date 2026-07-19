package cc.ranmc.residence.gui.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicUtil {

    /**
     * 颜色替换
     */
    public static String color(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }

    /**
     * 后台信息
     */
    public static void print(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    /**
     * 删除中括号文本
     */
    public static String removeBrackets(String text) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("(\\[[^]]*])");
        Matcher m = p.matcher(text);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
        }
        for (String tmp : list) {
            text = text.replace("[" + tmp + "]", "");
        }
        return text;
    }

    /**
     * 创建物品
     */
    public static ItemStack createItem(Material m, String name) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(color(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material m, String name, String... lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(color(name));
        List<String> loreList = new ArrayList<>(Arrays.asList(lore));
        loreList.replaceAll(BasicUtil::color);
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSkullItem(Material m, Player player, String... lore) {
        ItemStack item = new ItemStack(m);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        Objects.requireNonNull(meta).setOwningPlayer(player);
        meta.setDisplayName(color("&b" + player.getName()));
        List<String> loreList = new ArrayList<>(Arrays.asList(lore));
        loreList.replaceAll(BasicUtil::color);
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 填充空白底部栏
     */
    public static void fillEmptySlots(Inventory inventory) {
        ItemStack pane = createItem(Material.GRAY_STAINED_GLASS_PANE, "&r");
        for (int i = 45; i <= 53; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, pane);
            }
        }
    }
}
