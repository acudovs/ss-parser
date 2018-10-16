package ss.parser.ad.car;

import lombok.Getter;
import lombok.ToString;
import org.w3c.dom.Element;
import ss.parser.ad.AdImpl;

@Getter
@ToString
class Car extends AdImpl {
    private final String mark;
    private final String model;
    private final String engine;
    private final int year;
    private final int run;
    private final int price;

    Car(Element element, String mark, String model, String engine, int year, int run, int price) {
        super(element);
        this.mark = mark;
        this.model = model;
        this.engine = engine;
        this.year = year;
        this.run = run;
        this.price = price;
    }
}
