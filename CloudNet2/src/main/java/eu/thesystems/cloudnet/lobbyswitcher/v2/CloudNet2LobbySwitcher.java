package eu.thesystems.cloudnet.lobbyswitcher.v2;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.dytanic.cloudnet.lib.utility.document.Document;
import eu.thesystems.cloudnet.lobbyswitcher.core.CloudNetLobbySwitcher;
import eu.thesystems.cloudnet.lobbyswitcher.core.servers.SimplifiedServerInfo;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudNet2LobbySwitcher extends JavaPlugin {
    private CloudNetLobbySwitcher switcher = new CloudNetLobbySwitcher() {
        @Override
        public String getSelfGroup() {
            return CloudAPI.getInstance().getGroup();
        }

        @Override
        public String getSelfServerName() {
            return CloudAPI.getInstance().getServerId();
        }

        @Override
        public void sendPlayer(Player player, String server) {
            CloudAPI.getInstance().sendCustomSubProxyMessage("cloudnet_internal", "sendPlayer",
                    new Document("uniqueId", player.getUniqueId())
                            .append("server", server)
            );
        }

        @Override
        public void sendPlayerToGroup(Player player, String group) {
            ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
            dataOutput.writeUTF("Connect");
            dataOutput.writeUTF(group);
            player.sendPluginMessage(CloudNet2LobbySwitcher.this, "cloudnet:main", dataOutput.toByteArray());
        }

        @Override
        public void getOnlineServers(Consumer<List<SimplifiedServerInfo>> consumer) {
            consumer.accept(CloudAPI.getInstance().getServers().stream()
                    .map(serverInfo -> createServerInfo(serverInfo))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
    };

    public SimplifiedServerInfo createServerInfo(ServerInfo serverInfo) {
        return serverInfo.isOnline() ? new SimplifiedServerInfo(
                serverInfo.getServiceId().getGroup(),
                serverInfo.getServiceId().getServerId(),
                serverInfo.getServiceId().getId(),
                serverInfo.getServiceId().getWrapperId(),
                serverInfo.getOnlineCount()
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

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "cloudnet:main");

        CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(new NetworkHandlerAdapter() {

            //add not needed, because the server won't be online when started

            @Override
            public void onServerInfoUpdate(ServerInfo serverInfo) {
                CloudNet2LobbySwitcher.this.switcher.handleServerInfoUpdate(null, createServerInfo(serverInfo));
            }

            @Override
            public void onServerRemove(ServerInfo serverInfo) {
                CloudNet2LobbySwitcher.this.switcher.handleServerRemove(createServerInfo(serverInfo));
            }
        });
    }

    @Override
    public void onDisable() {
        this.switcher.disable();
    }
}
