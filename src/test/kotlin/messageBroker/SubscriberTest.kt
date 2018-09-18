package messageBroker

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@DisplayName("Story 3: Message subscriber tests")
class SubscriberTest {

    @Test
    @DisplayName("3.1: On subscribing successfully a message is returned along with HTTP 200")
    fun subscribeTest() {

    }

    @Test
    @Tag("xxxxxxxx")
    @DisplayName("3.2: Subscribing to a non-existent queue returns an HTTP 404")
    fun subscribeWrongQueueTest() {

    }

}