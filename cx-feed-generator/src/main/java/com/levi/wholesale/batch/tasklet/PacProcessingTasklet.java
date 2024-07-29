package com.levi.wholesale.batch.tasklet;

import com.levi.wholesale.service.PacProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PacProcessingTasklet implements Tasklet {

    @Autowired
    private PacProcessingService pacProcessingService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("starting pac processing tasklet");
        pacProcessingService.processPacUnconfirmedQuantity();
        return RepeatStatus.FINISHED;
    }
}
