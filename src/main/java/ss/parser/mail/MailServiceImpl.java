package ss.parser.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
class MailServiceImpl implements MailService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, Message> mailQueue = new ConcurrentHashMap<>();
    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public void queue(String caller, String text, boolean html) {
        if (mailConfig.isEnabled()) {
            mailQueue.merge(caller, new Message(text, html), Message::merge);
            log.info("Message to {} queued by {}", mailConfig.getTo(), caller);
        } else {
            log.warn("Message to {} not queued by {}: mail service is disabled", mailConfig.getTo(), caller);
        }
    }

    @Override
    public Duration getRate() {
        return mailConfig.getRate();
    }

    @Override
    public void run() {
        for (String caller : mailQueue.keySet()) {
            send(mailQueue.remove(caller));
            log.info("Message to {} sent by {}", mailConfig.getTo(), caller);
        }
    }

    private void send(Message message) {
        mailSender.send(mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailConfig.getFrom());
            messageHelper.setTo(mailConfig.getTo());
            messageHelper.setSubject(mailConfig.getSubject());
            messageHelper.setText(message.text, message.html);
        });
    }

    @RequiredArgsConstructor
    private static class Message {
        final String text;
        final boolean html;

        Message merge(Message message) {
            String delimiter = message.html ? "<br/>" : "\r\n";
            return new Message(text + delimiter + message.text, message.html);
        }
    }
}
