package org.spongepowered.lantern.network.handler.status;

import static org.spongepowered.api.data.DataQuery.of;

import com.flowpowered.networking.MessageHandler;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.translator.ConfigurateTranslator;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Texts;
import org.spongepowered.lantern.Lantern;
import org.spongepowered.lantern.LanternServer;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.SpongeVersion;
import org.spongepowered.lantern.network.LanternSession;
import org.spongepowered.lantern.network.message.status.StatusRequestMessage;
import org.spongepowered.lantern.network.message.status.StatusResponseMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class StatusRequestHandler implements MessageHandler<LanternSession, StatusRequestMessage> {

    @Override
    @SuppressWarnings("unchecked")
    public void handle(LanternSession session, StatusRequestMessage message) {
        LanternServer server = session.getServer();

        List<GameProfile> online = server.getOnlinePlayers().stream().map(User::getProfile).collect(Collectors.toList());

        ClientPingServerEvent.Response.Players players = SpongeEventFactory
                .createClientPingServerEventResponsePlayers(
                        online,
                        server.getMaxPlayers(),
                        online.size()
                );
        ClientPingServerEvent.Response response = SpongeEventFactory
                .createClientPingServerEventResponse(
                        Sponge.getGame().getServer().getMotd(),
                        server.getFavicon(),
                        Optional.of(players),
                        SpongeVersion.MINECRAFT_VERSION
                );

        ClientPingServerEvent event = SpongeEventFactory.createClientPingServerEvent(new LanternStatusClient(session), response);
        Lantern.post(event);

        DataContainer data = new MemoryDataContainer();

        data.set(of("version", "name"), "Lantern " + SpongeVersion.IMPLEMENTATION_VERSION);
        data.set(of("version", "protocol"), SpongeVersion.MINECRAFT_VERSION.getProtocol());
        event.getResponse().getPlayers().ifPresent(p -> {
            data.set(of("players", "max"), p.getMax());
            data.set(of("players", "online"), p.getProfiles().size());
            data.set(of("players", "sample"), p.getProfiles());
        });
        data.set(of("description.text"), Texts.json().to(event.getResponse().getDescription()));
        event.getResponse().getFavicon().ifPresent(favicon -> {
            data.set(of("favicon"), favicon);
        });

        ConfigurationNode node = ConfigurateTranslator.instance().translateData(data);
        StringWriter writer = new StringWriter();
        try {
            GsonConfigurationLoader.builder().setIndent(0).setSink(() -> new BufferedWriter(writer)).build().save(node);
        } catch (IOException e) {
            // How?
            SpongeImpl.getLogger().error("Unable to send status!", e);
            return;
        }

        // send it off
        session.send(new StatusResponseMessage(writer.toString().replaceAll("\n", "")));
    }
}
