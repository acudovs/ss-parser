package ss.parser.flat;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.scheduler.AdTask;

@Component
class FlatTask extends AdTask {
    FlatTask(FlatConfig flatConfig, MailService mailService) {
        super(flatConfig, mailService);
    }
}
