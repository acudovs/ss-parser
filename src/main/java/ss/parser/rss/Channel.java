package ss.parser.rss;

import java.time.ZonedDateTime;
import java.util.List;

public interface Channel extends RssElement {
    ZonedDateTime getLastBuildDate();

    int getTtl();

    List<Ad> getAds();
}
