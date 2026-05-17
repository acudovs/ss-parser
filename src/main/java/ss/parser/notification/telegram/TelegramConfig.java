package ss.parser.notification.telegram;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import ss.parser.notification.NotificationConfig;
import ss.parser.notification.WorkingHours;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.telegram")
class TelegramConfig implements NotificationConfig {
    private volatile boolean enabled;
    private volatile Duration rate;
    @NestedConfigurationProperty
    private volatile WorkingHours workingHours;
    private volatile int maxMessageSize;
    private volatile String botToken;
    private volatile String chatId;
    private volatile String adminChatId;
    private volatile Duration timeout;

    @PostConstruct
    private void init() {
        if (adminChatId == null) {
            adminChatId = chatId;
        }
    }
}
