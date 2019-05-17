package eu.thesystems.cloudnet.lobbyswitcher.core.config.items;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.ConfigurableItemEnchant;
import lombok.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ConfigurableInventoryItem {
    private String material;
    private int amount;
    private short subId;
    private String displayName;
    private List<String> lore;
    private Collection<ConfigurableItemEnchant> enchants;
    private Collection<String> flags;

    public ItemStack toBukkit() {
        return this.toBukkit(Collections.emptyMap());
    }

    public ItemStack toBukkit(Map<String, String> replacements) {
        Material material = Material.matchMaterial(this.material);
        if (material == null) {
            CloudNetLobbySwitcher.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Material \"" + this.material + "\" does not exist");
            return null;
        }
        ItemStack itemStack = new ItemStack(material);
        if (this.amount > 0 && this.amount <= 64)
            itemStack.setAmount(this.amount);
        if (this.subId >= 0)
            itemStack.setDurability(this.subId);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (this.displayName != null)
            itemMeta.setDisplayName(this.replace(this.displayName, replacements));
        if (this.lore != null && !this.lore.isEmpty())
            itemMeta.setLore(this.lore.stream().map(s -> this.replace(s, replacements)).collect(Collectors.toList()));
        if (this.enchants != null && !this.enchants.isEmpty()) {
            for (ConfigurableItemEnchant enchant : this.enchants) {
                Enchantment enchantment = Enchantment.getByName(enchant.getName());
                if (enchantment == null) {
                    CloudNetLobbySwitcher.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Enchantment \"" + enchant.getName() + "\" does not exist");
                    continue;
                }
                if (enchant.getLevel() <= 0)
                    continue;

                itemMeta.addEnchant(enchantment, enchant.getLevel(), true);
            }
        }
        if (this.flags != null && !this.flags.isEmpty()) {
            for (String name : this.flags) {
                ItemFlag flag;
                try {
                    flag = ItemFlag.valueOf(name);
                } catch (Exception e) {
                    CloudNetLobbySwitcher.getInstance().getPlugin().getLogger().log(
                            Level.SEVERE,
                            "ItemFlag \"" + name + "\" does not exist (Available flags: " +
                                    Arrays.stream(ItemFlag.values()).map(Enum::name).collect(Collectors.joining(", ")) +
                                    ")"
                    );
                    continue;
                }

                itemMeta.addItemFlags(flag);
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private String replace(String s, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
