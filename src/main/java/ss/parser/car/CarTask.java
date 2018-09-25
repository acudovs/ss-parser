package ss.parser.car;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.scheduler.AdTask;

@Component
class CarTask extends AdTask {
    CarTask(CarConfig carConfig, MailService mailService) {
        super(carConfig, mailService);
    }
}
