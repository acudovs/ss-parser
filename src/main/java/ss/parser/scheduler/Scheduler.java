package ss.parser.scheduler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ss.parser.mail.MailService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
class Scheduler implements SchedulerTask {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final Map<SchedulerTask, Pair<ScheduledFuture, Instant>> taskHistory = new ConcurrentHashMap<>();
    private final SchedulerConfig schedulerConfig;
    private final List<SchedulerTask> schedulerTasks;
    private final MailService mailService;
    private ThreadPoolTaskScheduler scheduler;

    @PostConstruct
    private void init() {
        List<SchedulerTask> tasks = schedulerConfig.isRandomize() ?
                Collections.singletonList(this) : schedulerTasks;

        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(t -> {
            log.error("Unexpected error occurred in scheduled task", t);
            mailService.queue(getClass().getName(), ExceptionUtils.getStackTrace(t), false);
        });
        scheduler.setPoolSize(tasks.size());
        scheduler.initialize();

        for (SchedulerTask task : tasks) {
            Duration rate = task.getRate();
            log.info("Schedule task {} at fixed rate {}", task, rate);
            scheduler.scheduleAtFixedRate(task, rate);
        }
    }

    @Override
    public Duration getRate() {
        return schedulerTasks.stream()
                .map(SchedulerTask::getRate)
                .min(Duration::compareTo)
                .orElse(Duration.ZERO);
    }

    @Override
    public void run() {
        Instant now = Instant.now();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (SchedulerTask task : schedulerTasks) {
            Duration halfOfRate = task.getRate().dividedBy(2);
            if (isSchedule(task, halfOfRate, now)) {
                Instant nextRun = now.plusSeconds(rnd.nextLong(task.getRate().getSeconds()));
                log.info("Schedule task {} at {}", task, nextRun);
                ScheduledFuture future = scheduler.schedule(task, nextRun);
                taskHistory.put(task, Pair.of(future, nextRun));
            }
        }
    }

    private boolean isSchedule(SchedulerTask task, Duration rate, Instant now) {
        if (!taskHistory.containsKey(task))
            return true;

        Pair<ScheduledFuture, Instant> pair = taskHistory.get(task);
        ScheduledFuture future = pair.getLeft();
        Instant lastRun = pair.getRight();

        return future.isDone() && lastRun.plusSeconds(rate.getSeconds()).isBefore(now);
    }
}
