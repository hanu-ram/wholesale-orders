import com.levi.wholesale.domain.OrderDetails;
import com.levi.wholesale.lambda.common.utils.FeedUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFeedUtilTest {
    @Test
    void shouldBeAbleTOSuccessfullyLoadCSV() throws Exception {
        String fileName = "salesorder_new.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        List<OrderDetails> invFeedCSVModels = FeedUtil.loadCSV(OrderDetails.class, classPathResource.getInputStream());
        assertThat(invFeedCSVModels).isNotNull();
        Assertions.assertFalse(invFeedCSVModels.isEmpty());
    }

    @Test
    void shouldFailToLoadTheInvalidCSV() {

        Throwable exception = Assertions.assertThrows(RuntimeException.class, () -> {
            String fileName = "salesorder_with_wrong_header.csv";
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            FeedUtil.loadCSV(OrderDetails.class, classPathResource.getInputStream());
        });
        assertThat(exception.getMessage()).contains("Error capturing CSV header!");
    }

    @Test
    void testProcessBOM() throws IOException {
        String fileName = "salesorder_new.csv";
        ClassPathResource classPathResource = new ClassPathResource(fileName);

        InputStream inputStream = FeedUtil.processBOM(classPathResource.getInputStream());

        Assertions.assertNotNull(inputStream);
    }
}