package cc.ranmc.residence.gui.listener;

import cc.ranmc.residence.gui.Main;
import cc.ranmc.residence.gui.command.ResguiCommand;
import cc.ranmc.residence.gui.util.BasicUtil;
import cc.ranmc.residence.gui.util.InputUtil;
import cc.ranmc.residence.gui.util.ResidenceUtil;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cc.ranmc.residence.gui.util.BasicUtil.color;

public class GUIListener implements Listener {

    private static final Main plugin = Main.getInstance();

    @EventHandler
    public void onResidenceChangedEvent(ResidenceChangedEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getTitle().contains(color("&b&l领地管理丨"))) {
            player.closeInventory();
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player p = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (event.getView().getTitle().contains(color("&b&l领地管理丨领地列表"))
                || event.getView().getTitle().contains(color("&b&l领地管理丨")) && event.getView().getTitle().contains("的领地")) {
            event.setCancelled(true);
            if (clicked == null) return;
            boolean isOwnList = event.getView().getTitle().contains(color("&b&l领地管理丨领地列表"));

            // 点击领地物品
            if (event.getRawSlot() < 45) {
                String resName = ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName());
                ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(resName);
                if (res == null) {
                    p.sendMessage(color("&c找不到该领地"));
                    return;
                }
                if (event.getClick() == ClickType.RIGHT || !isOwnList) {
                    // 右键传送，或查看别人的领地时左键也传送
                    if (res.getTeleportLocation(p) != null) {
                        p.teleportAsync(res.getTeleportLocation(p));
                        p.sendMessage(color("&a已传送到领地 &b" + res.getResidenceName()));
                    } else {
                        p.sendMessage(color("&c该领地未设置传送点"));
                    }
                    p.closeInventory();
                } else {
                    // 左键管理
                    ResguiCommand.openAdminGUI(p, res);
                }
                return;
            }

            // 当前领地快速管理（仅自己的列表）
            if (isOwnList && event.getRawSlot() == 49 && clicked.getType() == Material.ENCHANTED_BOOK) {
                ClaimedResidence current = ResidenceApi.getResidenceManager().getByLoc(p.getLocation());
                if (current != null) {
                    ResguiCommand.openAdminGUI(p, current);
                }
                return;
            }

            // 关闭
            if (event.getRawSlot() == 45 || event.getRawSlot() == 53) {
                p.closeInventory();
                return;
            }
            return;
        }

        if (event.getView().getTitle().contains(color("&b&l领地管理丨公共权限"))) {
            event.setCancelled(true);
            if (clicked == null) return;

            // 从标题解析领地名 &b&l领地管理丨公共权限丨<名称>
            String raw = ChatColor.stripColor(event.getView().getTitle());
            String[] parts = raw.split("丨");
            String resName = parts.length >= 3 ? parts[2] : null;
            if (resName == null || resName.isEmpty()) {
                p.sendMessage(color("&c无法获取领地信息"));
                p.closeInventory();
                return;
            }
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByName(resName);
            if (claimedResidence == null) {
                p.sendMessage(color("&c该领地不存在"));
                p.closeInventory();
                return;
            }

            List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(claimedResidence.getPermissions().listPlayersFlags())).split(" "));
            Boolean admin = false;
            if (permList.contains(p.getName())) {
                admin = claimedResidence.getPermissions().getPlayerFlags(p.getName()).get("admin");
            }
            if ((admin == null || !admin) && !claimedResidence.getOwner().equalsIgnoreCase(p.getName())) {
                p.sendMessage(color("&c你没有权限设置该领地"));
                return;
            }

            if (event.getRawSlot() <= 35 && event.getCurrentItem() != null) {
                String permName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                permName = permName.substring(permName.indexOf("(") + 1, permName.indexOf(")"));
                ResidenceUtil.setFlag(event, permName, claimedResidence);
                return;
            }

            if (event.getRawSlot() == 46) {
                p.chat("/res tpset");
                p.closeInventory();
                return;
            }

            // 调整范围
            if (event.getRawSlot() == 50) {
                if (event.getClick() == ClickType.LEFT) {
                    InputUtil.open(p, "扩大领地", "请输入要扩大的格数", result -> {
                        try {
                            int amount = Integer.parseInt(result);
                            p.chat("/res expand " + amount);
                        } catch (NumberFormatException e) {
                            p.sendMessage(color("&c请输入有效的数字"));
                        }
                    });
                }
                if (event.getClick() == ClickType.RIGHT) {
                    InputUtil.open(p, "缩小领地", "请输入要缩小的格数", result -> {
                        try {
                            int amount = Integer.parseInt(result);
                            p.chat("/res contract " + amount);
                        } catch (NumberFormatException e) {
                            p.sendMessage(color("&c请输入有效的数字"));
                        }
                    });
                }
                return;
            }

            if (event.getRawSlot() == 51) {
                if (event.getClick() == ClickType.LEFT) {
                    String currentMsg = claimedResidence.getEnterMessage() != null ? claimedResidence.getEnterMessage() : "";
                    InputUtil.open(p, "设置领地进入消息", "", currentMsg, result -> {
                        claimedResidence.setEnterMessage(result);
                        p.sendMessage(color("&e消息文本已设置"));
                    });
                }
                if (event.getClick() == ClickType.RIGHT) {
                    String currentMsg = claimedResidence.getLeaveMessage() != null ? claimedResidence.getLeaveMessage() : "";
                    InputUtil.open(p, "设置领地离开消息", "", currentMsg, result -> {
                        claimedResidence.setLeaveMessage(result);
                        p.sendMessage(color("&e消息文本已设置"));
                    });
                }
                return;
            }

            if (event.getRawSlot() == 47) {
                p.closeInventory();
                if (Math.abs(claimedResidence.getTotalSize()) > 15360000) {
                    p.chat("/res show");
                    return;
                }
                Location lowloc = claimedResidence.getAreaArray()[0].getLowLoc();
                Location highloc = claimedResidence.getAreaArray()[0].getHighLoc();
                ResidenceUtil.showEdge(p, lowloc, highloc);

                long hideDelay = plugin.getConfig().getInt("glass-show-time", 5);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        ResidenceUtil.hideEdge(p, lowloc, highloc), hideDelay * 20);
                return;
            }

            if (event.getRawSlot() == 48) {
                p.chat("/res remove " + claimedResidence.getResidenceName());
                p.closeInventory();
                return;
            }

            if (event.getRawSlot() == 52) {
                p.chat("/resgui perm");
                return;
            }

            // 返回领地列表
            if (event.getRawSlot() == 45) {
                ResguiCommand.openResidenceList(p);
                return;
            }

            if (event.getRawSlot() == 53) {
                p.closeInventory();
                return;
            }
        }

        if (event.getView().getTitle().contains(color("&b&l领地管理丨玩家权限"))) {
            event.setCancelled(true);
            if (clicked == null) return;

            // 从标题解析领地名
            String raw = ChatColor.stripColor(event.getView().getTitle());
            String[] parts = raw.split("丨");
            String resName = parts.length >= 3 ? parts[2] : null;
            if (resName == null || resName.isEmpty()) {
                p.sendMessage(color("&c无法获取领地信息"));
                p.closeInventory();
                return;
            }
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByName(resName);
            if (claimedResidence == null) {
                p.sendMessage(color("&c该领地不存在"));
                p.closeInventory();
                return;
            }

            List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(claimedResidence.getPermissions().listPlayersFlags())).split(" "));
            Boolean admin = false;
            if (permList.contains(p.getName())) {
                admin = claimedResidence.getPermissions().getPlayerFlags(p.getName()).get("admin");
            }
            if ((admin == null || !admin) && (!claimedResidence.getOwner().equalsIgnoreCase(p.getName()))) {
                p.sendMessage(color("&c你没有权限设置该领地"));
                return;
            }

            if (event.getRawSlot() == 53) {
                p.closeInventory();
                return;
            }

            // 返回领地列表
            if (event.getRawSlot() == 45) {
                ResguiCommand.openResidenceList(p);
                return;
            }

            if (event.getRawSlot() == 49) {
                Inventory inventory = Bukkit.createInventory(null, 54, color("&b&l领地管理丨添加玩家丨" + resName));
                int invSize = 0;
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!permList.contains(onlinePlayer.getName())) {
                        if (invSize < 45) {
                            inventory.setItem(invSize, BasicUtil.createSkullItem(Material.PLAYER_HEAD, onlinePlayer, "&e不要分享权限给陌生人", "&e否则造成损失后果自负"));
                            invSize++;
                        }
                    }
                }
                inventory.setItem(49, BasicUtil.createItem(Material.WRITABLE_BOOK, "&b手动添加", "&e找不到该玩家", "&e可能已经离线", "&e尝试输入名称"));
                ItemStack closeItem = BasicUtil.createItem(Material.BARRIER, "&b返回菜单");
                inventory.setItem(45, closeItem);
                inventory.setItem(53, closeItem);
                BasicUtil.fillEmptySlots(inventory);
                p.openInventory(inventory);
                return;
            }

            if (event.getRawSlot() < 49 && event.getCurrentItem() != null) {
                String playerName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName().replace("§b", "");
                Inventory inventory = Bukkit.createInventory(null, 54, color("&b&l领地管理丨玩家" + playerName + "丨" + resName));
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.DIAMOND_PICKAXE, "&b建筑&f(build)", "&e是否允许放置或破坏方块", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.GRASS_BLOCK, "&b放置&f(place)", "&e是否允许放置方块", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.IRON_AXE, "&b破坏&f(destroy)", "&e是否允许破坏方块", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.LEVER, "&b交互&f(use)", "&e是否允许与拉杆/门/工作台等交互", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.CHEST_MINECART, "&b容器&f(container)", "&e是否允许与箱子/漏斗等交互", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ENDER_EYE, "&b传送&f(tp)", "&e是否允许传送至领地", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SADDLE, "&b骑乘&f(riding)", "&e是否允许骑乘生物", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.COOKED_BEEF, "&b攻击动物&f(animalkilling)", "&e是否允许攻击动物", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ROTTEN_FLESH, "&b攻击怪物&f(mobkilling)", "&e是否允许攻击怪物", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ENDER_PEARL, "&b末影珍珠&f(enderpearl)", "&e是否允许使用末影珍珠进入", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.OAK_BOAT, "&b破坏载具&f(vehicledstroy)", "&e是否允许破坏载具", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.CHORUS_FRUIT, "&b紫颂果&f(chorustp)", "&e是否允许使用紫颂果", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.NOTE_BLOCK, "&b音符盒&f(note)", "&e是否允许使用音符", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.LEAD, "&b拴绳&f(leash)", "&e是否允许牵引动物", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.FLINT_AND_STEEL, "&b点火&f(ignite)", "&e是否允许使用打火石", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.ARROW, "&b射箭&f(shoot)", "&e是否允许使用弓弩", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.INK_SAC, "&b染色&f(dye)", "&e是否允许使用染色", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.SHEARS, "&b剪取&f(shear)", "&e是否允许剪取羊毛", playerName);
                ResidenceUtil.CreatePermButton(claimedResidence, inventory, Material.PEONY, "&b管理&f(admin)", "&e是否允许修改领地权限", playerName);

                inventory.setItem(48, BasicUtil.createItem(Material.COMPARATOR, "&b删除权限", "§e对目标指定该玩家", "§e移除基本领地权限"));
                inventory.setItem(50, BasicUtil.createItem(Material.BREWING_STAND, "&b转让领地", "§e对目标指定该玩家", "§e给予领地的所有权"));

                ItemStack closeItem = BasicUtil.createItem(Material.BARRIER, "&b返回菜单");
                inventory.setItem(45, closeItem);
                inventory.setItem(53, closeItem);
                BasicUtil.fillEmptySlots(inventory);
                p.openInventory(inventory);
                return;
            }
        }

        if (event.getView().getTitle().contains(color("&b&l领地管理丨玩家")) && !event.getView().getTitle().contains("添加")) {
            event.setCancelled(true);
            if (clicked == null) return;

            // 从标题解析领地名 &b&l领地管理丨玩家<player>丨<name>
            String raw = ChatColor.stripColor(event.getView().getTitle());
            String[] parts = raw.split("丨");
            String resName = parts.length >= 3 ? parts[2] : null;
            if (resName == null || resName.isEmpty()) {
                p.sendMessage(color("&c无法获取领地信息"));
                p.closeInventory();
                return;
            }
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByName(resName);
            if (claimedResidence == null) {
                p.sendMessage(color("&c该领地不存在"));
                p.closeInventory();
                return;
            }

            List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(claimedResidence.getPermissions().listPlayersFlags())).split(" "));
            Boolean admin = false;
            if (permList.contains(p.getName())) {
                admin = claimedResidence.getPermissions().getPlayerFlags(p.getName()).get("admin");
            }
            if ((admin == null || !admin) && !claimedResidence.getOwner().equalsIgnoreCase(p.getName())) {
                p.sendMessage(color("&c你没有权限设置该领地"));
                return;
            }

            if (event.getRawSlot() == 53) {
                // 返回玩家权限菜单
                openPlayerPermList(p, claimedResidence, resName);
                return;
            }

            // 返回领地列表
            if (event.getRawSlot() == 45) {
                ResguiCommand.openResidenceList(p);
                return;
            }

            if (event.getRawSlot() == 48) {
                String playerName = ChatColor.stripColor(event.getView().getTitle()).split("丨")[1].replace("玩家", "");
                p.chat("/res pset " + claimedResidence.getResidenceName() + " " + playerName + " removeall");
                p.chat("/resgui perm");
                return;
            }

            if (event.getRawSlot() == 50) {
                String playerName = ChatColor.stripColor(event.getView().getTitle()).split("丨")[1].replace("玩家", "");
                p.chat("/res give " + claimedResidence.getResidenceName() + " " + playerName);
                p.closeInventory();
                return;
            }

            if (event.getRawSlot() < 49 && event.getCurrentItem() != null) {
                String permName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                permName = permName.substring(permName.indexOf("(") + 1, permName.indexOf(")"));
                String playerName = ChatColor.stripColor(event.getView().getTitle()).split("丨")[1].replace("玩家", "");
                ResidenceUtil.setFlag(event, permName, claimedResidence, playerName);
                return;
            }
        }

        if (event.getView().getTitle().contains(color("&b&l领地管理丨添加玩家"))) {
            event.setCancelled(true);
            if (clicked == null) return;

            // 从标题解析领地名
            String raw = ChatColor.stripColor(event.getView().getTitle());
            String[] parts = raw.split("丨");
            String resName = parts.length >= 3 ? parts[2] : null;
            if (resName == null || resName.isEmpty()) {
                p.sendMessage(color("&c无法获取领地信息"));
                p.closeInventory();
                return;
            }
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByName(resName);
            if (claimedResidence == null) {
                p.sendMessage(color("&c该领地不存在"));
                p.closeInventory();
                return;
            }

            List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(claimedResidence.getPermissions().listPlayersFlags())).split(" "));
            Boolean admin = false;
            if (permList.contains(p.getName())) {
                admin = claimedResidence.getPermissions().getPlayerFlags(p.getName()).get("admin");
            }
            if ((admin == null || !admin) && !claimedResidence.getOwner().equalsIgnoreCase(p.getName())) {
                p.sendMessage(color("&c你没有权限设置该领地"));
                return;
            }

            if (event.getRawSlot() == 53) {
                // 返回玩家权限菜单
                openPlayerPermList(p, claimedResidence, resName);
                return;
            }

            // 返回领地列表
            if (event.getRawSlot() == 45) {
                ResguiCommand.openResidenceList(p);
                return;
            }

            if (event.getRawSlot() == 49) {
                InputUtil.open(p, "添加玩家权限", "请输入玩家名称",
                        result -> {
                            p.chat("/res padd " + claimedResidence.getResidenceName() + " " + result);
                            openPlayerPermList(p, claimedResidence, resName);
                        });
                return;
            }

            if (event.getRawSlot() < 49 && event.getCurrentItem() != null) {
                p.chat("/res padd " + claimedResidence.getResidenceName() + " " + ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName()));
                openPlayerPermList(p, claimedResidence, resName);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType() == Material.WOODEN_HOE
                && event.getAction() == Action.RIGHT_CLICK_AIR
                && plugin.getConfig().getBoolean("right-click-action")) {
            ClaimedResidence claimedResidence = ResidenceApi.getResidenceManager().getByLoc(p.getLocation());
            if (claimedResidence != null) {
                p.chat("/resgui");
            }
        }
    }

    private static void openPlayerPermList(Player p, ClaimedResidence residence, String resName) {
        List<String> permList = Arrays.asList(BasicUtil.removeBrackets(ChatColor.stripColor(residence.getPermissions().listPlayersFlags())).split(" "));
        Inventory inventory = Bukkit.createInventory(null, 54, color("&b&l领地管理丨玩家权限丨" + resName));
        int invSize = 0;
        for (int i = 0; i < permList.size(); i++) {
            if (i < 45 && !permList.get(i).equals(residence.getOwner())) {
                String line1 = "&e基础权限: &c否";
                if (residence.getPermissions().getPlayerFlags(permList.get(i)) == null) {
                    line1 = "&e基础权限: &c否";
                } else if (residence.getPermissions().getPlayerFlags(permList.get(i)).get("build") == null) {
                    line1 = "&e基础权限: &c未设置";
                } else if (residence.getPermissions().getPlayerFlags(permList.get(i)).get("build")) {
                    line1 = "&e基础权限: &c是";
                }
                inventory.setItem(invSize, ResidenceUtil.createPermItem(Material.PLAYER_HEAD, "&b" + permList.get(i), line1, "&e查看更多详情"));
                invSize++;
            }
        }
        inventory.setItem(49, BasicUtil.createItem(Material.OAK_SIGN, "&b添加玩家", "&e添加你受信任玩家", "&e给予基本领地权限"));
        inventory.setItem(45, BasicUtil.createItem(Material.ARROW, "&b返回领地列表"));
        inventory.setItem(53, BasicUtil.createItem(Material.BARRIER, "&b关闭菜单"));
        BasicUtil.fillEmptySlots(inventory);
        p.openInventory(inventory);
    }
}
