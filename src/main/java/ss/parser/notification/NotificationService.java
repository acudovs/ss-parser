package ss.parser.notification;

import ss.parser.ad.Ad;

public interface NotificationService {
    void sendError(String sender, String message);

    void sendAd(String sender, Ad ad);
}
