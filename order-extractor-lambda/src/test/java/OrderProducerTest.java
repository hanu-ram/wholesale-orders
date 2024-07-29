
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.levi.wholesale.delta.order.producer.OrderDetailsProducer;
import com.levi.wholesale.delta.persistence.PersistenceManager;
import com.levi.wholesale.lambda.common.config.Configuration;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class OrderProducerTest {

    private final PersistenceManager persistenceManager = mock(PersistenceManager.class);
    private final MockProducer<String, String> mockProducer
            = new MockProducer<>(true, new StringSerializer(), new StringSerializer());


    private static MockedStatic<Configuration> configurationMockedStatic;

    @BeforeAll
    static void setUp() throws IOException {
        configurationMockedStatic = Mockito.mockStatic(Configuration.class);
        Mockito.when(Configuration.getKafkaTopic()).thenReturn("wholesale-orders");
        Mockito.when(Configuration.getKafkaErrorTopic()).thenReturn("error-topic");
    }

    @AfterAll
    static void close() {
        configurationMockedStatic.close();
    }

    @Test
    void shouldPublishDataToStoreDeltaTopic() throws Exception {

        String orderDetails = "{\n" +
                "  \"account_no\": \"90003207\",\n" +
                "  \"po\": \"POJ00304467\",\n" +
                "  \"sd\": \"57555360\",\n" +
                "  \"product_code\": \"34964-0073\",\n" +
                "  \"customer_name\": \"LS RETAIL LEVI ONLINE\",\n" +
                "  \"brand\": \"LEVIS\",\n" +
                "  \"consumer_group\": \"MEN\",\n" +
                "  \"category\": \"BOTTOMS\",\n" +
                "  \"doc_date\": \"17/12/21 0:00\",\n" +
                "  \"rdd_date\": \"17/12/21 0:00\",\n" +
                "  \"cxl_date\": \"24/12/21 0:00\",\n" +
                "  \"approx_due_in_date\": \"28/03/22 0:00\",\n" +
                "  \"plant\": \"2006\",\n" +
                "  \"product_name\": \"WEDGIE STRAIGHT JAZZ JIVE SOUND\",\n" +
                "  \"customer_price\": \"15.45\",\n" +
                "  \"wholesale_price\": \"18.03\",\n" +
                "  \"ipa\": \"0\",\n" +
                "  \"levi_price\": \"18.03\",\n" +
                "  \"units\": \"3\",\n" +
                "  \"stock_type\": \"L\",\n" +
                "  \"vir\": \"324\",\n" +
                "  \"quantity\": 8,\n" +
                "  \"currency\": \"USD\",\n" +
                "  \"order_value\": 1050.0,\n" +
                "  \"rejection_reason\": \"J0\"\n" +
                "}";
        new OrderDetailsProducer().publishOrderDetailsToKafka(mockProducer, orderDetails, "0020014228", new ArrayList<>());
        assertThat(mockProducer.history().size()).isEqualTo(1);

        String response = mockProducer.history().get(0).value();
        DocumentContext context = JsonPath.parse(response);
        assertEquals("34964-0073", context.read("$.product_code"));
        assertEquals("90003207", context.read("$.account_no"));
        assertEquals("LS RETAIL LEVI ONLINE", context.read("$.customer_name"));
        assertEquals("POJ00304467", context.read("$.po"));
        assertEquals("57555360", context.read("$.sd"));
        assertEquals("LEVIS", context.read("$.brand"));
    }

    @Test
    void shouldPublishTwoRecordsToStoreDeltaTopic() throws Exception {

        String orderDetails = "{\n" +
                "  \"account_no\": \"90003207\",\n" +
                "  \"po\": \"POJ00304467\",\n" +
                "  \"sd\": \"57555360\",\n" +
                "  \"product_code\": \"34964-0073\",\n" +
                "  \"customer_name\": \"LS RETAIL LEVI ONLINE\",\n" +
                "  \"brand\": \"LEVIS\",\n" +
                "  \"consumer_group\": \"MEN\",\n" +
                "  \"category\": \"BOTTOMS\",\n" +
                "  \"doc_date\": \"17/12/21 0:00\",\n" +
                "  \"rdd_date\": \"17/12/21 0:00\",\n" +
                "  \"cxl_date\": \"24/12/21 0:00\",\n" +
                "  \"approx_due_in_date\": \"28/03/22 0:00\",\n" +
                "  \"plant\": \"2006\",\n" +
                "  \"product_name\": \"WEDGIE STRAIGHT JAZZ JIVE SOUND\",\n" +
                "  \"customer_price\": \"15.45\",\n" +
                "  \"wholesale_price\": \"18.03\",\n" +
                "  \"ipa\": \"0\",\n" +
                "  \"levi_price\": \"18.03\",\n" +
                "  \"units\": \"3\",\n" +
                "  \"stock_type\": \"L\",\n" +
                "  \"vir\": \"324\",\n" +
                "  \"quantity\": 8,\n" +
                "  \"currency\": \"USD\",\n" +
                "  \"order_value\": 1050.0,\n" +
                "  \"rejection_reason\": \"J0\"\n" +
                "}";

        String orderDetails2 = "{\n" +
                "  \"account_no\": \"90003208\",\n" +
                "  \"po\": \"POJ00304468\",\n" +
                "  \"sd\": \"57555368\",\n" +
                "  \"product_code\": \"34964-0078\",\n" +
                "  \"customer_name\": \"LS RETAIL LEVI ONLINE_2\",\n" +
                "  \"brand\": \"LEVIS\",\n" +
                "  \"consumer_group\": \"MEN\",\n" +
                "  \"category\": \"BOTTOMS\",\n" +
                "  \"doc_date\": \"17/12/21 0:00\",\n" +
                "  \"rdd_date\": \"17/12/21 0:00\",\n" +
                "  \"cxl_date\": \"24/12/21 0:00\",\n" +
                "  \"approx_due_in_date\": \"28/03/22 0:00\",\n" +
                "  \"plant\": \"2006\",\n" +
                "  \"product_name\": \"WEDGIE STRAIGHT JAZZ JIVE SOUND\",\n" +
                "  \"customer_price\": \"15.45\",\n" +
                "  \"wholesale_price\": \"18.03\",\n" +
                "  \"ipa\": \"0\",\n" +
                "  \"levi_price\": \"18.03\",\n" +
                "  \"units\": \"3\",\n" +
                "  \"stock_type\": \"L\",\n" +
                "  \"vir\": \"324\",\n" +
                "  \"quantity\": 8,\n" +
                "  \"currency\": \"USD\",\n" +
                "  \"order_value\": 1050.0,\n" +
                "  \"rejection_reason\": \"J0\"\n" +
                "}";
        new OrderDetailsProducer().publishOrderDetailsToKafka(mockProducer, orderDetails, "0020014229", new ArrayList<>());
        new OrderDetailsProducer().publishOrderDetailsToKafka(mockProducer, orderDetails2, "0020014230", new ArrayList<>());
        assertThat(mockProducer.history().size()).isEqualTo(2);

        String response = mockProducer.history().get(0).value();
        DocumentContext context = JsonPath.parse(response);
        assertEquals("34964-0073", context.read("$.product_code"));
        assertEquals("90003207", context.read("$.account_no"));
        assertEquals("LS RETAIL LEVI ONLINE", context.read("$.customer_name"));
        assertEquals("POJ00304467", context.read("$.po"));
        assertEquals("57555360", context.read("$.sd"));
        assertEquals("LEVIS", context.read("$.brand"));

        String response2 = mockProducer.history().get(1).value();
        DocumentContext context2 = JsonPath.parse(response2);
        assertEquals("34964-0078", context2.read("$.product_code"));
        assertEquals("90003208", context2.read("$.account_no"));
        assertEquals("LS RETAIL LEVI ONLINE_2", context2.read("$.customer_name"));
        assertEquals("POJ00304468", context2.read("$.po"));
        assertEquals("57555368", context2.read("$.sd"));
        assertEquals("LEVIS", context2.read("$.brand"));
    }
}
