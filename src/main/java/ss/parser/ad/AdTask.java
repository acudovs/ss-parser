package ss.parser.ad;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ss.parser.mail.MailService;
import ss.parser.rss.RssChannel;
import ss.parser.rss.RssChannelImpl;
import ss.parser.scheduler.SchedulerTask;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AdTask implements SchedulerTask {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final AdConfig adConfig;
    private final MailService mailService;
    private ZonedDateTime lastBuildDate = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());

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
        RssChannel rssChannel = new RssChannelImpl(adConfig, mailService);
        if (!rssChannel.getLastBuildDate().isAfter(lastBuildDate)) {
            log.info("{} is already parsed on {}", rssChannel, rssChannel.getLastBuildDate());
            return;
        }
        log.debug("All {} ads in the {}: {}", rssChannel.getAds().size(), rssChannel, rssChannel.getAds());

        List<Ad> ads = filter(rssChannel.getAds());
        log.debug("Matched {} ads in the {}: {}", ads.size(), rssChannel, ads);

        if (!ads.isEmpty()) {
            String text = ads.stream().map(Ad::toHtml).collect(Collectors.joining("<br/>"));
            mailService.queue(getClass().getName(), text, true);
        }

        lastBuildDate = rssChannel.getLastBuildDate();
    }

    private List<Ad> filter(List<Ad> ads) {
        return ads.stream()
                .filter(ad -> ad.getPubDate().isAfter(lastBuildDate))
                .filter(ad -> (boolean) adConfig.getExpression().getValue(ad))
                .collect(Collectors.toList());
    }
}
