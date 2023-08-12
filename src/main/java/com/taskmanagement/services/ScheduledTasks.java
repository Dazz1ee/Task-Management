package com.taskmanagement.services;

import com.taskmanagement.dao.ReviewDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {
    private final TaskService taskService;

    @Scheduled(cron = "0 0 10 ? * 1")
    public void deleteCompletedTask() {
        int countDeleted = taskService.deleteCompletedTask();
        log.debug("{} tasks deleted", countDeleted);
    }
}
