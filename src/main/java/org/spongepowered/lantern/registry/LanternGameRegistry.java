/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.lantern.registry;

import com.google.inject.Singleton;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.statistic.BlockStatistic;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class LanternGameRegistry implements GameRegistry {

    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends ResettableBuilder<? super T>> T createBuilder(Class<T> builderClass) throws IllegalArgumentException {
        return null; //TODO: Implement
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<EntityStatistic> getEntityStatistic(StatisticGroup statisticGroup, EntityType entityType) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<ItemStatistic> getItemStatistic(StatisticGroup statisticGroup, ItemType itemType) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<BlockStatistic> getBlockStatistic(StatisticGroup statisticGroup, BlockType blockType) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<TeamStatistic> getTeamStatistic(StatisticGroup statisticGroup, TextColor teamColor) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Statistic> getStatistics(StatisticGroup statisticGroup) {
        return null; //TODO: Implement
    }

    @Override
    public void registerStatistic(Statistic stat) {
        //TODO: Implement
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return null; //TODO: Implement
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String name) {
        return null; //TODO: Implement
    }

    @Override
    public Favicon loadFavicon(String raw) throws IOException {
        return null; //TODO: Implement
    }

    @Override
    public Favicon loadFavicon(Path path) throws IOException {
        return null; //TODO: Implement
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        return null; //TODO: Implement
    }

    @Override
    public Favicon loadFavicon(InputStream in) throws IOException {
        return null; //TODO: Implement
    }

    @Override
    public Favicon loadFavicon(BufferedImage image) throws IOException {
        return null; //TODO: Implement
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<ResourcePack> getResourcePackById(String id) {
        return null; //TODO: Implement
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        return null; //TODO: Implement
    }

    @Override
    public void registerWorldGeneratorModifier(WorldGeneratorModifier modifier) {
        //TODO: Implement
    }

    @Override
    public PopulatorFactory getPopulatorFactory() {
        return null; //TODO: Implement
    }

    @Override
    public ExtentBufferFactory getExtentBufferFactory() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return null; //TODO: Implement
    }
}
