import cz.encircled.jira.reactive.model.*
import reactor.test.test
import kotlin.test.Ignore
import kotlin.test.Test

class DefaultFieldsTest : JiraTest {

    @Test
    @Ignore // TODO
    fun testGetIssueDefaultFields() {
        client(listOf("status")).getIssue("TRANS-1305")
                .test()
                .expectNext(Issue("TRANS-1305", Fields(
                        summary = null,
                        description = null,
                        status = Status("Resolved", StatusCategory("done")),
                        issueLinks = listOf()
                )))
                .verifyComplete()
    }

    @Test
    @Ignore // TODO
    fun testGetIssuesDefaultFields() {
        client(listOf("status")).getIssues(listOf("TRANS-1305"))
                .test()
                .expectNext(Issue("TRANS-1305", Fields(
                        summary = null,
                        description = null,
                        status = Status("Resolved", StatusCategory("done")),
                        issueLinks = listOf()
                )))
                .verifyComplete()
    }

    @Test
    fun testSearchIssuesDefaultFields() {
        client(listOf("summary")).searchIssues("key in (JRASERVER-68588)")
                .test()
                .expectNext(SearchResult(listOf(
                        Issue("JRASERVER-68588", Fields(summary = "Jira incorrectly sorts options from Select List custom fields with multiple contexts in Two Dimensional Filter Statistics Gadget"))
                )))
                .verifyComplete()
    }

}