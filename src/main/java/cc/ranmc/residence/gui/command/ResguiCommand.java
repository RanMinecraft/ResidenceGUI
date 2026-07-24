package cc.ranmc.residence.gui.command;

import cc.ranmc.residence.gui.Main;
import cc.ranmc.residence.gui.util.BasicUtil;
import cc.ranmc.residence.gui.util.InputUtil;
import cc.ranmc.residence.gui.util.ResidenceUtil;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cc.ranmc.residence.gui.util.BasicUtil.color;
import static cc.ranmc.residence.gui.util.ResidenceUtil.getResCommand;

public class ResguiCommand implements CommandExecutor {

    /**
     * 打开领地列表（查看自己的领地）
     */
    public static void openResidenceList(Player p) {
        openResidenceList(p, p.getName(), true);
    }

    /**
     * 打开领地列表
     * @param viewer   查看者
     * @param owner    领地主人
     * @param canManage 是否可管理
     */
    public static void openResidenceList(Player viewer, String owner, boolean canManage) {
        boolean canManageTitle = canManage;
        if (viewer.isOp()) canManage = true;

        ResidenceManager manager = (ResidenceManager) ResidenceApi.getResidenceManager();
        Map<String, ClaimedResidence> all = manager.getResidenceMapList(owner, true);
        String title = canManageTitle ? "&b&l领地管理丨领地列表" : "&b&l领地管理丨" + owner + "的领地";
        Inventory inventory = Bukkit.createInventory(null, 54, color(title));

        int slot = 0;
        if (all != null) {
            for (Map.Entry<String, ClaimedResidence> entry : all.entrySet()) {
                if (slot >= 45) break;
                ClaimedResidence res = entry.getValue();
                if (canManage) {
                    inventory.setItem(slot, BasicUtil.createItem(Material.OAK_CHEST_BOAT, "&b" + res.getResidenceName(),
                            "&e左键管理领地权限", "&e右键传送到领地处"));
                } else {
                    inventory.setItem(slot, BasicUtil.createItem(Material.OAK_CHEST_BOAT, "&b" + res.getResidenceName(),
                            "&e点击传送到领地处"));
                }
                slot++;
            }
        }

        // 当前所在地信息
        ClaimedResidence current = ResidenceApi.getResidenceManager().getByLoc(viewer.getLocation());
        if (current != null) {
            List<String> lore = new ArrayList<>();
            lore.add(color("&e领地名: " + current.getResidenceName()));
            lore.add(color("&e领地主人: " + current.getOwner()));
            if (canManage) {
                lore.add(color("&e点击快速管理"));
            }
            inventory.setItem(49, BasicUtil.createItem(Material.ENCHANTED_BOOK, "&b当前领地", lore.toArray(new String[0])));
        }

        // 底部按钮
        inventory.setItem(45, BasicUtil.createItem(Material.BARRIER, "&b关闭菜单"));
        BasicUtil.fillEmptySlots(inventory);
        inventory.setItem(53, BasicUtil.createItem(Material.BARRIER, "&b关闭菜单"));

        viewer.openInventory(inventory);
    }

    /**
     * 打开领地管理菜单（公共权限）
     */
    public static void openAdminGUI(Player p, ClaimedResidence claimedResidence) {
        List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(claimedResidence.getPermissions().listPlayersFlags())).split(" "));
        Boolean admin = false;
        if (permList.contains(p.getName())) {
            admin = claimedResidence.getPermissions().getPlayerFlags(p.getName()).get("admin");
        }
        if ((admin == null || !admin) && !claimedResidence.getOwner().equalsIgnoreCase(p.getName())) {
            p.sendMessage(color("&c你没有权限设置该领地"));
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, 54, color("&b&l领地管理丨公共权限丨" + claimedResidence.getResidenceName()));

        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.DIAMOND_PICKAXE, "&b建筑&f(build)", "&e是否允许放置或破坏方块");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.GRASS_BLOCK, "&b放置&f(place)", "&e是否允许放置方块");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.IRON_AXE, "&b破坏&f(destroy)", "&e是否允许破坏方块");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.LEVER, "&b交互&f(use)", "&e是否允许与拉杆/门/工作台等交互");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.CHEST_MINECART, "&b容器&f(container)", "&e是否允许与箱子/漏斗等交互");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ENDER_EYE, "&b传送&f(tp)", "&e是否允许传送至领地");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SADDLE, "&b骑乘&f(riding)", "&e是否允许骑乘生物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.COOKED_BEEF, "&b攻击动物&f(animalkilling)", "&e是否允许攻击动物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ROTTEN_FLESH, "&b攻击怪物&f(mobkilling)", "&e是否允许攻击怪物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SHEEP_SPAWN_EGG, "&b生成动物&f(animals)", "&e是否允许生成动物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SKELETON_SPAWN_EGG, "&b生成怪物&f(monsters)", "&e是否允许生成怪物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.LILY_PAD, "&b怪物进入&f(nomobs)", "&e是否允许怪物进入");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.WATER_BUCKET, "&b液体流动&f(flow)", "&e是否允许液体流动");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ENDER_PEARL, "&b末影珍珠&f(enderpearl)", "&e是否允许使用末影珍珠进入");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.OAK_BOAT, "&b破坏载具&f(vehicledestroy)", "&e是否允许破坏载具");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.DIRT, "&b耕地保护&f(trample)", "&e是否开启耕地保护");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.CHORUS_FRUIT, "&b紫颂果&f(chorustp)", "&e是否允许使用紫颂果");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.NOTE_BLOCK, "&b音符盒&f(note)", "&e是否允许使用音符");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.WHEAT_SEEDS, "&b生长&f(grow)", "&e是否允许农作物生长");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.LEAD, "&b拴绳&f(leash)", "&e是否允许牵引动物");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.FLINT_AND_STEEL, "&b点火&f(ignite)", "&e是否允许使用打火石");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.BLAZE_POWDER, "&b火势蔓延&f(firespread)", "&e是否允许火势蔓延");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.TNT, "&b爆炸&f(explode)", "&e是否允许爆炸伤害");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.DIAMOND_SWORD, "&b格斗&f(pvp)", "&e是否允许玩家伤害");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ARROW, "&b射箭&f(shoot)", "&e是否允许使用弓弩");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.INK_SAC, "&b染色&f(dye)", "&e是否允许使用染色");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.PISTON, "&b活塞&f(piston)", "&e是否允许活塞工作");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SHEARS, "&b剪取&f(shear)", "&e是否允许剪取羊毛");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ICE, "&b融化&f(icemelt)", "&e是否允许冰雪融化");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.BLUE_ICE, "&b阻止冰霜行者&f(iceform)", "&e是否阻止冰霜行者生效");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.YELLOW_DYE, "&b永昼&f(day)", "&e是否开启领地内白天");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.BLACK_DYE, "&b永夜&f(night)", "&e是否开启领地内夜天");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SUNFLOWER, "&b晴天&f(sun)", "&e是否开启领地内晴天");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.BLUE_ORCHID, "&b雨天&f(rain)", "&e是否开启领地内雨天");
        ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.FEATHER, "&b飞行&f(svip)", "&e是否允许s/vip领地内飞行");

        ItemStack item2 = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta2 = item2.getItemMeta();
        Objects.requireNonNull(meta2).setDisplayName(color("&b领地详情"));
        ArrayList<String> lore2 = new ArrayList<>();
        lore2.add(color("&e领地名: " + claimedResidence.getResidenceName()));
        lore2.add(color("&e领地主人: " + claimedResidence.getOwner()));
        lore2.add(color("&e领地规格: " + (int) claimedResidence.getTotalSize() + "m³"));
        if (claimedResidence.getEnterMessage() != null) {
            lore2.add(color("&e进入消息: " + claimedResidence.getEnterMessage()));
        } else {
            lore2.add(color("&e进入消息: 无"));
        }
        if (claimedResidence.getLeaveMessage() != null) {
            lore2.add(color("&e离开消息: " + claimedResidence.getLeaveMessage()));
        } else {
            lore2.add(color("&e离开消息: 无"));
        }
        meta2.setLore(lore2);
        item2.setItemMeta(meta2);
        inventory.setItem(49, item2);

        inventory.setItem(46, BasicUtil.createItem(Material.BEACON, "&b设置传送点", "&e记录你所在位置", "&e设为领地传送点"));
        inventory.setItem(47, BasicUtil.createItem(Material.POWERED_RAIL, "&b显示边界", "&e显示领地实际边界"));
        inventory.setItem(48, BasicUtil.createItem(Material.LAVA_BUCKET, "&b删除领地", "&e删除不会退还金币", "&e请谨慎决定后操作"));
        inventory.setItem(50, BasicUtil.createItem(Material.WOODEN_HOE, "&b调整范围", 
                "&e扩大需看着需要扩大的方向",
                "&e缩小需看着需要缩小的方向"));
        inventory.setItem(51, BasicUtil.createItem(Material.LEATHER, "&b设置提示", "&e左键设置进入领地消息", "&e右键设置离开领地消息"));
        inventory.setItem(52, BasicUtil.createItem(Material.PLAYER_HEAD, "&b分享权限", "&e你可以分享权限给好友",
                "&e不要分享权限给陌生人",
                "&e否则造成损失后果自负"));

        inventory.setItem(45, BasicUtil.createItem(Material.ARROW, "&b返回领地列表"));

        ItemStack closeItem = BasicUtil.createItem(Material.BARRIER, "&b关闭菜单");
        inventory.setItem(53, closeItem);
        BasicUtil.fillEmptySlots(inventory);
        p.openInventory(inventory);
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command cmd,
                             String label,
                             String[] args) {

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(color("&c该指令不能在控制台输入"));
                    return true;
                }
                InputUtil.open(p, "创建领地", "请输入领地名", result ->
                        p.chat(getResCommand(p) + " create " + result));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("resgui.admin")) {
                    Main.getInstance().loadConfig();
                    sender.sendMessage(color("&e重载完成"));
                } else {
                    sender.sendMessage(color("&c你没有权限这么做"));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("resgui.user")) {
                    sender.sendMessage(color("&e===== &b领地管理 帮助 &e====="));
                    sender.sendMessage(color("&e/resgui &f- 打开领地列表"));
                    sender.sendMessage(color("&e/resgui list <玩家> &f- 查看指定玩家领地"));
                    sender.sendMessage(color("&e/resgui help &f- 查看帮助"));
                    sender.sendMessage(color("&e/resgui reload &f- 重载配置文件 &7(管理员)"));
                } else {
                    sender.sendMessage(color("&c你没有权限这么做"));
                }
                return true;
            }
        }

        // 以下指令不能在控制台输入
        if (!(sender instanceof Player p)) {
            BasicUtil.print("&c该指令不能在控制台输入");
            return true;
        }

        // /resgui list <player> - 查看其他玩家的领地
        if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("resgui.user")) {
                sender.sendMessage(color("&c你没有权限这么做"));
                return true;
            }
            String target = args[1];
            if (target.equalsIgnoreCase(p.getName())) {
                openResidenceList(p, target, true);
            } else {
                openResidenceList(p, target, false);
            }
            return true;
        }

        // 打开领地列表
        if (args.length == 0) {
            if (!sender.hasPermission("resgui.user")) {
                sender.sendMessage(color("&c你没有权限这么做"));
                return true;
            }
            openResidenceList(p);
            return true;
        }

        sender.sendMessage(color("&c未知指令"));
        return true;
    }
}
