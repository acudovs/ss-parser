package ss.parser.scheduler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("ss-parser.scheduler")
class SchedulerConfigImpl implements SchedulerConfig {
    private boolean parallel;
    private boolean randomize;
}
