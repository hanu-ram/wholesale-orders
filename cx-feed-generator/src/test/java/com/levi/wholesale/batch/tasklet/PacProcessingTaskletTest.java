package com.levi.wholesale.batch.tasklet;

import com.levi.wholesale.service.PacProcessingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;

@ExtendWith(MockitoExtension.class)
class PacProcessingTaskletTest {

    @InjectMocks
    private PacProcessingTasklet pacProcessingTasklet;

    @Mock
    private PacProcessingService pacProcessingService;
    @Test
    void testExecute() {
        Mockito.doNothing().when(pacProcessingService).processPacUnconfirmedQuantity();
        RepeatStatus actualRepeatStatus = pacProcessingTasklet.execute(null, null);
        Assertions.assertEquals(RepeatStatus.FINISHED, actualRepeatStatus);
    }
}