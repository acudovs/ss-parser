package ss.parser.mail;

import javax.mail.internet.InternetAddress;
import java.time.Duration;

interface MailConfig {
    Duration getRate();

    InternetAddress getFrom();

    InternetAddress getTo();

    String getSubject();

    boolean isEnabled();
}
