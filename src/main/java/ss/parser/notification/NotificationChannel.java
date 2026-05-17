package ss.parser.notification;

import ss.parser.scheduler.SchedulerTask;

public interface NotificationChannel extends NotificationService, SchedulerTask {
    NotificationConfig getConfig();
}
