import com.levi.wholesale.delta.domain.OrderDetails;
import com.levi.wholesale.lambda.common.utils.FeedUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderFeedUtilTest {
    @Test
    void shouldBeAbleTOSuccessfullyLoadCSV() throws Exception {
        String fileName = "wholesale_orderdata_new.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        List<OrderDetails> invFeedCSVModels = FeedUtil.loadCSV(OrderDetails.class, classPathResource.getInputStream());
        assertThat(invFeedCSVModels).isNotNull();
    }

    @Test
    void shouldFailToLoadTheInvalidCSV() {

        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> {
            String fileName = "wholesale_orderdata_wrongheader.csv";
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            FeedUtil.loadCSV(OrderDetails.class, classPathResource.getInputStream());
        });
        assertThat(exception.getMessage()).contains("Error capturing CSV header!");
    }
}
