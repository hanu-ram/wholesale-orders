package com.levi.wholesale.batch.tasklet;

import com.levi.wholesale.service.VirProcessingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;

@ExtendWith(MockitoExtension.class)
class VirProcessingTaskletTest {
    @InjectMocks
    private VirProcessingTasklet virProcessingTasklet;
    @Mock
    private VirProcessingService virProcessingService;
    @Test
    void execute() {
        Mockito.doNothing().when(virProcessingService).processVirUnconfirmedQuantity();
        RepeatStatus actualRepeatStatus = virProcessingTasklet.execute(null, null);
        Assertions.assertEquals(RepeatStatus.FINISHED, actualRepeatStatus);
    }
}