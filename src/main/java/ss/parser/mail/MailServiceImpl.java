package ss.parser.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
class MailServiceImpl implements MailService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> errorQueue = new ConcurrentHashMap<>();
    private final Map<String, String> messageQueue = new ConcurrentHashMap<>();
    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void sendError(String sender, String message) {
        addToQueue(errorQueue, sender, message, "\r\n");
    }

    @Override
    public void sendHtml(String sender, String message) {
        addToQueue(messageQueue, sender, message, "<br/>");
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
        send(errorQueue, mailConfig.getAdmin(), false);
        send(messageQueue, mailConfig.getTo(), true);

    }

    private void addToQueue(Map<String, String> queue, String sender, String message, String delimiter) {
        if (isEnabled()) {
            queue.merge(sender, message, (message1, message2) -> message1 + delimiter + message2);
            log.info("Message queued by {}", sender);
        } else {
            log.warn("Message not queued by {}: mail service is disabled", sender);
        }
    }

    private void send(Map<String, String> queue, InternetAddress[] to, boolean html) {
        for (String sender : queue.keySet()) {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setFrom(mailConfig.getFrom());
                messageHelper.setTo(to);
                messageHelper.setSubject(mailConfig.getSubject());
                messageHelper.setText(queue.remove(sender), html);
            });
            log.info("Message to {} sent by {}", mailConfig.getTo(), sender);
        }
    }
}
