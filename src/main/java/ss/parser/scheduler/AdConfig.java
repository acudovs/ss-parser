package ss.parser.scheduler;

import org.springframework.expression.Expression;

import java.net.URL;
import java.time.Duration;

public interface AdConfig {
    Duration getRate();

    URL getUrl();

    String getRegex();

    String getReplace();

    Expression getExpression();
}
