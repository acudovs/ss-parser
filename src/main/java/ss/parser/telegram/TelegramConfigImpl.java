package ss.parser.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.telegram")
class TelegramConfigImpl implements TelegramConfig {
    private boolean enabled;
    private Duration rate;
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
