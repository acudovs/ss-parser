package ss.parser.ad.car;

import org.springframework.stereotype.Component;
import ss.parser.ad.AbstractAdTask;
import ss.parser.notification.NotificationService;

@Component
class CarTask extends AbstractAdTask {
    CarTask(CarConfig carConfig, NotificationService notificationService) {
        super(carConfig, notificationService);
    }
}
