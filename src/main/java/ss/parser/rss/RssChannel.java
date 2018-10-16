package ss.parser.rss;

import ss.parser.ad.Ad;

import java.time.ZonedDateTime;
import java.util.List;

public interface RssChannel extends RssElement {
    ZonedDateTime getLastBuildDate();

    int getTtl();

    List<Ad> getAds();
}
