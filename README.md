[![Build Status](https://travis-ci.org/encircled/reactive-jira-client.svg?branch=master)](https://travis-ci.org/encircled/reactive-jira-client)
[![codecov](https://codecov.io/gh/encircled/reactive-jira-client/branch/master/graph/badge.svg)](https://codecov.io/gh/encircled/reactive-jira-client)

# Reactive Jira REST & Greenhopper client for Kotlin and Java 

Reactive Jira client, based on reactor project and Spring WebClient

## Setup

```kotlin
val client = ReactiveJiraClient("https://my.jira.net", "username", "password")

client.getIssue("MY-123")
```

## Maven

```xml
<dependency>
    <groupId>cz.encircled</groupId>
    <artifactId>reactive-jira-client</artifactId>
    <version>${jira.client.version}</version>    
</dependency>
```

## Supported methods

- getIssue
- getFilter
- searchIssues
- getActiveSprints
- getSprintReport