package ss.parser.home;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.scheduler.AdTask;

@Component
class HomeTask extends AdTask {
    HomeTask(HomeConfig homeConfig, MailService mailService) {
        super(homeConfig, mailService);
    }
}
