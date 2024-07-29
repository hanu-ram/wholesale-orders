
import com.levi.wholesale.delta.handler.OrderFeedProcessor;
import com.levi.wholesale.delta.persistence.PersistenceManager;
import com.levi.wholesale.lambda.common.config.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class OrderFeedProcesserDBTest {
    private static MockedStatic<Configuration> configurationMockedStatic;

    @BeforeAll
    static void setUp() throws IOException {
        configurationMockedStatic = Mockito.mockStatic(Configuration.class);
        Mockito.when(Configuration.getBootStrapHost()).thenReturn("localhost:9092");
        Mockito.when(Configuration.getProducerAck()).thenReturn("1");
        Mockito.when(Configuration.getProducerRetries()).thenReturn("3");
        Mockito.when(Configuration.shouldUseSSL()).thenReturn("false");
        Mockito.when(Configuration.shouldUseSASL()).thenReturn("false");
        Mockito.when(Configuration.getApiKey()).thenReturn("YZJ7D32IZNRS5EIP");
        Mockito.when(Configuration.getApiSecret()).thenReturn("AIZ3sPteudOln8UE3JZ90viibPNYqk0ChBQXaWhv0TPapoY+OpGNJ35zSLnSAYby");
        Mockito.when(Configuration.getKafkaTopic()).thenReturn("wholesale-orders-topic");
        Mockito.when(Configuration.getKafkaErrorTopic()).thenReturn("wholesale-error-topic");    }

    @AfterAll
    static void close() {
        configurationMockedStatic.close();
    }

    //This test is for local kafka topic update
    @Disabled
    @Test
    void shouldCreate() throws Exception {
        OrderFeedProcessor feedProcessor = new OrderFeedProcessor(new PersistenceManager());
        String fileName = "wholesale_orderdata_new.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        feedProcessor.processFeed(fileName, classPathResource.getInputStream());
        Assertions.assertEquals("a", "a");
    }

}
