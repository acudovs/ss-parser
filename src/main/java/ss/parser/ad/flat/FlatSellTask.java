package ss.parser.ad.flat;

import org.springframework.stereotype.Component;
import ss.parser.ad.AdTask;
import ss.parser.mail.MailService;

@Component
class FlatSellTask extends AdTask {
    FlatSellTask(FlatSellConfig flatSellConfig, MailService mailService) {
        super(flatSellConfig, mailService);
    }
}
