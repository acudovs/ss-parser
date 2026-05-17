package ss.parser.ad;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import ss.parser.notification.NotificationService;
import ss.parser.rss.RssChannel;
import ss.parser.rss.RssChannelImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractAdTask implements AdTask {
    private static final EvaluationContext evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AdConfig adConfig;
    private final NotificationService notificationService;
    private ZonedDateTime lastBuildDate = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());

    @Override
    public AdConfig getConfig() {
        return adConfig;
    }

    @Override
    public String getName() {
        return adConfig.getName();
    }

    @Override
    public boolean isEnabled() {
        return adConfig.isEnabled();
    }

    @Override
    public Duration getRate() {
        return adConfig.getRate();
    }

    @Override
    public void run() {
        RssChannel rssChannel = new RssChannelImpl(adConfig, notificationService);
        if (!rssChannel.getLastBuildDate().isAfter(lastBuildDate)) {
            log.info("{} is already parsed on {}", rssChannel, rssChannel.getLastBuildDate());
            return;
        }
        log.debug("All {} ads in the {}: {}", rssChannel.getAds().size(), rssChannel, rssChannel.getAds());

        List<Ad> ads = filter(rssChannel.getAds());
        log.debug("Matched {} ads in the {}: {}", ads.size(), rssChannel, ads);
        ads.forEach(ad -> notificationService.sendAd(getClass().getName(), ad));
        lastBuildDate = rssChannel.getLastBuildDate();
    }

    private List<Ad> filter(List<Ad> ads) {
        return ads.stream()
                .filter(ad -> ad.getPubDate().isAfter(lastBuildDate))
                .filter(ad -> (Boolean) adConfig.getExpression().getValue(evaluationContext, ad))
                .collect(Collectors.toList());
    }
}
