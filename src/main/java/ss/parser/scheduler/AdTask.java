package ss.parser.scheduler;

import ss.parser.rss.Channel;

public interface AdTask extends SchedulerTask {
    Channel newChannel();
}
