package eu.thesystems.cloudnet.lobbyswitcher.core.listener;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.GroupConfig;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.GroupConnectableInventoryItem;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.SlottedInventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SwitchListener implements Listener {

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || event.getClickedInventory() == null || event.getCurrentItem() == null)
            return;

        GroupConfig groupConfig = CloudNetLobbySwitcher.getInstance().getSelfGroupConfig();
        if (groupConfig != null) {
            Player player = (Player) event.getWhoClicked();

            if (groupConfig.handleClick(event.getClickedInventory(), player, event.getRawSlot(), event.getSlot())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleDrop(PlayerDropItemEvent event) {
        GroupConfig groupConfig = CloudNetLobbySwitcher.getInstance().getSelfGroupConfig();
        if (groupConfig != null) {
            Player player = event.getPlayer();

            if (groupConfig.handleDrop(player, player.getInventory().getHeldItemSlot())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent event) {
        GroupConfig groupConfig = CloudNetLobbySwitcher.getInstance().getSelfGroupConfig();
        if (groupConfig != null) {
            Player player = event.getPlayer();

            if (groupConfig.handleInteract(player, player.getInventory().getHeldItemSlot())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        GroupConfig groupConfig = CloudNetLobbySwitcher.getInstance().getSelfGroupConfig();
        if (groupConfig != null) {
            Player player = event.getPlayer();

            Bukkit.getScheduler().runTask(CloudNetLobbySwitcher.getInstance().getPlugin(), () -> {
                for (SlottedInventoryItem item : Arrays.asList(groupConfig.getHotbarClickableOpenItem(), groupConfig.getInventoryClickableOpenItem())) {
                    if (item == null || item.getSlot() < 0 || item.getSlot() >= player.getInventory().getSize())
                        continue;
                    ItemStack itemStack = item.toBukkit();
                    if (itemStack != null) {
                        player.getInventory().setItem(item.getSlot(), itemStack);
                    }
                }
                if (groupConfig.getHotbarItems() != null && !groupConfig.getHotbarItems().isEmpty()) {
                    for (GroupConnectableInventoryItem item : groupConfig.getHotbarItems()) {
                        if (item == null || item.getSlot() < 0 || item.getSlot() >= player.getInventory().getSize() || (item.getNeededPermission() != null && !player.hasPermission(item.getNeededPermission())))
                            continue;
                        ItemStack itemStack = item.toBukkit();
                        if (itemStack != null) {
                            player.getInventory().setItem(item.getSlot(), itemStack);
                        }
                    }
                }
            });
        }
    }

}
