package de.dkutzer.buggy.developer.developer.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "developers")
@TypeAlias(value = "developer")
data class Developer (
        @Id
        val id: String,
        val firstName: String,
        val lastName: String
)
