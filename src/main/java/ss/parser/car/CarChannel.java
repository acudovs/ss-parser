package ss.parser.car;

import org.w3c.dom.Element;
import ss.parser.rss.Ad;
import ss.parser.rss.ChannelImpl;
import ss.parser.scheduler.AdConfig;

import java.util.regex.Matcher;

class CarChannel extends ChannelImpl {
    CarChannel(AdConfig adConfig) {
        super(adConfig);
    }

    @Override
    protected Ad newAd(Element element, Matcher matcher) {
        return new Car(element,
                matcher.group("mark"),
                matcher.group("model"),
                matcher.group("engine"),
                Integer.parseInt(matcher.group("year")),
                Integer.parseInt(matcher.group("run") != null ? matcher.group("run") : "-1"),
                Integer.parseInt(matcher.group("price").replaceAll(",", "")));
    }
}
