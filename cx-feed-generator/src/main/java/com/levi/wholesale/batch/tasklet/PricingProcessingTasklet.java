package com.levi.wholesale.batch.tasklet;

import com.levi.wholesale.service.PricingProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PricingProcessingTasklet implements Tasklet {

    @Autowired
    private PricingProcessingService pricingProcessingService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("starting pricing error tasklet");
        pricingProcessingService.processPricingErrorData();
        return RepeatStatus.FINISHED;
    }
}
