package eu.thesystems.cloudnet.lobbyswitcher.core.config;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import eu.thesystems.cloudnet.lobbyswitcher.core.config.items.ConfigurableInventoryItem;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GroupedInventoryItems {
    private String targetGroup;
    private int[] slots;
    private String neededPermission;
    private String noPermissionMessage;
    private ConfigurableInventoryItem item;
}
