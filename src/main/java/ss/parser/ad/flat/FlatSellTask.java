package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.notification.NotificationService;

@Component
class FlatSellTask extends AdTask {
    FlatSellTask(FlatSellConfig flatSellConfig, NotificationService notificationService) {
        super(flatSellConfig, notificationService);
    }
}
