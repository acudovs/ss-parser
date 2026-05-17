package ss.parser.notification;

import java.time.Duration;

public interface NotificationConfig {
    default String getName() {
        return getClass().getSimpleName().replace("Config", "");
    }

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Duration getRate();

    void setRate(Duration rate);

    WorkingHours getWorkingHours();

    void setWorkingHours(WorkingHours workingHours);

    int getMaxMessageSize();
}
