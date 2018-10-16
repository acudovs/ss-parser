package ss.parser.ad.car;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.mail.MailService;

@Component
class CarTask extends AdTask {
    CarTask(CarConfig carConfig, MailService mailService) {
        super(carConfig, mailService);
    }
}
