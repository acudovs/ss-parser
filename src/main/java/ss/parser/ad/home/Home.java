package ss.parser.ad.home;

import lombok.Getter;
import lombok.ToString;
import org.w3c.dom.Element;
import ss.parser.ad.AbstractAd;

@Getter
@ToString
class Home extends AbstractAd {
    private final String region;
    private final String address;
    private final int area;
    private final int floors;
    private final double land;
    private final int price;

    Home(Element element, String region, String address, int area, int floors, double land, int price) {
        super(element);
        this.region = region;
        this.address = address;
        this.area = area;
        this.floors = floors;
        this.land = land;
        this.price = price;
    }
}
