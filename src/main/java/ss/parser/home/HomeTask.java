package ss.parser.home;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.rss.Channel;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdTaskImpl;

@Component
class HomeTask extends AdTaskImpl {
    HomeTask(HomeConfig homeConfig, MailService mailService) {
        super(homeConfig, mailService);
    }

    @Override
    public Channel newChannel() {
        return new ChannelImpl(getAdConfig(), getMailService());
    }
}
