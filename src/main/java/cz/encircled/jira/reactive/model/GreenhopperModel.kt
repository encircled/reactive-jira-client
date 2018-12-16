package cz.encircled.jira.reactive.model

import com.fasterxml.jackson.annotation.JsonProperty

data class RapidBoardsSprints(val sprints: List<Sprint>)

data class SprintReport(val contents: SprintContent, val sprint: Sprint)

data class Sprint(
        val id: Int,
        val name: String,
        val state: String,
        val daysRemaining: Int,
        val startDate: String?, // TODO joda
        val endDate: String?
)

data class SprintIssue(
        val key: String,
        val summary: String,
        val status: Status
)

data class SprintContent(
        val completedIssues: List<SprintIssue>,

        @JsonProperty("issuesNotCompletedInCurrentSprint")
        val notCompletedIssues: List<SprintIssue>
)