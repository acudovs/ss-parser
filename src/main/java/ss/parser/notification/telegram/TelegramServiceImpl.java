package ss.parser.notification.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import ss.parser.ad.Ad;
import ss.parser.notification.AbstractNotificationChannel;
import ss.parser.notification.NotificationConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Component
@RequiredArgsConstructor
class TelegramServiceImpl extends AbstractNotificationChannel implements TelegramService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final TelegramConfig telegramConfig;

    @Override
    protected NotificationConfig getConfig() {
        return telegramConfig;
    }

    @Override
    protected String format(Ad ad) {
        String html = ad.getTitle() + "<br/><br/>"
                      + RFC_1123_DATE_TIME.format(ad.getPubDate()) + "<br/>"
                      + ad.getDescription();
        Safelist safelist = Safelist.none().addTags("a", "b", "br").addAttributes("a", "href");
        return Jsoup.clean(html, "", safelist, new OutputSettings().prettyPrint(false))
                .replaceAll("<br/?>", "\n")
                .replaceAll("(?m)^[ \t]+", "")
                .trim();
    }

    @Override
    protected void flushQueues() {
        batch(errorsQueue, "\n\n", (text, sender) -> post(telegramConfig.getAdminChatId(), text, sender));
        batch(messagesQueue, "\n\n", (text, sender) -> post(telegramConfig.getChatId(), text, sender));
    }

    private void post(String chatId, String text, String sender) {
        String url = "https://api.telegram.org/bot" + telegramConfig.getBotToken() + "/sendMessage";
        try {
            Message message = new Message(chatId, text, "HTML", new LinkPreviewOptions(true));
            String body = objectMapper.writeValueAsString(message);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(telegramConfig.getTimeout())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.info("Telegram message to {} sent by {}", chatId, sender);
            } else {
                log.error("Telegram API error {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Failed to send Telegram message to {}", chatId, e);
        }
    }

    private record LinkPreviewOptions(boolean is_disabled) {}

    private record Message(String chat_id, String text, String parse_mode, LinkPreviewOptions link_preview_options) {}
}
