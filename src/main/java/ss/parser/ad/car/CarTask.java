package ss.parser.ad.car;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.notification.NotificationService;

@Component
class CarTask extends AdTask {
    CarTask(CarConfig carConfig, NotificationService notificationService) {
        super(carConfig, notificationService);
    }
}
