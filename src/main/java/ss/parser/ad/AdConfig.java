package ss.parser.ad;

import org.springframework.expression.Expression;
import org.w3c.dom.Element;

import java.net.URL;
import java.time.Duration;
import java.util.regex.Matcher;

public interface AdConfig {
    Duration getRate();

    URL getUrl();

    Duration getTimeout();

    String getRegex();

    String getReplace();

    Expression getExpression();

    Ad newAd(Element element, Matcher matcher);
}
