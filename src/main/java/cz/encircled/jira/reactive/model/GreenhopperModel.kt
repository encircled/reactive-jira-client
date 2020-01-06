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
        var typeName: String? = null,
        val typeId: String,
        val statusId: String,
        var statusName: String? = null,
        val epic: String?
)

data class SprintContent(
        val completedIssues: List<SprintIssue>,

        @JsonProperty("issuesNotCompletedInCurrentSprint")
        val notCompletedIssues: List<SprintIssue>,

        val entityData: EntityData
)

data class EntityData(
        val statuses: Map<String, GreenhopperStatus>,
        val types: Map<String, GreenhopperType>
)

data class GreenhopperStatus(val statusName: String)

data class GreenhopperType(val typeName: String)