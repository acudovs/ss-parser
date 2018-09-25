package ss.parser.mail;

import ss.parser.scheduler.SchedulerTask;

public interface MailService extends SchedulerTask {
    void queue(String caller, String text, boolean html);
}
