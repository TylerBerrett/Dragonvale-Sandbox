package com.tylerb.dragonvalesandbox.model

import kotlinx.serialization.Serializable

@Serializable
data class GithubResponse(
    val commit: CommitData
)

@Serializable
data class CommitData(
    val committer: Committer
)

@Serializable
data class Committer(
    val date: String
)