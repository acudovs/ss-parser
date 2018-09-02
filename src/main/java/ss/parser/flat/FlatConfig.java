package ss.parser.flat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ss.parser.scheduler.AdConfigImpl;

@Component
@ConfigurationProperties("ss-parser.flat")
class FlatConfig extends AdConfigImpl {

}
