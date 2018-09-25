package ss.parser.scheduler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ss.parser.mail.MailService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final List<SchedulerTask> tasks;
    private final MailService mailService;

    @PostConstruct
    private void init() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(t -> {
            log.error("Unexpected error occurred in scheduled task", t);
            mailService.queue(getClass().getName(), ExceptionUtils.getStackTrace(t), false);
        });
        scheduler.setPoolSize(tasks.size());
        scheduler.initialize();

        for (SchedulerTask task : tasks) {
            log.info("Schedule task {} at fixed rate {}", task, task.getRate());
            scheduler.scheduleAtFixedRate(task, task.getRate());
        }
    }
}
