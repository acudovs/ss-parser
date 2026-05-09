package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.notification.NotificationService;

@Component
class FlatRentTask extends AdTask {
    FlatRentTask(FlatRentConfig flatRentConfig, NotificationService notificationService) {
        super(flatRentConfig, notificationService);
    }
}
