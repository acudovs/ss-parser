package ss.parser.car;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ss.parser.scheduler.AdConfigImpl;

@Component
@ConfigurationProperties("ss-parser.car")
class CarConfig extends AdConfigImpl {

}
