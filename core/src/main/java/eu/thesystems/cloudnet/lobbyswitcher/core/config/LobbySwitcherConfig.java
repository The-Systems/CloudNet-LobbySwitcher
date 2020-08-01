package eu.thesystems.cloudnet.lobbyswitcher.core.config;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class LobbySwitcherConfig {

    private Collection<GroupConfig> groupConfigs;

}
