package ss.parser.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ss.parser.ad.AdConfig;
import ss.parser.ad.AdConfig.ExpressionConverter;
import ss.parser.ad.AdTask;
import ss.parser.notification.NotificationChannel;
import ss.parser.notification.NotificationConfig;
import ss.parser.notification.WorkingHours;
import ss.parser.scheduler.Scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class ApiController {
    private final Scheduler scheduler;
    private final ExpressionConverter expressionConverter;

    record StatusDto(List<AdDto> ads, List<ChannelDto> channels) {}

    record AdDto(String name, boolean enabled, long rateSecs, String nextRun, boolean scheduled, String expression) {}

    record ChannelDto(String name, boolean enabled, long rateSecs, String nextRun, boolean scheduled, WorkingHours workingHours) {}

    @GetMapping("/status")
    StatusDto getStatus() {
        List<AdDto> ads = new ArrayList<>();
        List<ChannelDto> channels = new ArrayList<>();
        for (Scheduler.TaskStatus ts : scheduler.getTaskStatuses()) {
            String nextRun = ts.nextRun() != null ? ts.nextRun().toString() : null;
            if (ts.task() instanceof AdTask task) {
                ads.add(new AdDto(
                        task.getName(), task.isEnabled(), task.getRate().toSeconds(), nextRun, ts.scheduled(),
                        task.getConfig().getExpression().getExpressionString()
                ));
            } else if (ts.task() instanceof NotificationChannel channel) {
                channels.add(new ChannelDto(
                        channel.getName(), channel.isEnabled(), channel.getRate().toSeconds(), nextRun, ts.scheduled(),
                        channel.getConfig().getWorkingHours()
                ));
            }
        }
        return new StatusDto(ads, channels);
    }

    record TaskPatch(Boolean enabled, Long rateSecs, String expression) {}

    @PatchMapping("/tasks/{name}")
    void patchTask(@PathVariable String name, @RequestBody TaskPatch patch) {
        AdConfig config = scheduler.getTaskStatuses().stream()
                .filter(ts -> ts.task() instanceof AdTask && ts.task().getName().equals(name))
                .map(ts -> ((AdTask) ts.task()).getConfig())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (patch.enabled() != null) {
            config.setEnabled(patch.enabled());
        }
        if (patch.rateSecs() != null) {
            if (patch.rateSecs() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rateSecs must be positive");
            }
            config.setRate(Duration.ofSeconds(patch.rateSecs()));
        }
        if (patch.expression() != null) {
            try {
                config.setExpression(expressionConverter.convert(patch.expression()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }
    }

    record ChannelPatch(Boolean enabled, Long rateSecs, WorkingHours workingHours) {}

    @PatchMapping("/channels/{name}")
    void patchChannel(@PathVariable String name, @RequestBody ChannelPatch patch) {
        NotificationConfig config = scheduler.getTaskStatuses().stream()
                .filter(ts -> ts.task() instanceof NotificationChannel && ts.task().getName().equals(name))
                .map(ts -> ((NotificationChannel) ts.task()).getConfig())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (patch.enabled() != null) {
            config.setEnabled(patch.enabled());
        }
        if (patch.rateSecs() != null) {
            if (patch.rateSecs() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rateSecs must be positive");
            }
            config.setRate(Duration.ofSeconds(patch.rateSecs()));
        }
        if (patch.workingHours() != null) {
            config.setWorkingHours(patch.workingHours());
        }
    }
}
