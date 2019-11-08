package eu.thesystems.cloudnet.lobbyswitcher.v3;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeConstants;
import de.dytanic.cloudnet.ext.bridge.ServiceInfoSnapshotUtil;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.servers.SimplifiedServerInfo;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet3LobbySwitcher extends JavaPlugin {

    @Getter
    private CloudNetLobbySwitcher switcher = new CloudNetLobbySwitcher() {
        @Override
        public String getSelfGroup() {
            return Wrapper.getInstance().getServiceId().getTaskName();
        }

        @Override
        public String getSelfServerName() {
            return Wrapper.getInstance().getServiceId().getName();
        }

        @Override
        public void sendPlayer(Player player, String server) {
            CloudNetDriver.getInstance().getMessenger().sendChannelMessage(
                    BridgeConstants.BRIDGE_CUSTOM_MESSAGING_CHANNEL_PLAYER_API_CHANNEL_NAME,
                    "send_on_proxy_player_to_server",
                    new JsonDocument()
                            .append("uniqueId", player.getUniqueId())
                            .append("name", player.getName())
                            .append("serviceName", server)
            );
        }

        @Override
        public void sendPlayerToGroup(Player player, String group) {
            CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync(group).onComplete((task, serviceInfoSnapshots) -> {
                if (!serviceInfoSnapshots.isEmpty()) {
                    serviceInfoSnapshots.stream().findAny().ifPresent(serviceInfoSnapshot -> sendPlayer(player, serviceInfoSnapshot.getServiceId().getName()));
                }
            });
        }

        @Override
        public void getOnlineServers(Consumer<List<SimplifiedServerInfo>> consumer) {
            CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync()
                    .onComplete((task, serviceInfoSnapshots) -> consumer.accept(
                            serviceInfoSnapshots.stream()
                                    .map(CloudNet3LobbySwitcher.this::createServerInfo)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList())
                    ))
                    .onFailure(throwable -> consumer.accept(Collections.emptyList()))
                    .onCancelled(task -> consumer.accept(Collections.emptyList()));
        }
    };

    SimplifiedServerInfo createServerInfo(ServiceInfoSnapshot serviceInfoSnapshot) {
        return ServiceInfoSnapshotUtil.isOnline(serviceInfoSnapshot) ? new SimplifiedServerInfo(
                serviceInfoSnapshot.getServiceId().getTaskName(),
                serviceInfoSnapshot.getServiceId().getName(),
                serviceInfoSnapshot.getServiceId().getTaskServiceId(),
                serviceInfoSnapshot.getServiceId().getNodeUniqueId(),
                ServiceInfoSnapshotUtil.getOnlineCount(serviceInfoSnapshot)
        ) : null;
    }

    @Override
    public void onLoad() {
        this.switcher.loadConfig();
    }

    @Override
    public void onEnable() {
        if (!this.switcher.enable(this))
            return;

        Wrapper.getInstance().getEventManager().registerListener(new CloudNet3ServersListener(this));
    }

    @Override
    public void onDisable() {
        Wrapper.getInstance().getEventManager().unregisterListeners(this.getClassLoader());

        this.switcher.disable();
    }

}
