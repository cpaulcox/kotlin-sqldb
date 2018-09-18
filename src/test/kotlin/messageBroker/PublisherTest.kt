package messageBroker

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Story 2: Publish messages to broker")
class PublisherTest {

    @Test
    @DisplayName("2.1: Successfully published messages return HTTP 202")
    fun publishTest() {

    }

    @Test
    @DisplayName("2.2: Publishing messages to a full queue return HTTP 503")
    fun publishFullQueueTest() {

    }
}