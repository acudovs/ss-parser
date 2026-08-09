package ss.parser.ad.flat;

import org.w3c.dom.Element;
import ss.parser.ad.AbstractAdConfig;
import ss.parser.ad.Ad;

import java.util.regex.Matcher;

abstract class AbstractFlatConfig extends AbstractAdConfig {
    @Override
    public Ad newAd(Element element, Matcher matcher) {
        return new Flat(element,
                parseString(matcher.group("region")),
                parseString(matcher.group("address")),
                parseString(matcher.group("series")),
                parseInt(matcher.group("rooms")),
                parseInt(matcher.group("area")),
                parseInt(matcher.group("floor")),
                parseInt(matcher.group("floors")),
                parseInt(matcher.group("price")),
                parseDouble(matcher.group("ppm2")));
    }
}
