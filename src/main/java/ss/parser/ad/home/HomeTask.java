package ss.parser.ad.home;

import org.springframework.stereotype.Component;
import ss.parser.ad.AbstractAdTask;
import ss.parser.notification.NotificationService;

@Component
class HomeTask extends AbstractAdTask {
    HomeTask(HomeConfig homeConfig, NotificationService notificationService) {
        super(homeConfig, notificationService);
    }
}
