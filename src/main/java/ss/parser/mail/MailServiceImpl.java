package ss.parser.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ss.parser.rss.Ad;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class MailServiceImpl implements MailService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> mailQueue = new ConcurrentHashMap<>();
    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void offer(List<Ad> ads) {
        if (isEnabled()) {
            String key = ads.get(0).getClass().getName();
            String html = ads.stream().map(Ad::toHtml).collect(Collectors.joining("<br><br>"));
            mailQueue.merge(key, html, (oldValue, newValue) -> oldValue + "<br><br>" + newValue);
            log.debug("Message to {} queued", mailConfig.getTo());
        }
    }

    @Override
    public void send(String text) {
        if (isEnabled()) {
            send(text, false);
            log.debug("Message to {} sent", mailConfig.getTo());
        }
    }

    @Override
    public Duration getRate() {
        return mailConfig.getRate();
    }

    @Override
    public void run() {
        for (String key : mailQueue.keySet()) {
            String html = mailQueue.remove(key);
            send(html, true);
            log.debug("Messages to {} sent: {}", mailConfig.getTo(), key);
        }
    }

    private boolean isEnabled() {
        if (mailConfig.isEnabled())
            return true;
        log.warn("Message to {} not sent: mail service is disabled", mailConfig.getTo());
        return false;
    }

    private void send(String text, boolean html) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(mailConfig.getFrom());
            message.setTo(mailConfig.getTo());
            message.setSubject(mailConfig.getSubject());
            message.setText(text, html);
        });
    }
}
