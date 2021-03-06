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

import com.google.common.collect.ImmutableList;
import org.spongepowered.lantern.SpongeImpl;
import org.spongepowered.lantern.world.LanternWorld;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager for world thread pool.
 * <p/>
 * This is a little magical and finnicky, so tread with caution when messing with the phasers
 */
public class WorldScheduler {
    private static class WorldEntry {
        private final LanternWorld world;
        private WorldThread task;

        private WorldEntry(LanternWorld world) {
            this.world = world;
        }
    }

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Phaser tickBegin = new Phaser(1);
    private final Phaser tickEnd = new Phaser(1);
    private final List<WorldEntry> worlds = new CopyOnWriteArrayList<>();
    private volatile int currentTick = -1;

    private class WorldThread extends Thread {
        private final LanternWorld world;

        public WorldThread(LanternWorld world) {
            super("Lantern-world-" + world.getName());
            this.world = world;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted() && !tickEnd.isTerminated()) {
                    tickBegin.arriveAndAwaitAdvance();
                    try {
                        world.pulse();
                    } catch (Exception e) {
                        SpongeImpl.getLogger().error("Error occurred while pulsing world " + world.getName(), e);
                    } finally {
                        tickEnd.arriveAndAwaitAdvance();
                    }
                }
            } finally {
                tickBegin.arriveAndDeregister();
                tickEnd.arriveAndDeregister();
            }
        }
    }

    public List<LanternWorld> getWorlds() {
        ImmutableList.Builder<LanternWorld> ret = ImmutableList.builder();
        for (WorldEntry entry : worlds) {
            ret.add(entry.world);
        }
        return ret.build();
    }

    public Optional<LanternWorld> getWorld(String name) {
        for (WorldEntry went : worlds) {
            if (went.world.getName().equals(name)) {
                return Optional.of(went.world);
            }
        }
        return Optional.empty();
    }

    public Optional<LanternWorld> getWorld(UUID uid) {
        for (WorldEntry went : worlds) {
            if (went.world.getUniqueId().equals(uid)) {
                return Optional.of(went.world);
            }
        }
        return Optional.empty();
    }

    public boolean addWorld(final LanternWorld world) {
        final WorldEntry went = new WorldEntry(world);
        worlds.add(went);
        try {
            went.task = new WorldThread(world);
            tickBegin.register();
            tickEnd.register();
            went.task.start();
            return true;
        } catch (Throwable t) {
            tickBegin.arriveAndDeregister();
            tickEnd.arriveAndDeregister();
            worlds.remove(went);
            return false;
        }
    }

    public boolean removeWorld(final LanternWorld world) {
        for (WorldEntry entry : worlds) {
            if (entry.world.equals(world)) {
                entry.task.interrupt();
                worlds.remove(entry);
                return true;
            }
        }
        return false;
    }

    int beginTick() throws InterruptedException {
        tickEnd.awaitAdvanceInterruptibly(currentTick); // Make sure previous tick is complete
        return currentTick = tickBegin.arrive();
    }

    boolean isTickComplete(int tick) {
        return tickEnd.getPhase() > tick || tickEnd.getPhase() < 0;
    }

    void stop() {
        tickBegin.forceTermination();
        tickEnd.forceTermination();
        for (WorldEntry ent : worlds) {
            ent.task.interrupt();
        }
    }

    void doTickEnd() {
        final int currentTick = this.currentTick;
        // Mark ourselves as arrived so world threads automatically trigger advance once done
        int endPhase = tickEnd.arriveAndAwaitAdvance();
        if (endPhase != currentTick + 1) {
            SpongeImpl.getLogger().warn("Tick end barrier " + endPhase + " has advanced differently from tick begin barrier:" + currentTick + 1);
        }
        lock.lock();
        condition.signalAll();
        lock.unlock();
    }

    public Lock getLock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }
}
