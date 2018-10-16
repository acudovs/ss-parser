package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.mail.MailService;

@Component
class FlatTask extends AdTask {
    FlatTask(FlatConfig flatConfig, MailService mailService) {
        super(flatConfig, mailService);
    }
}
