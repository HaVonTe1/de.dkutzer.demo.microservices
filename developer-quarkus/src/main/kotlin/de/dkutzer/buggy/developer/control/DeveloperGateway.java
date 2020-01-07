package de.dkutzer.buggy.developer.control;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dkutzer.buggy.developer.entity.Developer;
import de.dkutzer.buggy.developer.entity.DeveloperCreatedEvent;
import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class DeveloperGateway {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Inject
    @Channel("developer")
    @OnOverflow(OnOverflow.Strategy.BUFFER)
    Emitter<AmqpMessage<String>> createdEventEmitter;

    @Outgoing("developer")
    @Incoming("developer")
    @Broadcast
    public String  process(String developer) throws JsonProcessingException {
        return developer;
    }



    public void created(Developer developer) throws JsonProcessingException {
        createdEventEmitter.send(new AmqpMessage<String>(objectMapper.writeValueAsString(new DeveloperCreatedEvent(developer))));
    }

}
