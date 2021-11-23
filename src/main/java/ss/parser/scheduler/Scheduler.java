package ss.parser.scheduler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ss.parser.mail.MailService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

@Service
@RequiredArgsConstructor
class Scheduler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final Map<SchedulerTask, Pair<ScheduledFuture<?>, Instant>> taskHistory = new ConcurrentHashMap<>();
    private final SchedulerConfig schedulerConfig;
    private final List<SchedulerTask> schedulerTasks;
    private final MailService mailService;
    private TaskScheduler taskScheduler;

    private static String formatDate(Instant date) {
        return RFC_1123_DATE_TIME.format(ZonedDateTime.ofInstant(date, ZoneId.systemDefault()));
    }

    @PostConstruct
    private void init() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(t -> {
            log.error("Unexpected error occurred in scheduled task", t);
            mailService.sendError(getClass().getName(), ExceptionUtils.getStackTrace(t));
        });
        scheduler.setPoolSize(getPoolSize());
        scheduler.initialize();
        taskScheduler = scheduler;

        Duration rate = getRateOrThrow();
        log.info("Schedule task {} at fixed rate {}", this, rate);
        taskScheduler.scheduleAtFixedRate(this, rate);
    }

    @Override
    public void run() {
        Instant now = Instant.now();
        for (SchedulerTask task : schedulerTasks) {
            if (task.isEnabled() && isSchedule(task, now)) {
                Instant nextRun = getNextRun(task.getRate(), now);
                log.info("Schedule task {} on {}", task, formatDate(nextRun));
                ScheduledFuture<?> future = taskScheduler.schedule(task, nextRun);
                taskHistory.put(task, Pair.of(future, nextRun));
            }
        }
    }

    private Instant getNextRun(Duration rate, Instant now) {
        return schedulerConfig.isRandomize() ?
                now.plusSeconds(ThreadLocalRandom.current().nextLong(rate.getSeconds())) : now;
    }

    private int getPoolSize() {
        return schedulerConfig.isParallel() ? schedulerTasks.size() + 1 : 1;
    }

    private Duration getRateOrThrow() {
        return schedulerTasks.stream()
                .filter(SchedulerTask::isEnabled)
                .map(SchedulerTask::getRate)
                .min(Duration::compareTo)
                .orElseThrow(() -> new IllegalStateException("No enabled tasks"));
    }

    private boolean isSchedule(SchedulerTask task, Instant now) {
        if (!taskHistory.containsKey(task))
            return true;

        Pair<ScheduledFuture<?>, Instant> pair = taskHistory.get(task);
        ScheduledFuture<?> future = pair.getLeft();
        Instant lastRun = pair.getRight();
        Duration rate = schedulerConfig.isRandomize() ? task.getRate().dividedBy(2) : task.getRate();

        return future.isDone() && lastRun.plusSeconds(rate.getSeconds()).isBefore(now);
    }
}
