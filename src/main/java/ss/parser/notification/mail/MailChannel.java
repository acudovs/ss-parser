package ss.parser.notification.mail;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ss.parser.ad.Ad;
import ss.parser.notification.AbstractChannel;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Component
@RequiredArgsConstructor
class MailChannel extends AbstractChannel {
    private final MailConfig mailConfig;
    private final JavaMailSender mailSender;

    @Override
    public MailConfig getConfig() {
        return mailConfig;
    }

    @Override
    protected String format(Ad ad) {
        return "<p>" + ad.getTitle() + "</p>"
               + "<p>" + RFC_1123_DATE_TIME.format(ad.getPubDate()) + "</p>"
               + ad.getDescription().trim();
    }

    @Override
    protected void flushQueues() {
        batch(errorsQueue, "\n\n", (text, sender) -> send(mailConfig.getAdmin(), false, text, sender));
        batch(messagesQueue, "<br/>", (text, sender) -> send(mailConfig.getTo(), true, text, sender));
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
