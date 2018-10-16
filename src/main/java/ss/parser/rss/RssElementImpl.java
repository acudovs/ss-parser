package ss.parser.rss;

import lombok.Getter;
import org.w3c.dom.Element;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public abstract class RssElementImpl implements RssElement {
    private final Element element;
    private final String title;
    private final String link;
    private final String description;

    protected RssElementImpl(Element element) {
        this.element = element;
        title = getContent("title");
        link = getContent("link");
        description = getContent("description");
    }

    protected static String formatDate(ZonedDateTime date) {
        return DateTimeFormatter.RFC_1123_DATE_TIME.format(date);
    }

    protected static ZonedDateTime parseDate(String date) {
        return ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    protected String getContent(String tag) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }
}
