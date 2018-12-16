import cz.encircled.jira.reactive.ReactiveJiraClient
import cz.encircled.jira.reactive.model.*
import reactor.test.StepVerifier
import kotlin.test.Test

class IntegrationTest {

    @Test
    fun testGetIssue() {
        StepVerifier.create(
                client().getIssue("TRANS-1305", listOf("status", "summary", "description", "issuelinks")))
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
        StepVerifier.create(client().getFilter(12844))
                .expectNext(JiraFilter(
                        "project = 10240 AND issuetype = 1 ORDER BY key DESC",
                        "All JIRA Bugs"))
                .verifyComplete()
    }

    @Test
    fun testSearchIssues() {
        StepVerifier.create(client().searchIssues("project = 10240 AND issuetype = 1 ORDER BY key DESC", listOf("summary"), 2))
                .expectNext(SearchResult(listOf(
                        Issue("JRASERVER-68588", Fields(summary = "Jira incorrectly sorts options from Select List custom fields with multiple contexts")),
                        Issue("JRASERVER-68585", Fields(summary = "Created and Resolved gadget interval is broken"))
                )))
                .verifyComplete()
    }

    private fun client() = ReactiveJiraClient("https://jira.atlassian.com", "", "")

}