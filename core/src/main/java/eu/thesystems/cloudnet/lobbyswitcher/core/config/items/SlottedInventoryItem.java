package eu.thesystems.cloudnet.lobbyswitcher.core.config.items;
/*
 * Created by Mc_Ruben on 03.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.config.ConfigurableItemEnchant;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class SlottedInventoryItem extends ConfigurableInventoryItem {
    private int slot;

    public SlottedInventoryItem(int slot, String material, int amount, short subId, String displayName, List<String> lore, Collection<ConfigurableItemEnchant> enchants, Collection<String> flags) {
        super(material, amount, subId, displayName, lore, enchants, flags, 0);
        this.slot = slot;
    }

    public SlottedInventoryItem(int slot, String material, int amount, short subId, String displayName, List<String> lore, Collection<ConfigurableItemEnchant> enchants, Collection<String> flags, int ratelimits) {
        super(material, amount, subId, displayName, lore, enchants, flags, ratelimits);
        this.slot = slot;
    }
}
