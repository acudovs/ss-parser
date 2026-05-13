package ss.parser.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ss.parser.ad.Ad;

import jakarta.mail.internet.InternetAddress;
import ss.parser.notification.WorkingHours;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Component
@RequiredArgsConstructor
class MailServiceImpl implements MailService {
    private static final int MAX_MESSAGE_SIZE = 100_000;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, List<String>> errorsQueue = new ConcurrentHashMap<>();
    private final Map<String, List<String>> messagesQueue = new ConcurrentHashMap<>();
    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void sendError(String sender, String message) {
        enqueue(errorsQueue, sender, message);
    }

    @Override
    public void sendAd(String sender, Ad ad) {
        String html = "<p>" + ad.getTitle() + "</p>"
                + "<p>" + RFC_1123_DATE_TIME.format(ad.getPubDate()) + "</p>"
                + ad.getDescription().trim();
        enqueue(messagesQueue, sender, html);
    }

    @Override
    public boolean isEnabled() {
        return mailConfig.isEnabled();
    }

    @Override
    public Duration getRate() {
        return mailConfig.getRate();
    }

    @Override
    public void run() {
        WorkingHours workingHours = mailConfig.getWorkingHours();
        if (workingHours.isActive()) {
            flush(errorsQueue, mailConfig.getAdmin(), false);
            flush(messagesQueue, mailConfig.getTo(), true);
        } else {
            log.debug("Outside working hours {}-{}, skipping", workingHours.getStart(), workingHours.getEnd());
        }
    }

    private void enqueue(Map<String, List<String>> queue, String sender, String message) {
        if (isEnabled()) {
            queue.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);
            log.info("Message queued by {}", sender);
        } else {
            log.debug("Message not queued by {}: mail service is disabled", sender);
        }
    }

    private void flush(Map<String, List<String>> queue, InternetAddress[] to, boolean html) {
        String delimiter = html ? "<br/>" : "\n\n";
        for (String sender : queue.keySet()) {
            List<String> items = queue.remove(sender);
            List<String> batch = new ArrayList<>();
            int batchSize = 0;
            for (String item : items) {
                int addedSize = item.length() + (batch.isEmpty() ? 0 : delimiter.length());
                if (!batch.isEmpty() && batchSize + addedSize > MAX_MESSAGE_SIZE) {
                    send(to, html, String.join(delimiter, batch), sender);
                    batch.clear();
                    batchSize = 0;
                    addedSize = item.length();
                }
                batch.add(item);
                batchSize += addedSize;
            }
            if (!batch.isEmpty()) {
                send(to, html, String.join(delimiter, batch), sender);
            }
        }
    }

    private void send(InternetAddress[] to, boolean html, String text, String sender) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            messageHelper.setFrom(mailConfig.getFrom());
            messageHelper.setTo(to);
            messageHelper.setSubject(mailConfig.getSubject());
            messageHelper.setText(text, html);
        });
        log.info("Message to {} sent by {}", Arrays.toString(to), sender);
    }
}
