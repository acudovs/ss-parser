package ss.parser.ad;

import lombok.Getter;
import org.w3c.dom.Element;
import ss.parser.rss.AbstractRssElement;

import java.time.ZonedDateTime;

@Getter
public abstract class AbstractAd extends AbstractRssElement implements Ad {
    private final ZonedDateTime pubDate;

    public AbstractAd(Element element) {
        super(element);
        pubDate = parseDate(getContent("pubDate"));
    }
}
