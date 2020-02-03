package de.dkutzer.buggy.planning.entity

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
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

interface ErrorChannel {
    fun output(): MessageChannel
}
interface ParkingLotChannel {
    fun parkingLot(): MessageChannel
}

interface DevelopersErrorChannel : ErrorChannel {


    @Output("developers_error_channel_out")
    override fun output(): MessageChannel

    @Input("developers_error_channel_in")
    fun input(): SubscribableChannel

}

interface IssuesErrorChannel : ErrorChannel {

    @Output("issues_error_channel_out")
    override fun output(): MessageChannel

    @Input("issues_error_channel_in")
    fun input(): SubscribableChannel
}

interface DevelopersParkingLog : ParkingLotChannel {
    @Output("developers_parkinglot")
    override fun parkingLot(): MessageChannel 
        
}
interface IssuesParkingLog : ParkingLotChannel {
    @Output("issues_parkinglot")
    override fun parkingLot(): MessageChannel 
        
}
