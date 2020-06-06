package net.runelite.api.syntheticevents;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SynEventExecutor {
    private ScheduledExecutorService executor;

    public SynEventExecutor() {
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public ScheduledFuture<?> scheduleTask(Runnable r, long delay) {
        return this.executor.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        this.executor.shutdown();
    }
}
