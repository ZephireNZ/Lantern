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
package org.spongepowered.lantern.service.scheduler;

import org.spongepowered.lantern.SpongeImpl;

import java.util.concurrent.RejectedExecutionException;

public class SyncScheduler extends SchedulerBase {

    // The number of ticks elapsed since this scheduler began.
    private volatile int currentTick = 0;

    private final WorldScheduler worlds;

    SyncScheduler() {
        super(ScheduledTask.TaskSynchronicity.SYNCHRONOUS);
        this.worlds = new WorldScheduler();
    }

    /**
     * The hook to update the Ticks known by the SyncScheduler.
     */
    void tick() {
        this.runTick();
    }

    public WorldScheduler getWorldScheduler() {
        return worlds;
    }

    @Override
    protected long getTimestamp(ScheduledTask task) {
        if (task.getState() == ScheduledTask.ScheduledTaskState.WAITING) {
            // The timestamp is based on the initial offset
            if (task.delayIsTicks) {
                return this.currentTick;
            } else {
                return super.getTimestamp(task);
            }
        } else if (task.getState().isActive) {
            // The timestamp is based on the period
            if (task.intervalIsTicks) {
                return this.currentTick;
            } else {
                return super.getTimestamp(task);
            }
        }
        return 0L;
    }

    @Override
    protected void preTick() {
        try {
            currentTick = worlds.beginTick();
            try {
                new SpongeTaskBuilder().async().execute(this.worlds::doTickEnd).submit(SpongeImpl.getPlugin());
            } catch (RejectedExecutionException ex) {
                worlds.stop();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            worlds.getLock().lock();
        }
    }

    @Override
    protected void finallyPostTick() {
        try {
            while (!worlds.isTickComplete(currentTick)) {
                worlds.getCondition().await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            worlds.getLock().unlock();
        }
    }

    @Override
    protected void executeTaskRunnable(Runnable runnable) {
        runnable.run();
    }

}
