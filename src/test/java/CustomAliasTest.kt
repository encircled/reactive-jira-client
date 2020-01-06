import cz.encircled.jira.reactive.model.Fields
import cz.encircled.jira.reactive.model.Issue
import cz.encircled.jira.reactive.model.SearchResult
import reactor.test.test
import kotlin.test.Ignore
import kotlin.test.Test

class CustomAliasTest : JiraTest {

    @Test
    @Ignore // TODO
    fun testAliasInGetIssue() {
        val custom = "customfield_11435"
        val alias = "myAlias"
        client().customFieldAlias(custom, alias)
                .getIssue("TRANS-1305", listOf("summary", alias))
                .test()
                .expectNext(Issue("TRANS-1305",
                        Fields(summary = "Language Pack Upload Request",
                                custom = mutableMapOf(Pair(alias, "9223372036854775807")))))
                .verifyComplete()
    }

    @Test
    @Ignore // TODO
    fun testAliasInGetIssues() {
        val custom = "customfield_11435"
        val alias = "myAlias"
        client().customFieldAlias(custom, alias)
                .getIssues(listOf("TRANS-1305"), listOf("summary", alias))
                .test()
                .expectNext(Issue("TRANS-1305",
                        Fields(summary = "Language Pack Upload Request",
                                custom = mutableMapOf(Pair(alias, "9223372036854775807")))))
                .verifyComplete()
    }

    @Test
    @Ignore // TODO
    fun testAliasInGetIssueWithDefaultFields() {
        val custom = "customfield_11435"
        val alias = "myAlias"
        client(listOf("summary", alias)).customFieldAlias(custom, alias)
                .getIssues(listOf("TRANS-1305"))
                .test()
                .expectNext(Issue("TRANS-1305",
                        Fields(summary = "Language Pack Upload Request",
                                custom = mutableMapOf(Pair(alias, "9223372036854775807")))))
                .verifyComplete()
    }

    @Test
    @Ignore // TODO
    fun testAliasInSearchIssues() {
        val custom = "customfield_11435"
        val alias = "myAlias"
        client().customFieldAlias(custom, alias)
                .searchIssues("key in (TRANS-1305)", listOf("summary", alias))
                .test()
                .expectNext(SearchResult(listOf(
                        Issue("TRANS-1305",
                                Fields(summary = "Language Pack Upload Request",
                                        custom = mutableMapOf(Pair(alias, "9223372036854775807"))))
                )))
                .verifyComplete()
    }

}