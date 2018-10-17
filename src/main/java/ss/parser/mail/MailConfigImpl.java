package ss.parser.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.mail")
class MailConfigImpl implements MailConfig {
    private boolean enabled;
    private Duration rate;
    private InternetAddress from;
    private InternetAddress[] to;
    private String subject;
}
