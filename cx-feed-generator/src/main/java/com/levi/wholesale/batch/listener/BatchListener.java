package com.levi.wholesale.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchListener extends JobExecutionListenerSupport {
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void afterJob(JobExecution jobExecution) {
        BatchStatus status = jobExecution.getStatus();
        if (status == BatchStatus.COMPLETED) {
            log.info("cx feed processing completed... Shutting down the application");
            applicationContext.close();
        } else {
            log.error("cx feed status is {}", status);
        }
    }
}
