package ss.parser.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import ss.parser.notification.WorkingHours;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.telegram")
class TelegramConfigImpl implements TelegramConfig {
    private boolean enabled;
    private Duration rate;
    @NestedConfigurationProperty
    private WorkingHours workingHours;
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
