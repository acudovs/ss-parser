package ss.parser.ad;

import ss.parser.scheduler.SchedulerTask;

public interface AdTask extends SchedulerTask {
    AdConfig getConfig();
}
