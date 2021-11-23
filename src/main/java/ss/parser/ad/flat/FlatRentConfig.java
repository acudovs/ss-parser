package ss.parser.ad.flat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ss-parser.flat-rent")
class FlatRentConfig extends FlatConfig {}
