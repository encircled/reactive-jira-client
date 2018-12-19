package cz.encircled.jira.reactive

import cz.encircled.jira.reactive.model.*
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

/**
 * Jira client base class
 */
class ReactiveJiraClient(
        baseUrl: String,
        username: String = "",
        password: String = "",
        private val defaultIssueFields: List<String> = listOf()) {

    private val aliasToCustomField: MutableMap<String, String> = mutableMapOf()
    private val customFieldToAlias: MutableMap<String, String> = mutableMapOf()

    private val client: WebClient

    init {
        val builder = WebClient
                .builder()
                .baseUrl("$baseUrl/rest")

        // Add basic auth header
        if (username.isNotEmpty() && password.isNotEmpty()) {
            builder.defaultHeader("Authorization", "Basic " + Base64Utils
                    .encodeToString(("$username:$password").toByteArray(Charsets.UTF_8)))
        }

        client = builder.build()
    }

    /**
     * Add a meaningful alias for issue custom field, which can be used instead of "customfield_123"
     * in all methods ([getIssue], [searchIssues] etc) and in [Issue.fields]
     */
    public fun customFieldAlias(customFieldName: String, alias: String): ReactiveJiraClient {
        aliasToCustomField[alias] = customFieldName
        customFieldToAlias[customFieldName] = alias
        return this
    }

    /**
     * Fetch jira issue by key
     *
     * @param includedFields issue fields to be fetched, default is all. Default can be overriden during client creation
     */
    public fun getIssue(key: String, includedFields: List<String> = defaultIssueFields): Mono<Issue> {
        val fields = buildIncludedFields(includedFields)
        return client.get()
                .uri {
                    it.path("/api/latest/issue/{key}")
                            .queryParam("fields", fields)
                            .build(mapOf(Pair("key", key)))
                }
                .retrieve()
                .bodyToMono(Issue::class.java)
                .map(this::replaceCustomFieldsWithAliases)
    }

    /**
     * Fetch multiple jira issues by keys
     *
     * @param includedFields issue fields to be fetched, default is all. Default can be overriden during client creation
     */
    public fun getIssues(keys: List<String>, includedFields: List<String> = defaultIssueFields): Flux<Issue> =
            if (keys.isEmpty()) Flux.empty()
            else searchIssues("key in (${buildIncludedFields(keys)})", includedFields, keys.size)
                    .map(SearchResult::issues)
                    .flatMapMany { it.toFlux() }
                    .map(this::replaceCustomFieldsWithAliases)

    /**
     * Fetch jira filter by id
     */
    public fun getFilter(id: Int): Mono<JiraFilter> {
        return client.get()
                .uri("/api/latest/filter/$id")
                .retrieve()
                .bodyToMono(JiraFilter::class.java)

    }

    /**
     * Fetch multiple jira issues by JQL
     *
     * @param includedFields issue fields to be fetched, default is all. Default can be overriden during client creation
     * @param maxResults max result size
     */
    public fun searchIssues(jql: String, includedFields: List<String> = defaultIssueFields, maxResults: Int = 50): Mono<SearchResult> {
        return client.get()
                .uri {
                    it.path("/api/latest/search")
                            .queryParam("jql", jql)
                            .queryParam("maxResults", maxResults)
                            .queryParam("fields", buildIncludedFields(includedFields))
                            .build()
                }
                .retrieve()
                .bodyToMono(SearchResult::class.java)
                .map(this::replaceCustomFieldsWithAliases)
    }

    /**
     * Find **active** sprints which belong to rapid view with given **rapidBoardId**
     *
     * @param rapidBoardId id of target rapid view
     */
    public fun getActiveSprints(rapidBoardId: Int): Flux<SprintReport> {
        return client.get()
                .uri("/greenhopper/1.0/sprintquery/$rapidBoardId")
                .retrieve()
                .bodyToMono(RapidBoardsSprints::class.java)
                .map { it.sprints }
                .flatMapMany {
                    it.filter { s -> s.state == "ACTIVE" }
                            .map(Sprint::id)
                            .sorted()
                            .toFlux()
                }
                .flatMap { getSprintReport(rapidBoardId, it) }
    }

    /**
     * Fetch particular sprint report for given **rapidBoardId** and **sprintId**
     *
     * @param rapidBoardId id of target rapid view
     * @param sprintId id of target sprint
     */
    public fun getSprintReport(rapidBoardId: Int, sprintId: Int): Mono<SprintReport> {
        return client.get()
                .uri("/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=$rapidBoardId&sprintId=$sprintId")
                .retrieve()
                .bodyToMono(SprintReport::class.java)
    }

    /**
     * Replace with aliases if present and join using comma
     */
    private fun buildIncludedFields(includedFields: List<String>): String =
            includedFields
                    .joinToString(",") { aliasToCustomField[it] ?: it }

    /**
     * Replace custom fields with aliases for all issues in **searchResult**
     */
    private fun replaceCustomFieldsWithAliases(searchResult: SearchResult): SearchResult {
        val mapped = SearchResult(searchResult.issues.map(this::replaceCustomFieldsWithAliases))
        mapped.total = searchResult.total
        return mapped
    }

    /**
     * Replace custom fields with aliases for given **issue**
     */
    private fun replaceCustomFieldsWithAliases(issue: Issue): Issue {
        val replaced = issue.fields
                .custom
                .mapKeys { customFieldToAlias[it.key] ?: it.key }

        issue.fields.custom.clear()
        issue.fields.custom.putAll(replaced)

        return issue
    }

}