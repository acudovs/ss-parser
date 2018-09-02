package ss.parser.rss;

import lombok.Getter;
import org.w3c.dom.Element;

import java.time.ZonedDateTime;

@Getter
public abstract class AdImpl extends RssElementImpl implements Ad {
    private final ZonedDateTime pubDate;

    public AdImpl(Element element) {
        super(element);
        pubDate = parseDate(getContent("pubDate"));
    }

    @Override
    public String toHtml() {
        return "<p>" + getTitle() + "</p><p>" + formatDate(pubDate) + "</p>" + getDescription();
    }
}
