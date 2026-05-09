package ss.parser.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ss.parser.ad.Ad;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
class NotificationServiceImpl implements NotificationService {
    private final List<NotificationChannel> channels;

    @Override
    public void sendError(String sender, String message) {
        channels.forEach(c -> c.sendError(sender, message));
    }

    @Override
    public void sendAd(String sender, Ad ad) {
        channels.forEach(c -> c.sendAd(sender, ad));
    }
}
