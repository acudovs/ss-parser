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
    private boolean enabled;
    private Duration rate;
    @NestedConfigurationProperty
    private WorkingHours workingHours;
    private int maxMessageSize;
    private String botToken;
    private String chatId;
    private String adminChatId;
    private Duration timeout;

    @PostConstruct
    private void init() {
        if (adminChatId == null) {
            adminChatId = chatId;
        }
    }
}
