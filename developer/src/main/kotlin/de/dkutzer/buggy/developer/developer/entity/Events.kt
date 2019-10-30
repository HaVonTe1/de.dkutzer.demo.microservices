package de.dkutzer.buggy.developer.developer.entity

import de.dkutzer.buggy.developer.developer.control.MessageGateway

data class DeveloperCreated(
        val id: String,
        val firstName: String,
        val lastName: String
) : MessageGateway.Event

data class DeveloperUpdated(
        val id: String,
        val firstName: String,
        val lastName: String
) : MessageGateway.Event



data class DeveloperDeleted(
        val id: String
) : MessageGateway.Event