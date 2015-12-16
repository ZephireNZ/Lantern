package org.spongepowered.lantern.network;

import com.flowpowered.networking.AsyncableMessage;
import com.flowpowered.networking.ConnectionManager;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.MessageHandler;
import com.flowpowered.networking.session.BasicSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.CodecException;
import org.spongepowered.api.MinecraftVersion;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.lantern.LanternServer;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.entity.living.player.LanternPlayer;
import org.spongepowered.lantern.network.pipeline.CodecsHandler;
import org.spongepowered.lantern.network.pipeline.CompressionHandler;
import org.spongepowered.lantern.network.pipeline.EncryptionHandler;
import org.spongepowered.lantern.network.pipeline.NoopHandler;
import org.spongepowered.lantern.network.protocol.Protocol;
import org.spongepowered.lantern.network.protocol.ProtocolType;

import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 * @author Graham Edgecombe
 */
public final class LanternSession extends BasicSession {

    /**
     * The server this session belongs to.
     */
    private final LanternServer server;

    /**
     * The connection manager this session belongs to.
     */
    private final ConnectionManager connectionManager;

    /**
     * The Random for this session
     */
    private final Random random = new Random();

    /**
     * A queue of incoming and unprocessed messages.
     */
    private final Queue<Message> messageQueue = new ArrayDeque<>();

    /**
     * The remote address of the connection.
     */
    private InetSocketAddress address;

    /**
     * The state of the connection
     */
    private boolean online = false;
    /**
     * The verify token used in authentication
     */
    private byte[] verifyToken;

    /**
     * The verify username used in authentication
     */
    private String verifyUsername;

    /**
     * A message describing under what circumstances the connection ended.
     */
    private String quitReason;

    /**
     * The hostname used to connect.
     */
    @Nullable
    private InetSocketAddress virtualHost;

    /**
     * Data regarding a user who has connected through a proxy, used to
     * provide online-mode UUID and properties and other data even if the
     * server is running in offline mode. Null for non-proxied sessions.
     */
    @Nullable
    private ProxyData proxyData;

    /**
     * The player associated with this session (if there is one).
     */
    @Nullable
    private LanternPlayer player;

    /**
     * The ID of the last ping message sent, used to ensure the client responded correctly.
     */
    private int pingMessageId;

    /**
     * Stores the last block placement message sent, see BlockPlacementHandler.
     */
//    private BlockPlacementMessage previousPlacement; //TODO

    /**
     * The number of ticks until previousPlacement must be cleared.
     */
    private int previousPlacementTicks;
    
    /**
     * If the connection has been disconnected
     */
    private boolean disconnected;

    /**
     * The version of the client that is connecting.
     */
    @Nullable
    private MinecraftVersion version;

    /**
     * Creates a new session.
     * @param server The server this session belongs to.
     * @param channel The channel associated with this session.
     */
    public LanternSession(LanternServer server, Channel channel, ConnectionManager connectionManager) {
        super(channel, ProtocolType.HANDSHAKE.getProtocol());
        this.server = server;
        this.connectionManager = connectionManager;
        address = super.getAddress();
    }

    /**
     * Gets the server associated with this session.
     * @return The server.
     */
    public LanternServer getServer() {
        return server;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Auxiliary state

    /**
     * Get the randomly-generated verify token for this session.
     * @return The verify token
     */
    public byte[] getVerifyToken() {
        return verifyToken;
    }

    /**
     * Sets the verify token of this session.
     * @param verifyToken The verify token.
     */
    public void setVerifyToken(byte[] verifyToken) {
        this.verifyToken = verifyToken;
    }

    /**
     * Gets the verify username for this session.
     * @return The verify username.
     */
    public String getVerifyUsername() {
        return verifyUsername;
    }

    /**
     * Sets the verify username for this session.
     * @param verifyUsername The verify username.
     */
    public void setVerifyUsername(String verifyUsername) {
        this.verifyUsername = verifyUsername;
    }

    /**
     * Get the {@link ProxyData} for this session if available.
     * @return The proxy data to use, or null for an unproxied connection.
     */
    public ProxyData getProxyData() {
        return proxyData;
    }

    /**
     * Set the {@link ProxyData} for this session.
     * @param proxyData The proxy data to use.
     */
    public void setProxyData(ProxyData proxyData) {
        this.proxyData = proxyData;
        address = proxyData.getAddress();
        virtualHost = proxyData.getVirtualHost();
    }

    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.ofNullable(virtualHost);
    }

    /**
     * Set the hostname the player used to connect to the server.
     * @param host Virtual host
     */
    public void setVirtualHost(InetSocketAddress host) {
        this.virtualHost = host;
    }

    public Optional<MinecraftVersion> getVersion() {
        return Optional.ofNullable(version);
    }

    public void setVersion(@Nullable MinecraftVersion version) {
        this.version = version;
    }

    /**
     * Notify that the session is currently idle.
     */
    public void idle() {
        //TODO: Implement
//        if (pingMessageId == 0 && getProtocol() instanceof PlayProtocol) {
//            pingMessageId = random.nextInt();
//            if (pingMessageId == 0) {
//                pingMessageId++;
//            }
//            send(new PingMessage(pingMessageId));
//        } else {
//            disconnect("Timed out");
//        }
    }

    /**
     * Note that the client has responded to a keep-alive.
     * @param pingId The pingId to check for validity.
     */
    public void pong(long pingId) {
        if (pingId == pingMessageId) {
            pingMessageId = 0;
        }
    }

//    /**
//     * Get the saved previous BlockPlacementMessage for this session.
//     * @return The message.
//     */
    // TODO
//    public BlockPlacementMessage getPreviousPlacement() {
//        return previousPlacement;
//    }

//    /**
//     * Set the previous BlockPlacementMessage for this session.
//     * @param message The message.
//     */
    // TODO
//    public void setPreviousPlacement(BlockPlacementMessage message) {
//        previousPlacement = message;
//        previousPlacementTicks = 2;
//    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player and state management

    /**
     * Get session online state
     * @return true if this session's state is online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Gets the player associated with this session.
     * @return The player, or {@code null} if no player is associated with it.
     */
    public LanternPlayer getPlayer() {
        return player;
    }

    /**
     * Sets the player associated with this session.
     * @param profile The player's profile with name and UUID information.
     * @throws IllegalStateException if there is already a player associated
     * with this session.
     */
    public void setPlayer(GameProfile profile) {
        // TODO: Implement
//        if (player != null) {
//            throw new IllegalStateException("Cannot set player twice");
//        }
//
//        // isActive check here in case player disconnected during authentication
//        if (!isActive()) {
//            // no need to call onDisconnect() since it only does anything if there's a player set
//            return;
//        }
//
//        // initialize the player
//        PlayerDataService.PlayerReader reader = server.getPlayerDataService().beginReadingData(profile.getUniqueId());
//        player = new GlowPlayer(this, profile, reader);
//
//        // isActive check here in case player disconnected after authentication,
//        // but before the GlowPlayer initialization was completed
//        if (!isActive()) {
//            onDisconnect();
//            return;
//        }
//
//        // login event
//        PlayerLoginEvent event = EventFactory.onPlayerLogin(player, hostname);
//        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
//            disconnect(event.getKickMessage(), true);
//            return;
//        }
//
//        //joins the player
//        player.join(this, reader);
//
//        // Kick other players with the same UUID
//        for (GlowPlayer other : getServer().getOnlinePlayers()) {
//            if (other != player && other.getUniqueId().equals(player.getUniqueId())) {
//                other.getSession().disconnect("You logged in from another location.", true);
//                break;
//            }
//        }
//
//        player.getWorld().getRawPlayers().add(player);
//
//        online = true;
//
//        GlowServer.logger.info(player.getName() + " [" + address + "] connected, UUID: " + player.getUniqueId());
//
//        // message and user list
//        String message = EventFactory.onPlayerJoin(player).getJoinMessage();
//        if (message != null && !message.isEmpty()) {
//            server.broadcastMessage(message);
//        }
//
//        // todo: display names are included in the outgoing messages here, but
//        // don't show up on the client. A workaround or proper fix is needed.
//        Message addMessage = new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, player.getUserListEntry());
//        List<UserListItemMessage.Entry> entries = new ArrayList<>();
//        for (GlowPlayer other : server.getOnlinePlayers()) {
//            if (other != player && other.canSee(player)) {
//                other.getSession().send(addMessage);
//            }
//            if (player.canSee(other)) {
//                entries.add(other.getUserListEntry());
//            }
//        }
//        send(new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, entries));
    }

    @Override
    public ChannelFuture sendWithFuture(Message message) {
        if (!isActive()) {
            // discard messages sent if we're closed, since this happens a lot
            return null;
        }
        return super.sendWithFuture(message);
    }

    @Override
    @Deprecated
    public void disconnect() {
        disconnect("No reason specified.");
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel
     * is closed.
     * @param reason The reason for disconnection.
     */
    public void disconnect(String reason) {
        disconnect(reason, false);
    }

    /**
     * Disconnects the session with the specified reason. This causes a
     * KickMessage to be sent. When it has been delivered, the channel
     * is closed.
     * @param reason The reason for disconnection.
     * @param overrideKick Whether to skip the kick event.
     */
    public void disconnect(String reason, boolean overrideKick) {
        // TODO: Implement
//        if (player != null && !overrideKick) {
//            PlayerKickEvent event = EventFactory.onPlayerKick(player, reason);
//            if (event.isCancelled()) {
//                return;
//            }
//
//            reason = event.getReason();
//
//            if (player.isOnline() && event.getLeaveMessage() != null) {
//                server.broadcastMessage(event.getLeaveMessage());
//            }
//        }
//
//        // log that the player was kicked
//        if (player != null) {
//            GlowServer.logger.info(player.getName() + " kicked: " + reason);
//        } else {
//            GlowServer.logger.info("[" + address + "] kicked: " + reason);
//        }
//
//        if (quitReason == null) {
//            quitReason = "kicked";
//        }
//
//        // perform the kick, sending a kick message if possible
//        if (isActive() && (getProtocol() instanceof LoginProtocol || getProtocol() instanceof PlayProtocol)) {
//            // channel is both currently connected and in a protocol state allowing kicks
//            sendWithFuture(new KickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
//        } else {
//            getChannel().close();
//        }
    }

    /**
     * Pulse this session, performing any updates needed.
     */
    void pulse() {
        //TODO: Implement
//        // drop the previous placement if needed
//        if (previousPlacementTicks > 0 && --previousPlacementTicks == 0) {
//            previousPlacement = null;
//        }
//
//        // process messages
//        Message message;
//        while ((message = messageQueue.poll()) != null) {
//            if (disconnected) {
//                // disconnected, we are just seeing extra messages now
//                continue;
//            }
//
//            super.messageReceived(message);
//        }
//
//        // check if the client is disconnected
//        if (disconnected) {
//            connectionManager.sessionInactivated(this);
//
//            if (player == null) {
//                return;
//            }
//
//            player.remove();
//
//            Message userListMessage = UserListItemMessage.removeOne(player.getUniqueId());
//            for (GlowPlayer player : server.getOnlinePlayers()) {
//                if (player.canSee(this.player)) {
//                    player.getSession().send(userListMessage);
//                } else {
//                    player.stopHidingDisconnectedPlayer(this.player);
//                }
//            }
//
//            GlowServer.logger.info(player.getName() + " [" + address + "] lost connection");
//
//            if (player.isSleeping()) {
//                player.leaveBed(false);
//            }
//
//            final String text = EventFactory.onPlayerQuit(player).getQuitMessage();
//            if (online && text != null && !text.isEmpty()) {
//                server.broadcastMessage(text);
//            }
//
//            player = null; // in case we are disposed twice
//        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Pipeline management

    public void setProtocol(ProtocolType protocol) {
        getChannel().flush();

        Protocol proto = protocol.getProtocol();
        updatePipeline("codecs", new CodecsHandler(proto));
        super.setProtocol(proto);
    }

    public void enableEncryption(SecretKey sharedSecret) {
        updatePipeline("encryption", new EncryptionHandler(sharedSecret));
    }

    public void enableCompression(int threshold) {
//        send(new SetCompressionMessage(threshold)); //TODO
        updatePipeline("compression", new CompressionHandler(threshold));
    }

    public void disableCompression() {
//        send(new SetCompressionMessage(-1)); //TODO
        updatePipeline("compression", NoopHandler.INSTANCE);
    }

    private void updatePipeline(String key, ChannelHandler handler) {
        getChannel().pipeline().replace(key, key, handler);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Handler overrides

    @Override
    public void onDisconnect() {
        disconnected = true;
    }

    @Override
    public void messageReceived(Message message) {
        if (message instanceof AsyncableMessage && ((AsyncableMessage) message).isAsync()) {
            // async messages get their handlers called immediately
            super.messageReceived(message);
        } else {
            messageQueue.add(message);
        }
    }

    @Override
    public void onInboundThrowable(Throwable t) {
        if (t instanceof CodecException) {
            // generated by the pipeline, not a network error
            SpongeImpl.getLogger().error("Error in network input", t);
        } else {
            // probably a network-level error - consider the client gone
            if (quitReason == null) {
                quitReason = "read error: " + t;
            }
            getChannel().close();
        }
    }

    @Override
    public void onOutboundThrowable(Throwable t) {
        if (t instanceof CodecException) {
            // generated by the pipeline, not a network error
            SpongeImpl.getLogger().error("Error in network output", t);
        } else {
            // probably a network-level error - consider the client gone
            if (quitReason == null) {
                quitReason = "write error: " + t;
            }
            getChannel().close();
        }
    }

    @Override
    public void onHandlerThrowable(Message message, MessageHandler<?, ?> handle, Throwable t) {
        // can be safely logged and the connection maintained
        SpongeImpl.getLogger().error("Error while handling " + message + " (handler: " + handle.getClass().getSimpleName() + ")", t);
    }

    @Override
    public String toString() {
        if (player != null) {
            return player.getName() + "[" + address + "]";
        } else {
            return "[" + address + "]";
        }
    }
}
