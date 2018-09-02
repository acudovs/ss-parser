package ss.parser.home;

import org.w3c.dom.Element;
import ss.parser.rss.Ad;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdConfig;

import java.util.regex.Matcher;

class HomeChannel extends ChannelImpl {
    HomeChannel(AdConfig adConfig) {
        super(adConfig);
    }

    @Override
    protected Ad newAd(Element element, Matcher matcher) {
        return new Home(element,
                matcher.group("region"),
                matcher.group("address"),
                Integer.parseInt(matcher.group("area")),
                Integer.parseInt(matcher.group("floors")),
                Integer.parseInt(matcher.group("land")),
                Integer.parseInt(matcher.group("price").replaceAll(",", "")));
    }
}
