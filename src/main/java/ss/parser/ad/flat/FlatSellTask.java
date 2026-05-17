package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AbstractAdTask;
import ss.parser.notification.NotificationService;

@Component
class FlatSellTask extends AbstractAdTask {
    FlatSellTask(FlatSellConfig flatSellConfig, NotificationService notificationService) {
        super(flatSellConfig, notificationService);
    }
}
