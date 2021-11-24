package eu.thesystems.cloudnet.lobbyswitcher.core;
/*
 * Created by Mc_Ruben on 02.05.2019
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.GroupConfig;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.LobbySwitcherConfig;
import eu.thesystems.cloudnet.lobbyswitcher.core.config.defaults.DefaultLobbySwitcherConfig;
import eu.thesystems.cloudnet.lobbyswitcher.core.listener.SwitchListener;
import eu.thesystems.cloudnet.lobbyswitcher.core.servers.SimplifiedServerInfo;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class CloudNetLobbySwitcher {

    @Getter
    private static CloudNetLobbySwitcher instance;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @Getter
    private LobbySwitcherConfig switcherConfig;

    @Getter
    private Plugin plugin;

    public void loadConfig() {
        instance = this;

        Path config = Paths.get("plugins", "CloudNet-Lobby-Switcher", "config.json");
        if (Files.exists(config)) {
            try (InputStream inputStream = Files.newInputStream(config);
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                this.switcherConfig = GSON.fromJson(reader, LobbySwitcherConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.switcherConfig = new DefaultLobbySwitcherConfig();
            try {
                Files.createDirectories(config.getParent());
                try (OutputStream outputStream = Files.newOutputStream(config);
                     Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                    GSON.toJson(this.switcherConfig, writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean enable(Plugin plugin) {
        GroupConfig selfGroupConfig = this.getSelfGroupConfig();
        if (selfGroupConfig == null) {
            plugin.getLogger().log(Level.SEVERE, "LobbySwitcher is on a server which has no config for the servers inventory");
            return false;
        }
        if (selfGroupConfig.getInventory() == null || selfGroupConfig.getInventory().getGroupedItems() == null) {
            plugin.getLogger().log(Level.SEVERE, "The config contains an error");
            return false;
        }

        this.getOnlineServers(
                s -> selfGroupConfig.getInventory().getGroupedItems().stream()
                        .anyMatch(inventoryItems -> inventoryItems.getTargetGroup().equalsIgnoreCase(s)),
                selfGroupConfig::initInventory
        );

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new SwitchListener(), plugin);
        return true;
    }

    public void disable() {
        instance = null;
    }

    public GroupConfig getSelfGroupConfig() {
        return this.switcherConfig.getGroupConfigs().stream()
                .filter(groupConfig -> Arrays.stream(groupConfig.getTargetGroup()).anyMatch(s -> s.equalsIgnoreCase(this.getSelfGroup())))
                .findFirst().orElse(null);
    }

    public void handleServerInfoUpdate(SimplifiedServerInfo oldServerInfo, SimplifiedServerInfo newServerInfo) {
        if (oldServerInfo == null || newServerInfo == null) {
            this.getSelfGroupConfig().handleServerInfoUpdate(oldServerInfo, newServerInfo);
            return;
        }
        if (oldServerInfo.getOnlineCount() != newServerInfo.getOnlineCount()) {
            this.getSelfGroupConfig().handleServerInfoUpdate(oldServerInfo, newServerInfo);
        }
    }

    public void handleServerAdd(SimplifiedServerInfo serverInfo) {
        this.handleServerInfoUpdate(null, serverInfo);
    }

    public void handleServerRemove(SimplifiedServerInfo serverInfo) {
        this.handleServerInfoUpdate(serverInfo, null);
    }

    public void getOnlineServers(Predicate<String> groupTester, Consumer<List<SimplifiedServerInfo>> consumer) {
        this.getOnlineServers(serverInfos ->
                consumer.accept(serverInfos.stream().filter(serverInfo -> groupTester.test(serverInfo.getGroup())).collect(Collectors.toList()))
        );
    }

    public abstract String getSelfGroup();

    public abstract String getSelfServerName();

    public abstract void sendPlayer(Player player, String server);

    public abstract void sendPlayerToGroup(Player player, String group);

    public abstract void getOnlineServers(Consumer<List<SimplifiedServerInfo>> consumer);

}
