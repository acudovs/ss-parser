package ss.parser.ad;

import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.net.URL;
import java.time.Duration;

@Getter
@Setter
public abstract class AbstractAdConfig implements AdConfig {
    private volatile boolean enabled;
    private volatile Duration rate;
    private volatile URL url;
    private volatile Duration timeout;
    private volatile String regex;
    private volatile String replace;
    private volatile Expression expression;

    protected static double parseDouble(@Nullable String s) {
        return s == null ? 0 : Double.parseDouble(s.replaceAll("[^-\\d.]", ""));
    }

    protected static int parseInt(@Nullable String s) {
        return s == null ? 0 : Integer.parseInt(s.replaceAll("[^-\\d]", ""));
    }

    protected static String parseString(@Nullable String s) {
        return s == null ? "" : s;
    }

    protected static double convert(double value, @Nullable String unit) {
        return unit != null && unit.equals("га.") ? value * 10000 : value;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName().replace("Config", "");
    }
}
