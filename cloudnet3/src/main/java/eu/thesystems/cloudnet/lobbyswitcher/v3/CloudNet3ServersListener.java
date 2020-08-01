package eu.thesystems.cloudnet.lobbyswitcher.v3;
/*
 * Created by Mc_Ruben on 03.05.2019
 */

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceStopEvent;
import eu.thesystems.cloudnet.lobbyswitcher.core.servers.SimplifiedServerInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CloudNet3ServersListener {

    private CloudNet3LobbySwitcher switcher;

    //add not needed, because the server won't be online when started

    @EventListener
    public void handleServerUpdate(CloudServiceInfoUpdateEvent event) {
        SimplifiedServerInfo serverInfo = this.switcher.createServerInfo(event.getServiceInfo());
        if (serverInfo != null) {
            this.switcher.getSwitcher().handleServerInfoUpdate(null, serverInfo);
        }
    }

    @EventListener
    public void handleServerStop(CloudServiceStopEvent event) {
        SimplifiedServerInfo serverInfo = this.switcher.createServerInfo(event.getServiceInfo());
        if (serverInfo != null) {
            this.switcher.getSwitcher().handleServerRemove(serverInfo);
        }
    }

}
