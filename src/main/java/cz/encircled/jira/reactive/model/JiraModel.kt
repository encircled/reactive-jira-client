package cz.encircled.jira.reactive.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty


data class Issue(val key: String, val fields: Fields = Fields())

data class Fields(
        val description: String? = null,
        val summary: String? = null,
        val status: Status? = null,

        @JsonProperty("issuelinks")
        val issueLinks: List<IssueLink> = listOf(),

        @JsonIgnore
        val custom: MutableMap<String, Any?> = mutableMapOf()
) {

    @JsonAnySetter
    fun ignored(name: String, value: Any?) {
        custom[name] = value
    }

}

data class JiraFilter(val jql: String, val name: String)

data class IssueLink(val outwardIssue: Issue?, val inwardIssue: Issue?)

data class Status(val name: String, val statusCategory: StatusCategory)

data class StatusCategory(val key: String)

data class SearchResult(val issues: List<Issue>) {
    var total: Long = 0
}
