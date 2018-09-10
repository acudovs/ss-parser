package ss.parser.car;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import ss.parser.rss.Ad;
import ss.parser.scheduler.AdConfigImpl;

import java.util.regex.Matcher;

@Component
@ConfigurationProperties("ss-parser.car")
class CarConfig extends AdConfigImpl {
    @Override
    public Ad newAd(Element element, Matcher matcher) {
        return new Car(element,
                parseString(matcher.group("mark")),
                parseString(matcher.group("model")),
                parseString(matcher.group("engine")),
                parseInt(matcher.group("year")),
                parseInt(matcher.group("run")),
                parseInt(matcher.group("price")));
    }
}
