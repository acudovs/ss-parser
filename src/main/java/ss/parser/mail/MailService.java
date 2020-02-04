package ss.parser.mail;

import ss.parser.scheduler.SchedulerTask;

public interface MailService extends SchedulerTask {
    void sendError(String sender, String message);

    void sendHtml(String sender, String message);
}
