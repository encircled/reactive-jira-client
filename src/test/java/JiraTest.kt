import cz.encircled.jira.reactive.ReactiveJiraClient
import cz.encircled.jira.reactive.ReactiveJiraClientImpl

interface JiraTest {

    fun client(defaultFields: List<String> = listOf()) =
            ReactiveJiraClientImpl("https://jira.atlassian.com",
                    "",
                    "",
                    defaultFields)

}