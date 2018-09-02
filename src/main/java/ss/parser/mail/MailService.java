package ss.parser.mail;

import ss.parser.rss.Ad;
import ss.parser.scheduler.SchedulerTask;

import java.util.List;

public interface MailService extends SchedulerTask {
    void offer(List<Ad> ads);

    void send(String text);
}
