package org.spongepowered.lantern.registry.type;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.lantern.entity.living.player.LanternGameMode;
import org.spongepowered.lantern.registry.CatalogRegistryModule;
import org.spongepowered.lantern.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class GameModeRegistryModule implements CatalogRegistryModule<GameMode> {

    @RegisterCatalog(GameModes.class)
    public final HashMap<String, GameMode> gameModeMappings = Maps.newHashMap();

    @Override
    public Optional<GameMode> getById(String id) {
        return Optional.of(this.gameModeMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<GameMode> getAll() {
        return ImmutableList.copyOf(gameModeMappings.values());
    }

    @Override
    public void registerDefaults() {
        this.gameModeMappings.put("survival", new LanternGameMode(0, "survival"));
        this.gameModeMappings.put("creative", new LanternGameMode(1, "creative"));
        this.gameModeMappings.put("adventure", new LanternGameMode(2, "adventure"));
        this.gameModeMappings.put("spectator", new LanternGameMode(3, "spectator"));
        this.gameModeMappings.put("not_set", new LanternGameMode(-1, ""));
    }
}
