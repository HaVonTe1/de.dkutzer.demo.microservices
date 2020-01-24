package de.dkutzer.buggy.planning.entity

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel

interface DevelopersChannel {

    companion object {
        const val INPUT = "developers_channel"
    }

    @Input(DevelopersChannel.INPUT)
    fun input(): SubscribableChannel
}

interface IssuesChannel {

    companion object {
        const val INPUT = "issues_channel"
    }

    @Input(IssuesChannel.INPUT)
    fun input(): SubscribableChannel
}
