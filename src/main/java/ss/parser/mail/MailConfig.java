package ss.parser.mail;

import jakarta.mail.internet.InternetAddress;
import ss.parser.notification.WorkingHours;

import java.time.Duration;

interface MailConfig {
    boolean isEnabled();

    Duration getRate();

    WorkingHours getWorkingHours();

    InternetAddress getFrom();

    InternetAddress[] getTo();

    InternetAddress[] getAdmin();

    String getSubject();
}
