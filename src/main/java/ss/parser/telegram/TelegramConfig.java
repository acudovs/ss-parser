package ss.parser.telegram;

import java.time.Duration;

interface TelegramConfig {
    boolean isEnabled();

    Duration getRate();

    String getBotToken();

    String getChatId();

    String getAdminChatId();

    Duration getTimeout();
}
