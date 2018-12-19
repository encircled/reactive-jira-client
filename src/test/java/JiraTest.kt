import cz.encircled.jira.reactive.ReactiveJiraClient

interface JiraTest {

    fun client(defaultFields: List<String> = listOf()) =
            ReactiveJiraClient("https://jira.atlassian.com",
                    "",
                    "",
                    defaultFields)

}