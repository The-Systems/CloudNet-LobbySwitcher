package eu.thesystems.cloudnet.lobbyswitcher.core.config.items;
/*
 * Created by Mc_Ruben on 05.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.config.ConfigurableItemEnchant;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class GroupConnectableInventoryItem extends SlottedInventoryItem {
    private String targetGroup;
    private String neededPermission;
    private String noPermissionMessage;

    public GroupConnectableInventoryItem(String targetGroup, String neededPermission, String noPermissionMessage, int slot, String material, int amount, short subId, String displayName, List<String> lore, Collection<ConfigurableItemEnchant> enchants, Collection<String> flags) {
        super(slot, material, amount, subId, displayName, lore, enchants, flags);
        this.targetGroup = targetGroup;
        this.neededPermission = neededPermission;
        this.noPermissionMessage = noPermissionMessage;
    }
}
