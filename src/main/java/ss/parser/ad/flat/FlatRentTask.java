package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AbstractAdTask;
import ss.parser.notification.NotificationService;

@Component
class FlatRentTask extends AbstractAdTask {
    FlatRentTask(FlatRentConfig flatRentConfig, NotificationService notificationService) {
        super(flatRentConfig, notificationService);
    }
}
