package com.example.newsapp.domain.model

/**
 * A news provider/source. Pure-Kotlin domain model (no Android or framework deps).
 */
data class Source(
    val id: String,
    val name: String
)
