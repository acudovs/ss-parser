package ss.parser.flat;

import org.w3c.dom.Element;
import ss.parser.rss.Ad;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdConfig;

import java.util.regex.Matcher;

class FlatChannel extends ChannelImpl {
    FlatChannel(AdConfig adConfig) {
        super(adConfig);
    }

    @Override
    protected Ad newAd(Element element, Matcher matcher) {
        return new Flat(element,
                matcher.group("region"),
                matcher.group("address"),
                matcher.group("series"),
                Integer.parseInt(matcher.group("rooms") != null ? matcher.group("rooms") : "-1"),
                Integer.parseInt(matcher.group("area")),
                Integer.parseInt(matcher.group("floor")),
                Integer.parseInt(matcher.group("price").replaceAll(",", "")),
                Integer.parseInt(matcher.group("ppm2").replaceAll(",", "")));
    }
}
