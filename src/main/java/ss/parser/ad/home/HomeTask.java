package ss.parser.ad.home;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.mail.MailService;

@Component
class HomeTask extends AdTask {
    HomeTask(HomeConfig homeConfig, MailService mailService) {
        super(homeConfig, mailService);
    }
}
