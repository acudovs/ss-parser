package ss.parser.home;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ss.parser.scheduler.AdConfigImpl;

@Component
@ConfigurationProperties("ss-parser.home")
class HomeConfig extends AdConfigImpl {

}
