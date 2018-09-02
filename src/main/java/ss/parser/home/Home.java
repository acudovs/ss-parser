package ss.parser.home;

import lombok.Getter;
import lombok.ToString;
import org.w3c.dom.Element;
import ss.parser.rss.AdImpl;

@Getter
@ToString
class Home extends AdImpl {
    private final String region;
    private final String address;
    private final int area;
    private final int floors;
    private final int land;
    private final int price;

    Home(Element element, String region, String address, int area, int floors, int land, int price) {
        super(element);
        this.region = region;
        this.address = address;
        this.area = area;
        this.floors = floors;
        this.land = land;
        this.price = price;
    }
}
