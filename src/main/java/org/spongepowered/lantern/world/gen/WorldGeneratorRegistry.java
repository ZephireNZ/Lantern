package org.spongepowered.lantern.world.gen;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.lantern.SpongeImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class that handles the registry of world generators. The methods in
 * {@link GameRegistry} simply call methods on this class.
 *
 * @see GameRegistry#getType(Class, String) with {@link WorldGeneratorModifier}
 * @see GameRegistry#getAllOf(Class) with {@link WorldGeneratorModifier}
 * @see GameRegistry#registerWorldGeneratorModifier(WorldGeneratorModifier)
 */
public final class WorldGeneratorRegistry {

    public static WorldGeneratorRegistry getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Map of id => modifier.
     */
    private final Map<String, WorldGeneratorModifier> modifiers = Maps.newHashMap();

    public Map<String, WorldGeneratorModifier> viewModifiersMap() {
        return Collections.unmodifiableMap(this.modifiers);
    }

    public void registerModifier(WorldGeneratorModifier modifier) {
        checkNotNull(modifier, "modifier");
        String id = modifier.getId();
        checkId(id, "World generator ID");

        this.modifiers.put(id.toLowerCase(), modifier);
    }

    private void checkId(String id, String subject) {
        checkArgument(id.indexOf(' ') == -1, subject + " " + id + " may not contain a space");
    }

    /**
     * Gets the string list for the modifiers, for saving purposes.
     *
     * @param modifiers
     *            The modifiers
     * @return The string list
     * @throws IllegalArgumentException
     *             If any of the modifiers is not registered
     */
    public ImmutableCollection<String> toIds(Collection<WorldGeneratorModifier> modifiers) {
        ImmutableList.Builder<String> ids = ImmutableList.builder();
        for (WorldGeneratorModifier modifier : modifiers) {
            checkNotNull(modifier, "modifier (in collection)");
            String id = modifier.getId();
            checkArgument(this.modifiers.containsKey(id.toLowerCase()),
                    "unregistered modifier in collection");
            ids.add(id);
        }
        return ids.build();
    }

    /**
     * Gets the world generator modifiers with the given id. If no world
     * generator modifier can be found with a certain id, a message is logged
     * and the id is skipped.
     *
     * @param ids
     *            The ids
     * @return The modifiers
     */
    public Collection<WorldGeneratorModifier> toModifiers(Collection<String> ids) {
        List<WorldGeneratorModifier> modifiers = Lists.newArrayList();
        for (String id : ids) {
            WorldGeneratorModifier modifier = this.modifiers.get(id.toLowerCase());
            if (modifier != null) {
                modifiers.add(modifier);
            } else {
                SpongeImpl.getLogger().error("World generator modifier with id " + id + " not found. Missing plugin?");
            }
        }
        return modifiers;
    }

    /**
     * Checks that all modifiers are registered.
     *
     * @param modifiers
     *            The modifiers
     * @throws IllegalArgumentException
     *             If a modifier is not registered
     */
    public void checkAllRegistered(Collection<WorldGeneratorModifier> modifiers) {
        // We simply call toIds, that checks all world generators
        toIds(modifiers);
    }

    private static final class Holder {

        private static final WorldGeneratorRegistry INSTANCE = new WorldGeneratorRegistry();

    }

}