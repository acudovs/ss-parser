package ss.parser.notification.mail;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import ss.parser.notification.NotificationConfig;
import ss.parser.notification.WorkingHours;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.mail")
class MailConfig implements NotificationConfig {
    private volatile boolean enabled;
    private volatile Duration rate;
    @NestedConfigurationProperty
    private volatile WorkingHours workingHours;
    private volatile int maxMessageSize;
    private volatile InternetAddress from;
    private volatile InternetAddress[] to;
    private volatile InternetAddress[] admin;
    private volatile String subject;

    @PostConstruct
    private void init() {
        if (admin == null) {
            admin = new InternetAddress[]{to[0]};
        }
    }
}
