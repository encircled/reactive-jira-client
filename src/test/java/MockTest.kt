import org.junit.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.test.StepVerifier

class MockTest {

    @Test
    fun testGetIssue() {
        StepVerifier.create(WebClient.builder().build().get()
                .uri("https://jira.atlassian.com/rest/api/latest/issue/QWERTY-123")
                .retrieve()
                .bodyToMono(String::class.java))
                .expectError(WebClientResponseException::class.java)
                .verify()
    }

}