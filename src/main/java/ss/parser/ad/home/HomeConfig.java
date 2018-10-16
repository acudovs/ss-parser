package ss.parser.ad.home;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import ss.parser.ad.Ad;
import ss.parser.ad.AdConfigImpl;

import java.util.regex.Matcher;

@Component
@ConfigurationProperties("ss-parser.home")
class HomeConfig extends AdConfigImpl {
    @Override
    public Ad newAd(Element element, Matcher matcher) {
        return new Home(element,
                parseString(matcher.group("region")),
                parseString(matcher.group("address")),
                parseInt(matcher.group("area")),
                parseInt(matcher.group("floors")),
                convert(parseDouble(matcher.group("land")), matcher.group("unit")),
                parseInt(matcher.group("price")));
    }
}
