package cz.encircled.jira.reactive

import cz.encircled.jira.reactive.model.*
import org.springframework.util.Base64Utils
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

class ReactiveJiraClient(
        baseUrl: String,
        username: String = "",
        password: String = "") {

    fun registerCustomFieldAlias() {
        TODO()
    }

    private val client: WebClient

    init {
        val builder = WebClient.builder()
                .baseUrl("$baseUrl/rest")
        if (username.isNotEmpty() && password.isNotEmpty()) {
            builder.defaultHeader("Authorization", "Basic " + Base64Utils
                    .encodeToString(("$username:$password").toByteArray(Charsets.UTF_8)))
        }
        client = builder.build()
    }

    fun getIssue(key: String, includedFields: List<String> = listOf()): Mono<Issue> {
        val fields = includedFields.joinToString(",")
        return client.get()
                .uri("/api/latest/issue/$key?fields=$fields")
                .retrieve()
                .bodyToMono(Issue::class.java)
    }

    fun getFilter(id: Int): Mono<JiraFilter> {
        return client.get()
                .uri("/api/latest/filter/$id")
                .retrieve()
                .bodyToMono(JiraFilter::class.java)

    }

    fun searchIssues(jql: String, includedFields: List<String> = listOf(), maxResults: Int = 50): Mono<SearchResult> {
        return client.get()
                .uri {
                    it.path("/api/latest/search")
                            .queryParam("jql", jql)
                            .queryParam("maxResults", maxResults)
                            .queryParam("fields", includedFields.joinToString(","))
                            .build()
                }
                .retrieve()
                .bodyToMono(SearchResult::class.java)
    }

    fun getActiveSprints(rapidBoardId: Int): Flux<SprintReport> {
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

    fun getSprintReport(rapidBoardId: Int, sprintId: Int): Mono<SprintReport> {
        return client.get()
                .uri("/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=$rapidBoardId&sprintId=$sprintId")
                .retrieve()
                .bodyToMono(SprintReport::class.java)
    }

}

fun main() {
    ReactiveJiraClient("https://jira.homecredit.net/jira", "", "")
//            .getIssue("PIF-1813", listOf("customfield_10806", "status", "summary", "description", "issuelinks"))
            .getActiveSprints(44)
            .doOnError {
                print(it)
            }
            .collectList()
            .subscribe {
                println(it)
            }

    Thread.sleep(100000)
}