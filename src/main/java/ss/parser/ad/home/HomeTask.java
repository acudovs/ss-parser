package ss.parser.ad.home;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.notification.NotificationService;

@Component
class HomeTask extends AdTask {
    HomeTask(HomeConfig homeConfig, NotificationService notificationService) {
        super(homeConfig, notificationService);
    }
}
