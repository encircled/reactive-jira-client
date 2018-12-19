import cz.encircled.jira.reactive.ReactiveJiraClient
import cz.encircled.jira.reactive.model.*
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.test.test
import kotlin.test.Test

class BasicJiraTest : JiraTest {

    @Test
    fun testAuth() {
        ReactiveJiraClient("https://jira.atlassian.com", "not", "exists")
                .getIssue("XYZ-123")
                .test()
                .expectErrorMatches {
                    it is WebClientResponseException && it.rawStatusCode == 401
                }
                .verify()
    }

    @Test
    fun testGetIssue() {
        client().getIssue("TRANS-1305", listOf("status", "summary", "description", "issuelinks"))
                .test()
                .expectNext(Issue("TRANS-1305", Fields(
                        summary = "Language Pack Upload Request",
                        description = null,
                        status = Status("Resolved", StatusCategory("done")),
                        issueLinks = listOf()
                )))
                .verifyComplete()
    }

    @Test
    fun testGetFilter() {
        client().getFilter(12844).test()
                .expectNext(JiraFilter(
                        "project = 10240 AND issuetype = 1 ORDER BY key DESC",
                        "All JIRA Bugs"))
                .verifyComplete()
    }

    @Test
    fun testGetMultipleIssues() {
        client().getIssues(listOf("JRASERVER-68588", "JRASERVER-68585"), listOf("summary"))
                .cache()
                .collectList()
                .test()
                .expectNext(listOf(
                        Issue("JRASERVER-68588", Fields(summary = "Jira incorrectly sorts options from Select List custom fields with multiple contexts")),
                        Issue("JRASERVER-68585", Fields(summary = "Created and Resolved gadget interval is broken"))
                ))
                .verifyComplete()
    }

    @Test
    fun testSearchIssues() {
        client().searchIssues("key in (JRASERVER-68588, JRASERVER-68585)", listOf("summary"), 2)
                .test()
                .expectNext(SearchResult(listOf(
                        Issue("JRASERVER-68588", Fields(summary = "Jira incorrectly sorts options from Select List custom fields with multiple contexts")),
                        Issue("JRASERVER-68585", Fields(summary = "Created and Resolved gadget interval is broken"))
                )))
                .verifyComplete()
    }

}