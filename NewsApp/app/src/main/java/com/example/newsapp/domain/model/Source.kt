package com.example.newsapp.domain.model

import java.io.Serializable

/**
 * A news provider/source. Pure-Kotlin domain model (no Android deps).
 * `Serializable` (pure JVM) lets it ride through Compose navigation.
 */
data class Source(
    val id: String,
    val name: String
) : Serializable {
    private companion object {
        private const val serialVersionUID: Long = 1L
    }
}
