package eu.thesystems.cloudnet.lobbyswitcher.core.servers;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SimplifiedServerInfo {
    private String group;
    private String name;
    private int id;
    private String launcher;
    private int onlineCount;
}
