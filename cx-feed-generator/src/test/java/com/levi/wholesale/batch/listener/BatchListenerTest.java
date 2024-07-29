package com.levi.wholesale.batch.listener;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.ConfigurableApplicationContext;

@ExtendWith(MockitoExtension.class)
class BatchListenerTest {

    @InjectMocks
    private BatchListener batchListener;

    @Mock
    private ConfigurableApplicationContext applicationContext;

    private ListAppender<ILoggingEvent> listAppender;

    @Test
    void testAfterJob_closesApplicationContext() {
        JobExecution jobExecution = getJobExecution(true);
        batchListener.afterJob(jobExecution);
        Mockito.verify(applicationContext).close();

    }

    @Test
    void testAfterJob_doNotCloseApplicationContext() {
        listAppender = getListAppenderForClass(BatchListener.class);
        JobExecution jobExecution = getJobExecution(false);
        batchListener.afterJob(jobExecution);
        Assertions.assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith("cx feed status"));
    }

    public JobExecution getJobExecution(boolean isCompleted) {
        JobExecution jobExecution = new JobExecution(10001l);
        if (isCompleted) jobExecution.setStatus(BatchStatus.COMPLETED);
        return jobExecution;
    }

    public static ListAppender<ILoggingEvent> getListAppenderForClass(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        ListAppender<ILoggingEvent> loggingEventListAppender = new ListAppender<>();
        loggingEventListAppender.start();
        logger.addAppender(loggingEventListAppender);
        return loggingEventListAppender;
    }

}