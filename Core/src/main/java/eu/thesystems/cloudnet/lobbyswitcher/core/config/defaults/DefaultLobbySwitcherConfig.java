package eu.thesystems.cloudnet.lobbyswitcher.core.config.defaults;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.*;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.ConfigurableInventoryItem;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.GroupConnectableInventoryItem;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.SlottedInventoryItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;

public class DefaultLobbySwitcherConfig extends LobbySwitcherConfig {
    public DefaultLobbySwitcherConfig() {
        super(
                Arrays.asList(
                        new GroupConfig(
                                new String[]{CloudNetLobbySwitcher.getInstance().getSelfGroup()},
                                "&cYou are already on &e%server_name%",
                                new SwitcherInventory(
                                        "&eLobbies",
                                        9 * 3,
                                        Arrays.asList(
                                                new GroupedInventoryItems(
                                                        "Lobby",
                                                        new int[]{
                                                                0, 1, 2,
                                                                9, 10, 11,
                                                                18, 19, 20
                                                        },
                                                        "",
                                                        "",
                                                        new ConfigurableInventoryItem(
                                                                Material.EMERALD.name(),
                                                                1,
                                                                (short) 0,
                                                                "&7%server_name%",
                                                                Arrays.asList(
                                                                        "&7Running on &e%launcher%",
                                                                        "&7OnlineCount: &e%online_count%"
                                                                ),
                                                                null,
                                                                null
                                                        )
                                                ),
                                                new GroupedInventoryItems(
                                                        "PremiumLobby",
                                                        new int[]{
                                                                6, 7, 8,
                                                                15, 16, 17,
                                                                24, 25, 26
                                                        },
                                                        "cloudnet.premiumlobby",
                                                        "&cYou are not allowed to join &e%server_name%",
                                                        new ConfigurableInventoryItem(
                                                                Material.GOLD_INGOT.name(),
                                                                1,
                                                                (short) 0,
                                                                "&7%server_name%",
                                                                Arrays.asList(
                                                                        "&7Running on &e%launcher%",
                                                                        "&7OnlineCount: &e%online_count%"
                                                                ),
                                                                Arrays.asList(
                                                                        new ConfigurableItemEnchant(
                                                                                Enchantment.DAMAGE_ALL.getName(),
                                                                                8
                                                                        )
                                                                ),
                                                                Arrays.asList("HIDE_ENCHANTS")
                                                        )
                                                )
                                        ),
                                        Arrays.asList(
                                                new SlottedInventoryItem(
                                                        -1,
                                                        "STAINED_GLASS_PANE",
                                                        1,
                                                        (short) 2,
                                                        "§c",
                                                        null,
                                                        null,
                                                        null
                                                )
                                        )
                                ),
                                new SlottedInventoryItem(
                                        13,
                                        Material.WATCH.name(),
                                        1,
                                        (short) 0,
                                        "&eLobbySwitcher",
                                        Arrays.asList(
                                                "&7Click to switch the Lobby"
                                        ),
                                        null,
                                        null
                                ),
                                new SlottedInventoryItem(
                                        4,
                                        Material.WATCH.name(),
                                        1,
                                        (short) 0,
                                        "&eLobbySwitcher",
                                        Arrays.asList(
                                                "&7Click to switch the Lobby"
                                        ),
                                        null,
                                        null
                                ),
                                Arrays.asList(
                                        new GroupConnectableInventoryItem(
                                                "Silentlobby",
                                                "cloudnet.silentlobby",
                                                "&cYou are not allowed to join &eSilentLobby",
                                                2,
                                                "TNT",
                                                1,
                                                (short) 0,
                                                "&eSilent&cLobby",
                                                Arrays.asList(
                                                        "&7Click to connect to the SilentLobby"
                                                ),
                                                null,
                                                null
                                        )
                                )
                        )
                )
        );
    }
}
