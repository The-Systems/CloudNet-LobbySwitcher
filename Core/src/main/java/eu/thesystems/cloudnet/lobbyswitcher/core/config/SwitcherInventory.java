package eu.thesystems.cloudnet.lobbyswitcher.core.config;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.SlottedInventoryItem;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SwitcherInventory {
    private String title;
    private int size;
    private Collection<GroupedInventoryItems> groupedItems;
    private Collection<SlottedInventoryItem> items;
}
