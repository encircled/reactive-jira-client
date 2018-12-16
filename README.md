[![Build Status](https://travis-ci.org/encircled/reactive-jira-client.svg?branch=master)](https://travis-ci.org/encircled/Joiner)

# Reactive Jira REST & Greenhopper client for Kotlin and Java 

Reactive Jira client, based on reactor project and Spring WebClient

## Setup
```java
new ReactiveJiraClient("https://my.jira.net", "username", "password")
```

## Supported methods

- getIssue
- getFilter
- searchIssues
- getActiveSprints
- getSprintReport