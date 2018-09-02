package ss.parser.rss;

import java.time.ZonedDateTime;

public interface Ad extends RssElement {
    ZonedDateTime getPubDate();

    String toHtml();
}
