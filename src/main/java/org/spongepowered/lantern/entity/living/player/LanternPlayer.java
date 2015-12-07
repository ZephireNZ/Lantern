package org.spongepowered.lantern.entity.living.player;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.data.manipulator.mutable.entity.BanData;
import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingAbilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthScalingData;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.data.manipulator.mutable.entity.MovementSpeedData;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import org.spongepowered.api.data.manipulator.mutable.entity.StatisticData;
import org.spongepowered.api.data.manipulator.mutable.entity.WhitelistData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.lantern.entity.living.LanternLivingEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class LanternPlayer extends LanternLivingEntity implements Player {

    static {
        defaultManipulators.add(AchievementData.class);
        defaultManipulators.add(BanData.class);
        defaultManipulators.add(ExperienceHolderData.class);
        defaultManipulators.add(FlyingAbilityData.class);
        defaultManipulators.add(FlyingData.class);
        defaultManipulators.add(FoodData.class);
        defaultManipulators.add(GameModeData.class);
        defaultManipulators.add(HealthScalingData.class);
        defaultManipulators.add(JoinData.class);
        defaultManipulators.add(MovementSpeedData.class);
        defaultManipulators.add(SkinData.class);
        defaultManipulators.add(SleepingData.class);
        defaultManipulators.add(SneakingData.class);
        defaultManipulators.add(SprintData.class);
        defaultManipulators.add(StatisticData.class);
        defaultManipulators.add(WhitelistData.class);
        defaultManipulators.add(SprintData.class);

    }

    public LanternPlayer(DataView container) {
        super(container);
    }

    @Override
    public Locale getLocale() {
        return null; //TODO: Implement
    }

    @Override
    public PlayerConnection getConnection() {
        return null; //TODO: Implement
    }

    @Override
    public void sendResourcePack(ResourcePack pack) {
        //TODO: Implement
    }

    @Override
    public TabList getTabList() {
        return null; //TODO: Implement
    }

    @Override
    public void kick() {
        //TODO: Implement
    }

    @Override
    public void kick(Text reason) {
        //TODO: Implement
    }

    @Override
    public Scoreboard getScoreboard() {
        return null; //TODO: Implement
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        //TODO: Implement
    }

    @Override
    public boolean isSleepingIgnored() {
        return false; //TODO: Implement
    }

    @Override
    public void setSleepingIgnored(boolean sleepingIgnored) {
        //TODO: Implement
    }

    @Override
    public MessageSink getMessageSink() {
        return null; //TODO: Implement
    }

    @Override
    public void setMessageSink(MessageSink sink) {
        //TODO: Implement
    }

    @Override
    public boolean isViewingInventory() {
        return false; //TODO: Implement
    }

    @Override
    public Optional<Inventory> getOpenInventory() {
        return null; //TODO: Implement
    }

    @Override
    public void openInventory(Inventory inventory) {
        //TODO: Implement
    }

    @Override
    public void closeInventory() {
        //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getHelmet() {
        return null; //TODO: Implement
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getChestplate() {
        return null; //TODO: Implement
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
        //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getLeggings() {
        return null; //TODO: Implement
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getBoots() {
        return null; //TODO: Implement
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getItemInHand() {
        return null; //TODO: Implement
    }

    @Override
    public void setItemInHand(@Nullable ItemStack itemInHand) {
        //TODO: Implement
    }

    @Override
    public boolean canEquip(EquipmentType type) {
        return false; //TODO: Implement
    }

    @Override
    public boolean canEquip(EquipmentType type, @Nullable ItemStack equipment) {
        return false; //TODO: Implement
    }

    @Override
    public Optional<ItemStack> getEquipped(EquipmentType type) {
        return null; //TODO: Implement
    }

    @Override
    public boolean equip(EquipmentType type, @Nullable ItemStack equipment) {
        return false; //TODO: Implement
    }

    @Override
    public CarriedInventory<? extends Carrier> getInventory() {
        return null; //TODO: Implement
    }

    @Override
    public EntityType getType() {
        return null; //TODO: Implement
    }

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass, Vector3d velocity) {
        return null; //TODO: Implement
    }

    @Override
    public GameProfile getProfile() {
        return null; //TODO: Implement
    }

    @Override
    public boolean isOnline() {
        return false; //TODO: Implement
    }

    @Override
    public Optional<Player> getPlayer() {
        return null; //TODO: Implement
    }

    @Override
    public BanData getBanData() {
        return null; //TODO: Implement
    }

    @Override
    public String getIdentifier() {
        return null; //TODO: Implement
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectData getSubjectData() {
        return null; //TODO: Implement
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return null; //TODO: Implement
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return false; //TODO: Implement
    }

    @Override
    public boolean hasPermission(String permission) {
        return false; //TODO: Implement
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return null; //TODO: Implement
    }

    @Override
    public boolean isChildOf(Subject parent) {
        return false; //TODO: Implement
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, Subject parent) {
        return false; //TODO: Implement
    }

    @Override
    public List<Subject> getParents() {
        return null; //TODO: Implement
    }

    @Override
    public List<Subject> getParents(Set<Context> contexts) {
        return null; //TODO: Implement
    }

    @Override
    public Set<Context> getActiveContexts() {
        return null; //TODO: Implement
    }

    @Override
    public String getName() {
        return null; //TODO: Implement
    }

    @Override
    public Text getTeamRepresentation() {
        return null; //TODO: Implement
    }

    @Override
    public DataHolder copy() {
        return null; //TODO: Implement
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {
        //TODO: Implement
    }

    @Override
    public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        //TODO: Implement
    }

    @Override
    public void playSound(SoundType sound, Vector3d position, double volume, double pitch, double minVolume) {
        //TODO: Implement
    }

    @Override
    public void sendTitle(Title title) {
        //TODO: Implement
    }

    @Override
    public void sendMessage(Text message) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(Text... messages) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        //TODO: Implement
    }

    @Override
    public boolean damage(double damage, DamageSource damageSource, Cause cause) {
        return false; //TODO: Implement
    }

    @Override
    public void sendMessage(ChatType type, Text message) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(ChatType type, Text... messages) {
        //TODO: Implement
    }

    @Override
    public void sendMessages(ChatType type, Iterable<Text> messages) {
        //TODO: Implement
    }

    //TODO: Implement

}
