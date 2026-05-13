package ss.parser.notification;

import java.time.Duration;

public interface NotificationConfig {
    boolean isEnabled();

    Duration getRate();

    WorkingHours getWorkingHours();

    int getMaxMessageSize();
}
