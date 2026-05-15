package ss.parser.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ss.parser.ad.Ad;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public abstract class AbstractNotificationChannel implements NotificationChannel {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Map<String, List<String>> errorsQueue = new ConcurrentHashMap<>();
    protected final Map<String, List<String>> messagesQueue = new ConcurrentHashMap<>();

    protected abstract NotificationConfig getConfig();

    protected abstract String format(Ad ad);

    protected abstract void flushQueues();

    @Override
    public boolean isEnabled() {
        return getConfig().isEnabled();
    }

    @Override
    public Duration getRate() {
        return getConfig().getRate();
    }

    @Override
    public void sendError(String sender, String message) {
        enqueue(errorsQueue, sender, message);
    }

    @Override
    public void sendAd(String sender, Ad ad) {
        enqueue(messagesQueue, sender, format(ad));
    }

    @Override
    public void run() {
        WorkingHours workingHours = getConfig().getWorkingHours();
        if (workingHours == null || workingHours.isActive()) {
            flushQueues();
        } else {
            log.debug("Outside working hours {}-{}, skipping", workingHours.getStart(), workingHours.getEnd());
        }
    }

    protected void enqueue(Map<String, List<String>> queue, String sender, String message) {
        if (isEnabled()) {
            queue.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);
            log.info("Message queued by {}", sender);
        } else {
            log.debug("Message not queued by {}: notification channel is disabled", sender);
        }
    }

    protected void batch(Map<String, List<String>> queue, String delimiter, BiConsumer<String, String> send) {
        int maxSize = getConfig().getMaxMessageSize();
        List<String> buf = new ArrayList<>();
        for (String sender : queue.keySet()) {
            buf.clear();
            int bufSize = 0;
            for (String message : queue.remove(sender)) {
                int addedSize = message.length() + (buf.isEmpty() ? 0 : delimiter.length());
                if (!buf.isEmpty() && bufSize + addedSize > maxSize) {
                    send.accept(String.join(delimiter, buf), sender);
                    buf.clear();
                    bufSize = 0;
                    addedSize = message.length();
                }
                buf.add(message);
                bufSize += addedSize;
            }
            if (!buf.isEmpty()) {
                send.accept(String.join(delimiter, buf), sender);
            }
        }
    }
}
