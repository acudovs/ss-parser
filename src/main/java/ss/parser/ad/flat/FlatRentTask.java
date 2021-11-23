package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.mail.MailService;

@Component
class FlatRentTask extends AdTask {
    FlatRentTask(FlatRentConfig flatRentConfig, MailService mailService) {
        super(flatRentConfig, mailService);
    }
}
