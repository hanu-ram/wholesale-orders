package com.levi.wholesale.batch.tasklet;

import com.levi.wholesale.service.PricingProcessingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;

@ExtendWith(MockitoExtension.class)
class PricingProcessingTaskletTest {
    @InjectMocks
    private PricingProcessingTasklet pricingProcessingTasklet;
    @Mock
    private PricingProcessingService pricingProcessingService;
    @Test
    void execute() {
        Mockito.doNothing().when(pricingProcessingService).processPricingErrorData();
        RepeatStatus actualRepeatStatus = pricingProcessingTasklet.execute(null, null);
        Assertions.assertEquals(RepeatStatus.FINISHED, actualRepeatStatus);
    }

}