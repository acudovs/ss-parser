package ss.parser.telegram;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ss.parser.ad.Ad;
import ss.parser.notification.WorkingHours;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Component
@RequiredArgsConstructor
class TelegramServiceImpl implements TelegramService {
    private static final int MAX_MESSAGE_LENGTH = 4096;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, List<String>> errorsQueue = new ConcurrentHashMap<>();
    private final Map<String, List<String>> messagesQueue = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final TelegramConfig telegramConfig;

    // The RSS description is already HTML, so strip the tags that Telegram does not support and keep <a>, <b>.
    static String convertHtml(String html) {
        Safelist safelist = Safelist.none().addTags("a", "b", "br").addAttributes("a", "href");
        OutputSettings outputSettings = new OutputSettings();
        outputSettings.prettyPrint(false);
        return Jsoup.clean(html, "", safelist, outputSettings)
                .replaceAll("<br/?>", "\n")
                .replaceAll("(?m)^[ \t]+", "")
                .trim();
    }

    @Override
    public void sendError(String sender, String message) {
        enqueue(errorsQueue, sender, message);
    }

    @Override
    public void sendAd(String sender, Ad ad) {
        String html = ad.getTitle() + "<br/><br/>"
                      + RFC_1123_DATE_TIME.format(ad.getPubDate()) + "<br/>"
                      + ad.getDescription().trim();
        enqueue(messagesQueue, sender, html);
    }

    @Override
    public boolean isEnabled() {
        return telegramConfig.isEnabled();
    }

    @Override
    public Duration getRate() {
        return telegramConfig.getRate();
    }

    @Override
    public void run() {
        WorkingHours workingHours = telegramConfig.getWorkingHours();
        if (workingHours.isActive()) {
            flush(errorsQueue, telegramConfig.getAdminChatId(), false);
            flush(messagesQueue, telegramConfig.getChatId(), true);
        } else {
            log.debug("Outside working hours {}-{}, skipping", workingHours.getStart(), workingHours.getEnd());
        }
    }

    private void enqueue(Map<String, List<String>> queue, String sender, String message) {
        if (isEnabled()) {
            queue.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);
            log.info("Message queued by {}", sender);
        } else {
            log.debug("Message not queued by {}: telegram service is disabled", sender);
        }
    }

    private void flush(Map<String, List<String>> queue, String chatId, boolean html) {
        String delimiter = "\n\n";
        for (String sender : queue.keySet()) {
            List<String> items = queue.remove(sender);
            List<String> batch = new ArrayList<>();
            int batchSize = 0;
            for (String item : items) {
                String text = html ? convertHtml(item) : item;
                int addedSize = text.length() + (batch.isEmpty() ? 0 : delimiter.length());
                if (!batch.isEmpty() && batchSize + addedSize > MAX_MESSAGE_LENGTH) {
                    post(chatId, String.join(delimiter, batch), sender);
                    batch.clear();
                    batchSize = 0;
                    addedSize = text.length();
                }
                batch.add(text);
                batchSize += addedSize;
            }
            if (!batch.isEmpty()) {
                post(chatId, String.join(delimiter, batch), sender);
            }
        }
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
