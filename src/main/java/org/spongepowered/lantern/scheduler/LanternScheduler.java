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
package org.spongepowered.lantern.scheduler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;
import com.google.inject.Singleton;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

@Singleton
public class LanternScheduler implements Scheduler {

    private static class LanternThreadFactory implements ThreadFactory {
        public static final LanternThreadFactory INSTANCE = new LanternThreadFactory();
        private final AtomicInteger threadCounter = new AtomicInteger();

        private LanternThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Lantern-scheduler-" + threadCounter.getAndIncrement());
        }
    }

    @Nullable
    private static LanternScheduler instance;
    public static final int TICK_DURATION_MS = 50;
    public static final long TICK_DURATION_NS = TimeUnit.NANOSECONDS.convert(TICK_DURATION_MS, TimeUnit.MILLISECONDS);

    private final ScheduledExecutorService tickExecutor = Executors.newSingleThreadScheduledExecutor(LanternThreadFactory.INSTANCE);

    private final AsyncScheduler asyncScheduler;
    private final WorldScheduler worldScheduler;
    private final SyncScheduler syncScheduler;

    protected LanternScheduler() {
        instance = this;
        this.asyncScheduler = new AsyncScheduler();
        this.worldScheduler = new WorldScheduler();
        this.syncScheduler = new SyncScheduler(this.worldScheduler);
    }

    public static LanternScheduler getInstance() {
        return instance;
    }

    public void start() {
        tickExecutor.scheduleAtFixedRate(() -> {
            try {
                tickSyncScheduler();
            } catch (Exception ex) {
                SpongeImpl.getLogger().error("Error while pulsing", ex);
            }
        }, 0, TICK_DURATION_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the scheduler and all tasks.
     */
    public void stop() {
        getScheduledTasks().forEach(Task::cancel);
        this.worldScheduler.stop();
        tickExecutor.shutdownNow();
    }

    @Override
    public Task.Builder createTaskBuilder() {
        return new SpongeTaskBuilder();
    }

    @Override
    public Optional<Task> getTaskById(UUID id) {
        Optional<Task> optTask = this.syncScheduler.getTask(id);
        if (optTask.isPresent()) {
            return optTask;
        }
        return this.asyncScheduler.getTask(id);
    }

    @Override
    public Set<Task> getTasksByName(String pattern) {
        Pattern searchPattern = Pattern.compile(checkNotNull(pattern, "pattern"));
        Set<Task> matchingTasks = this.getScheduledTasks();

        Iterator<Task> it = matchingTasks.iterator();
        while (it.hasNext()) {
            Matcher matcher = searchPattern.matcher(it.next().getName());
            if (!matcher.matches()) {
                it.remove();
            }
        }

        return matchingTasks;
    }

    @Override
    public Set<Task> getScheduledTasks() {
        Set<Task> allTasks = Sets.newHashSet();
        allTasks.addAll(this.asyncScheduler.getScheduledTasks());
        allTasks.addAll(this.syncScheduler.getScheduledTasks());
        return allTasks;
    }

    @Override
    public Set<Task> getScheduledTasks(boolean async) {
        if (async) {
            return this.asyncScheduler.getScheduledTasks();
        } else {
            return this.syncScheduler.getScheduledTasks();
        }
    }

    @Override
    public Set<Task> getScheduledTasks(Object plugin) {
        String testOwnerId = checkPluginInstance(plugin).getId();

        Set<Task> allTasks = this.getScheduledTasks();
        Iterator<Task> it = allTasks.iterator();

        while (it.hasNext()) {
            String taskOwnerId = it.next().getOwner().getId();
            if (!testOwnerId.equals(taskOwnerId)) {
                it.remove();
            }
        }

        return allTasks;
    }

    @Override
    public int getPreferredTickInterval() {
        return TICK_DURATION_MS;
    }

    /**
     * Check the object is a plugin instance.
     *
     * @param plugin The plugin to check
     * @return The plugin container of the plugin instance
     * @throws NullPointerException If the passed in plugin instance is null
     * @throws IllegalArgumentException If the object is not a plugin instance
     */
    static PluginContainer checkPluginInstance(Object plugin) {
        Optional<PluginContainer> optPlugin = SpongeImpl.getGame().getPluginManager().fromInstance(checkNotNull(plugin, "plugin"));
        checkArgument(optPlugin.isPresent(), "Provided object is not a plugin instance");
        return optPlugin.get();
    }

    private SchedulerBase getDelegate(Task task) {
        if (task.isAsynchronous()) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    private SchedulerBase getDelegate(ScheduledTask.TaskSynchronicity syncType) {
        if (syncType == ScheduledTask.TaskSynchronicity.ASYNCHRONOUS) {
            return this.asyncScheduler;
        } else {
            return this.syncScheduler;
        }
    }

    String getNameFor(PluginContainer plugin, ScheduledTask.TaskSynchronicity syncType) {
        return getDelegate(syncType).nextName(plugin);
    }

    void submit(ScheduledTask task) {
        getDelegate(task).addTask(task);
    }

    /**
     * Ticks the synchronous scheduler.
     */
    public void tickSyncScheduler() {
        this.syncScheduler.tick();
    }

    public WorldScheduler getWorldScheduler() {
        return worldScheduler;
    }

    @Override
    public SpongeExecutorService createSyncExecutor(Object plugin) {
        return new TaskExecutorService(this::createTaskBuilder, this.syncScheduler, checkPluginInstance(plugin));
    }

    @Override
    public SpongeExecutorService createAsyncExecutor(Object plugin) {
        return new TaskExecutorService(() -> createTaskBuilder().async(), this.asyncScheduler, checkPluginInstance(plugin));
    }
}
