package org.spongepowered.lantern;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockSnapshotBuilder;
import org.spongepowered.api.block.BlockStateBuilder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.ImmutableDataRegistry;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.data.value.ValueBuilder;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.EntitySnapshotBuilder;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.damage.source.ProjectileDamageSourceBuilder;
import org.spongepowered.api.event.cause.entity.spawn.BlockSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.BreedingSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.MobSpawnerSpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCauseBuilder;
import org.spongepowered.api.event.cause.entity.spawn.WeatherSpawnCauseBuilder;
import org.spongepowered.api.item.FireworkEffectBuilder;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.ScoreboardBuilder;
import org.spongepowered.api.scoreboard.TeamBuilder;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.objective.ObjectiveBuilder;
import org.spongepowered.api.statistic.BlockStatistic;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticBuilder;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.statistic.achievement.AchievementBuilder;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.explosion.ExplosionBuilder;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

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
    public <T> Optional<T> createBuilderOfType(Class<T> builderClass) {
        return null; //TODO: Implement
    }

    @Override
    public BlockStateBuilder createBlockStateBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public BlockSnapshotBuilder createBlockSnapshotBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public EntitySnapshotBuilder createEntitySnapshotBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ItemStackBuilder createItemBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public TradeOfferBuilder createTradeOfferBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public FireworkEffectBuilder createFireworkEffectBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public PotionEffectBuilder createPotionEffectBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ObjectiveBuilder createObjectiveBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public TeamBuilder createTeamBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ScoreboardBuilder createScoreboardBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public StatisticBuilder createStatisticBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public StatisticBuilder.EntityStatisticBuilder createEntityStatisticBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public StatisticBuilder.BlockStatisticBuilder createBlockStatisticBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public StatisticBuilder.ItemStatisticBuilder createItemStatisticBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public StatisticBuilder.TeamStatisticBuilder createTeamStatisticBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public AchievementBuilder createAchievementBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public WorldBuilder createWorldBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ExplosionBuilder createExplosionBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ValueBuilder createValueBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ParticleEffectBuilder createParticleEffectBuilder(ParticleType particle) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Career> getCareers(Profession profession) {
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
    public Favicon loadFavicon(File file) throws IOException {
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
    public GameDictionary getGameDictionary() {
        return null; //TODO: Implement
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public DataManipulatorRegistry getManipulatorRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public ImmutableDataRegistry getImmutableDataRegistry() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<ResourcePack> getById(String id) {
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

    @Override
    public BlockDamageSourceBuilder createBlockDamageSourceBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public DamageSourceBuilder createDamageSourceBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public EntityDamageSourceBuilder createEntityDamageSourceBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public FallingBlockDamageSourceBuilder createFallingBlockDamageSourceBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public ProjectileDamageSourceBuilder createProjectileDamageSourceBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public SpawnCauseBuilder createSpawnCauseBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public BlockSpawnCauseBuilder createBlockSpawnCauseBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public EntitySpawnCauseBuilder createEntitySpawnCauseBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public BreedingSpawnCauseBuilder createBreedingSpawnCauseBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public MobSpawnerSpawnCauseBuilder createMobSpawnerSpawnCauseBuilder() {
        return null; //TODO: Implement
    }

    @Override
    public WeatherSpawnCauseBuilder createWeatherSpawnCauseBuilder() {
        return null; //TODO: Implement
    }
}
