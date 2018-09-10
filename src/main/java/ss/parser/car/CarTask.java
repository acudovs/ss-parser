package ss.parser.car;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.rss.Channel;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdTaskImpl;

@Component
class CarTask extends AdTaskImpl {
    CarTask(CarConfig carConfig, MailService mailService) {
        super(carConfig, mailService);
    }

    @Override
    public Channel newChannel() {
        return new ChannelImpl(getAdConfig(), getMailService());
    }
}
