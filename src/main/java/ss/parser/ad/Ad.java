package ss.parser.ad;

import ss.parser.rss.RssElement;

import java.time.ZonedDateTime;

public interface Ad extends RssElement {
    ZonedDateTime getPubDate();

    String toHtml();
}
