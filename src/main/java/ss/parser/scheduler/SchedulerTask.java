package ss.parser.scheduler;

import java.time.Duration;

public interface SchedulerTask extends Runnable {
    Duration getRate();
}
