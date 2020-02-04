package ss.parser.mail;

import javax.mail.internet.InternetAddress;
import java.time.Duration;

interface MailConfig {
    boolean isEnabled();

    Duration getRate();

    InternetAddress getFrom();

    InternetAddress[] getTo();

    InternetAddress[] getAdmin();

    String getSubject();
}
