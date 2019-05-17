package eu.thesystems.cloudnet.lobbyswitcher.core.config;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.GroupConnectableInventoryItem;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.SlottedInventoryItem;
import eu.thesystems.cloudnet.lobbyswitcher.core.servers.SimplifiedServerInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GroupConfig {

    private String[] targetGroup;
    private SwitcherInventory inventory;
    private String alreadyConnectedMessage;
    private SlottedInventoryItem inventoryClickableOpenItem, hotbarClickableOpenItem;
    private Collection<GroupConnectableInventoryItem> hotbarItems;
    private transient Inventory bukkitInventory;
    private transient Map<String, Integer> serverInfos;

    public GroupConfig(String[] targetGroup, String alreadyConnectedMessage, SwitcherInventory inventory, SlottedInventoryItem inventoryClickableOpenItem, SlottedInventoryItem hotbarClickableOpenItem, Collection<GroupConnectableInventoryItem> hotbarItems) {
        this.targetGroup = targetGroup;
        this.alreadyConnectedMessage = alreadyConnectedMessage;
        this.inventory = inventory;
        this.inventoryClickableOpenItem = inventoryClickableOpenItem;
        this.hotbarClickableOpenItem = hotbarClickableOpenItem;
        this.hotbarItems = hotbarItems;
    }

    public boolean initInventory(List<SimplifiedServerInfo> serverInfos) {
        if (this.bukkitInventory != null)
            return true;
        if (this.inventory == null)
            return false;
        if (this.serverInfos == null)
            this.serverInfos = new HashMap<>();
        if (this.inventory.getTitle() == null || this.inventory.getTitle().isEmpty()) {
            CloudNetLobbySwitcher.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Title of an inventory cannot be empty");
            return false;
        }
        if (this.inventory.getSize() <= 0 || this.inventory.getSize() % 9 != 0) {
            CloudNetLobbySwitcher.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Size of an inventory must be a multiple of 9 and greater than 0");
            return false;
        }
        Inventory inventory = Bukkit.createInventory(null, this.inventory.getSize(),
                ChatColor.translateAlternateColorCodes('&', this.inventory.getTitle())
        );

        if (this.inventory.getItems() != null && !this.inventory.getItems().isEmpty()) {
            for (SlottedInventoryItem item : this.inventory.getItems()) {
                if (item.getSlot() >= inventory.getSize())
                    continue;
                ItemStack itemStack = item.toBukkit();
                if (itemStack == null)
                    continue;
                if (item.getSlot() < 0) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        inventory.setItem(i, itemStack);
                    }
                    break;
                }

                inventory.setItem(item.getSlot(), itemStack);
            }
        }

        if (this.inventory.getGroupedItems() != null && !this.inventory.getGroupedItems().isEmpty()) {
            for (GroupedInventoryItems groupedItems : this.inventory.getGroupedItems()) {
                serverInfos.stream().filter(serverInfo -> serverInfo.getGroup().equalsIgnoreCase(groupedItems.getTargetGroup()))
                        .forEach(serverInfo -> {
                            for (int slot : groupedItems.getSlots()) {
                                if (slot >= inventory.getSize() || slot < 0)
                                    continue;

                                if (this.findServerBySlot(slot) != null)
                                    continue;

                                ItemStack serverItem = GroupConfig.this.createServerItem(groupedItems, serverInfo);
                                if (serverItem != null) {
                                    inventory.setItem(slot, serverItem);

                                    GroupConfig.this.serverInfos.put(serverInfo.getName(), slot);
                                }
                                break;
                            }
                        });
            }
        }

        this.bukkitInventory = inventory;
        return true;
    }

    private void updateInventory() {
        /*for (HumanEntity viewer : this.bukkitInventory.getViewers()) {
            if (viewer instanceof Player) {
                ((Player) viewer).updateInventory();
            }
        }*/
    }

    public ItemStack createServerItem(GroupedInventoryItems inventoryItems, SimplifiedServerInfo serverInfo) {
        if (inventoryItems.getItem() == null)
            return null;

        Map<String, String> replacements = new HashMap<>();
        replacements.put("%server_name%", serverInfo.getName());
        replacements.put("%server_group%", serverInfo.getGroup());
        replacements.put("%server_id%", Integer.toString(serverInfo.getId()));
        replacements.put("%launcher%", serverInfo.getLauncher());
        replacements.put("%online_count%", Integer.toString(serverInfo.getOnlineCount()));

        return inventoryItems.getItem().toBukkit(replacements);
    }

    public void handleServerInfoUpdate(SimplifiedServerInfo oldServerInfo, SimplifiedServerInfo newServerInfo) {
        if (this.bukkitInventory == null)
            return;
        if (this.serverInfos == null)
            this.serverInfos = new HashMap<>();

        String name = oldServerInfo == null ? newServerInfo.getName() : oldServerInfo.getName();

        if (!this.serverInfos.containsKey(name)) {
            if (newServerInfo == null)
                return;
            GroupedInventoryItems items = this.inventory.getGroupedItems().stream()
                    .filter(groupedItems -> newServerInfo.getGroup().equalsIgnoreCase(groupedItems.getTargetGroup())).findFirst()
                    .orElse(null);
            if (items == null)
                return;
            for (int slot : items.getSlots()) {
                if (slot >= this.bukkitInventory.getSize() || slot < 0)
                    continue;

                if (this.findServerBySlot(slot) != null)
                    continue;

                ItemStack serverItem = this.createServerItem(items, newServerInfo);
                if (serverItem != null) {
                    this.bukkitInventory.setItem(slot, serverItem);

                    this.serverInfos.put(name, slot);
                }
                break;
            }

            if (!this.serverInfos.containsKey(name))
                return;
        }

        int slot = this.serverInfos.get(name);
        ItemStack currentItem = this.bukkitInventory.getItem(slot);
        if (currentItem == null) {
            this.serverInfos.remove(name);
            return;
        }

        if (newServerInfo == null) {
            this.bukkitInventory.setItem(slot, null);
            this.serverInfos.remove(name);
            this.inventory.getItems().stream().filter(item -> item.getSlot() == slot || item.getSlot() == -1).findFirst()
                    .ifPresent(item -> {
                        ItemStack itemStack = item.toBukkit();
                        if (itemStack == null)
                            return;
                        this.bukkitInventory.setItem(slot, itemStack);
                    });
            this.updateInventory();
            return;
        }

        this.inventory.getGroupedItems().stream()
                .filter(items -> items.getTargetGroup().equalsIgnoreCase(newServerInfo.getGroup()))
                .findFirst()
                .ifPresent(inventoryItems -> {
                    ItemStack serverItem = this.createServerItem(inventoryItems, newServerInfo);
                    if (serverItem != null) {
                        this.bukkitInventory.setItem(slot, serverItem);
                        this.updateInventory();
                    }
                });
    }

    public boolean handleClick(Inventory inventory, Player player, int rawSlot, int slot) {
        if (inventory.equals(this.bukkitInventory)) {
            if (this.serverInfos == null)
                this.serverInfos = new HashMap<>();
            String server = this.findServerBySlot(slot);
            if (server != null) {
                GroupedInventoryItems items = this.inventory.getGroupedItems().stream()
                        .filter(inventoryItems -> Arrays.stream(inventoryItems.getSlots()).anyMatch(value -> value == slot))
                        .findFirst().orElse(null);
                if (items == null)
                    return true;
                if (items.getNeededPermission() != null && !items.getNeededPermission().isEmpty() &&
                        !player.hasPermission(items.getNeededPermission())) {
                    if (items.getNoPermissionMessage() != null && !items.getNoPermissionMessage().isEmpty()) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&',
                                        items.getNoPermissionMessage().replace("%server_name%", server)
                                )
                        );
                    }
                    return true;
                }
                if (server.equalsIgnoreCase(CloudNetLobbySwitcher.getInstance().getSelfServerName())) {
                    if (this.alreadyConnectedMessage != null) {
                        player.sendMessage(
                                ChatColor.translateAlternateColorCodes('&',
                                        this.alreadyConnectedMessage.replace("%server_name%", server)
                                )
                        );
                    }
                    return true;
                }
                CloudNetLobbySwitcher.getInstance().sendPlayer(player, server);
                return true;
            }
            return true;
        } else if (inventory.getType().equals(InventoryType.PLAYER)) {
            if (this.inventoryClickableOpenItem != null && this.inventoryClickableOpenItem.getSlot() == slot) {
                this.openInventory(player);
                return true;
            } else if (this.hotbarClickableOpenItem != null && this.hotbarClickableOpenItem.getSlot() == slot) {
                return true;
            }
        }
        return false;
    }

    public boolean handleDrop(Player player, int slot) {
        return (this.hotbarClickableOpenItem != null && this.hotbarClickableOpenItem.getSlot() == slot) ||
                (this.inventoryClickableOpenItem != null && this.inventoryClickableOpenItem.getSlot() == slot);
    }

    public boolean handleInteract(Player player, int slot) {
        if (this.hotbarClickableOpenItem != null && this.hotbarClickableOpenItem.getSlot() == slot) {
            this.openInventory(player);
            return true;
        } else if (this.hotbarItems != null && !this.hotbarItems.isEmpty()) {
            GroupConnectableInventoryItem item = this.hotbarItems.stream().filter(i -> i.getSlot() == slot).findFirst().orElse(null);
            if (item != null) {
                if (item.getNeededPermission() != null && !player.hasPermission(item.getNeededPermission())) {
                    if (item.getNoPermissionMessage() != null) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', item.getNoPermissionMessage()));
                    }
                    return true;
                }

                CloudNetLobbySwitcher.getInstance().sendPlayerToGroup(player, item.getTargetGroup());

                return true;
            }
        }
        return false;
    }

    public void openInventory(Player player) {
        if (this.bukkitInventory != null) {
            player.openInventory(this.bukkitInventory);
        }
    }

    private String findServerBySlot(int slot) {
        Map.Entry<String, Integer> e = this.serverInfos.entrySet().stream().filter(entry -> entry.getValue() == slot)
                .findFirst()
                .orElse(null);
        return e == null ? null : e.getKey();
    }

}
