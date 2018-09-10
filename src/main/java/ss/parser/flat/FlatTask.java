package ss.parser.flat;

import org.springframework.stereotype.Component;
import ss.parser.mail.MailService;
import ss.parser.rss.Channel;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdTaskImpl;

@Component
class FlatTask extends AdTaskImpl {
    FlatTask(FlatConfig flatConfig, MailService mailService) {
        super(flatConfig, mailService);
    }

    @Override
    public Channel newChannel() {
        return new ChannelImpl(getAdConfig(), getMailService());
    }
}
