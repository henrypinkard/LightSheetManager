package org.micromanager.lightsheetmanager.gui.navigation;

import javax.swing.Timer;
import java.util.Objects;

// TODO: use a SwingWorker? will it interfere with +/- buttons?

/**
 * Updates the positions on the navigation panel periodically using a timer.
 */
public class PositionUpdater {

    private static final int DEFAULT_TIMER_DELAY_MS = 1000;

    private Timer timer_;

    private NavigationPanel navPanel_;

    public PositionUpdater(final NavigationPanel navPanel, final boolean isPollingPositions) {
        navPanel_ = Objects.requireNonNull(navPanel);
        timer_ = new Timer(DEFAULT_TIMER_DELAY_MS, e -> navPanel_.update());
        if (!isPollingPositions) {
            timer_.stop();
        }
        timer_.start();
        //System.out.println("isRunning: " + timer_.isRunning());
    }

    public void setTimerDelay(final int ms) {
        timer_.setDelay(ms);
    }

    public int getTimerDelay() {
        return timer_.getDelay();
    }

    public void stopTimer() {
        timer_.stop();
    }

    public void startTimer() {
        timer_.start();
    }
}
