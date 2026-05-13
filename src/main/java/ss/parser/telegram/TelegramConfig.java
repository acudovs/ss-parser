package ss.parser.telegram;

import ss.parser.notification.WorkingHours;

import java.time.Duration;

interface TelegramConfig {
    boolean isEnabled();

    Duration getRate();

    WorkingHours getWorkingHours();

    String getBotToken();

    String getChatId();

    String getAdminChatId();

    Duration getTimeout();
}
