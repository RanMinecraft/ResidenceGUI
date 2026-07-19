package cc.ranmc.residence.gui.util;

import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cc.ranmc.residence.gui.util.BasicUtil.color;

public class ResidenceUtil {

    public static void showEdge(Player p, Location lowloc, Location highloc) {
        for (int i = 0; i <= highloc.getBlockX() - lowloc.getBlockX(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setX(lowloc.getBlockX() + i);
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setZ(highloc.getBlockZ());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setY(highloc.getBlockY());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setZ(lowloc.getBlockZ());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
            }
        }
        for (int i = 0; i <= highloc.getBlockZ() - lowloc.getBlockZ(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setZ(lowloc.getBlockZ() + i);
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setX(highloc.getBlockX());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setY(highloc.getBlockY());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setX(lowloc.getBlockX());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
            }
        }
        for (int i = 0; i <= highloc.getBlockY() - lowloc.getBlockY(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setY(lowloc.getBlockY() + i);
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setX(highloc.getBlockX());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setZ(highloc.getBlockZ());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
                loc.setX(lowloc.getBlockX());
                p.sendBlockChange(loc, Material.RED_STAINED_GLASS.createBlockData());
            }
        }
    }

    public static void hideEdge(Player p, Location lowloc, Location highloc) {
        for (int i = 0; i <= highloc.getBlockX() - lowloc.getBlockX(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setX(lowloc.getBlockX() + i);
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setZ(highloc.getBlockZ());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setY(highloc.getBlockY());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setZ(lowloc.getBlockZ());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
            }
        }
        for (int i = 0; i <= highloc.getBlockZ() - lowloc.getBlockZ(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setZ(lowloc.getBlockZ() + i);
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setX(highloc.getBlockX());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setY(highloc.getBlockY());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setX(lowloc.getBlockX());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
            }
        }
        for (int i = 0; i <= highloc.getBlockY() - lowloc.getBlockY(); i++) {
            Location loc = new Location(lowloc.getWorld(), lowloc.getBlockX(), lowloc.getBlockY(), lowloc.getBlockZ());
            if (loc.getChunk().isLoaded()) {
                loc.setY(lowloc.getBlockY() + i);
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setX(highloc.getBlockX());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setZ(highloc.getBlockZ());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
                loc.setX(lowloc.getBlockX());
                p.sendBlockChange(loc, loc.getBlock().getType(), (byte) 0);
            }
        }
    }

    /**
     * 设置物品LORE
     */
    public static ItemStack setItemLore(ItemStack item, int i, String s) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = Objects.requireNonNull(meta).getLore();
        Objects.requireNonNull(lore).set(i, color(s));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 创建权限物品
     */
    public static ItemStack createPermItem(Material m, String name, String... lore) {
        ItemStack item = new ItemStack(m);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(color(name));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> loreList = new ArrayList<>();
        for (String text : lore) {
            loreList.add(color(text));
        }
        loreList.add(color("&e点击设置权限"));
        loreList.add(color("&e右键取消设置"));
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * 设置领地权限
     */
    public static void setFlag(InventoryClickEvent event, String perm, ClaimedResidence claimedResidence) {
        Inventory inventory = event.getClickedInventory();
        if (event.getClick() == ClickType.RIGHT) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c未设置"));
            claimedResidence.getPermissions().setFlag(perm, FlagPermissions.FlagState.NEITHER);
            return;
        }
        Boolean flag = claimedResidence.getPermissions().getFlags().get(perm);
        if (flag == null) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c是"));
            claimedResidence.getPermissions().setFlag(perm, FlagPermissions.FlagState.TRUE);
        } else if (flag) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c否"));
            claimedResidence.getPermissions().setFlag(perm, FlagPermissions.FlagState.FALSE);
        } else {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c是"));
            claimedResidence.getPermissions().setFlag(perm, FlagPermissions.FlagState.TRUE);
        }
    }

    public static void setFlag(InventoryClickEvent event, String perm, ClaimedResidence claimedResidence, String playerName) {
        Inventory inventory = event.getClickedInventory();
        if (event.getClick() == ClickType.RIGHT) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c未设置"));
            claimedResidence.getPermissions().setFlag(perm, FlagPermissions.FlagState.NEITHER);
            return;
        }
        Boolean flag = claimedResidence.getPermissions().getPlayerFlags(playerName).get(perm);
        if (flag == null) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c是"));
            claimedResidence.getPermissions().setPlayerFlag(playerName, perm, FlagPermissions.FlagState.TRUE);
        } else if (flag) {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c否"));
            claimedResidence.getPermissions().setPlayerFlag(playerName, perm, FlagPermissions.FlagState.FALSE);
        } else {
            Objects.requireNonNull(inventory).setItem(event.getRawSlot(), setItemLore(Objects.requireNonNull(event.getCurrentItem()), 0, "&e权限状态: &c是"));
            claimedResidence.getPermissions().setPlayerFlag(playerName, perm, FlagPermissions.FlagState.TRUE);
        }
    }

    /**
     * 创建权限按钮
     */
    public static void CreatePermButton(ClaimedResidence claimedResidence, Inventory inventory, Material material, String name, String message) {
        String permName = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
        Boolean flag = claimedResidence.getPermissions().getFlags().get(permName);
        if (flag == null) {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c未设置", message));
        } else if (flag) {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c是", message));
        } else {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c否", message));
        }
    }

    public static void CreatePermButton(ClaimedResidence claimedResidence, Inventory inventory, Material material, String name, String message, String playerName) {
        String permName = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
        Boolean flag = claimedResidence.getPermissions().getPlayerFlags(playerName).get(permName);
        if (flag == null) {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c未设置", message));
        } else if (flag) {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c是", message));
        } else {
            inventory.addItem(createPermItem(material, name, "&e权限状态: &c否", message));
        }
    }
}
