package ss.parser.flat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import ss.parser.rss.Ad;
import ss.parser.scheduler.AdConfigImpl;

import java.util.regex.Matcher;

@Component
@ConfigurationProperties("ss-parser.flat")
class FlatConfig extends AdConfigImpl {
    @Override
    public Ad newAd(Element element, Matcher matcher) {
        return new Flat(element,
                parseString(matcher.group("region")),
                parseString(matcher.group("address")),
                parseString(matcher.group("series")),
                parseInt(matcher.group("rooms")),
                parseInt(matcher.group("area")),
                parseInt(matcher.group("floor")),
                parseInt(matcher.group("price")),
                parseInt(matcher.group("ppm2")));
    }
}
