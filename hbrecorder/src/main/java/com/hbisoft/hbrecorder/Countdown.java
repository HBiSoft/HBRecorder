package com.hbisoft.hbrecorder;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Countdown extends Timer {
    private long totalTime, interval, delay;
    private TimerTask task;
    private long startTime = -1;
    private boolean restart = false, wasCancelled = false, wasStarted = false;

    public Countdown(long totalTime, long interval) {
        this(totalTime, interval, 0);
    }

    public Countdown(long totalTime, long interval, long delay) {
        super("PreciseCountdown", true);
        this.delay = delay;
        this.interval = interval;
        this.totalTime = totalTime;
        this.task = getTask(totalTime);
    }

    public void start() {
        wasStarted = true;
        this.scheduleAtFixedRate(task, delay, interval);
    }

    public void stop() {
        onStopCalled();
        this.wasCancelled = true;
        this.task.cancel();
        dispose();
    }

    // Call this when there's no further use for this timer
    public void dispose(){
        cancel();
        purge();
    }

    private TimerTask getTask(final long totalTime) {
        return new TimerTask() {

            @Override
            public void run() {
                long timeLeft;
                if (startTime < 0 || restart) {
                    startTime = scheduledExecutionTime();
                    timeLeft = totalTime;
                    restart = false;
                } else {
                    timeLeft = totalTime - (scheduledExecutionTime() - startTime);

                    if (timeLeft <= 0) {
                        this.cancel();
                        startTime = -1;
                        onFinished();
                        return;
                    }
                }

                onTick(timeLeft);
            }
        };
    }

    public abstract void onTick(long timeLeft);
    public abstract void onFinished();
    public abstract void onStopCalled();
}
