package ss.parser.flat;

import lombok.Getter;
import lombok.ToString;
import org.w3c.dom.Element;
import ss.parser.rss.AdImpl;

@Getter
@ToString
class Flat extends AdImpl {
    private final String region;
    private final String address;
    private final String series;
    private final int rooms;
    private final int area;
    private final int floor;
    private final int price;
    private final int ppm2;

    Flat(Element element, String region, String address, String series,
         int rooms, int area, int floor, int price, int ppm2) {
        super(element);
        this.region = region;
        this.address = address;
        this.series = series;
        this.rooms = rooms;
        this.area = area;
        this.floor = floor;
        this.price = price;
        this.ppm2 = ppm2;
    }
}
