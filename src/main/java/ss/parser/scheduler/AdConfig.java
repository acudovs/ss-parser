package ss.parser.scheduler;

import org.springframework.expression.Expression;
import org.w3c.dom.Element;
import ss.parser.rss.Ad;

import java.net.URL;
import java.time.Duration;
import java.util.regex.Matcher;

public interface AdConfig {
    Duration getRate();

    URL getUrl();

    String getRegex();

    String getReplace();

    Expression getExpression();

    Ad newAd(Element element, Matcher matcher);
}
