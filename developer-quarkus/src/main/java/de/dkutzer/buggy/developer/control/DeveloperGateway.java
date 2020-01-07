package de.dkutzer.buggy.developer.control;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dkutzer.buggy.developer.entity.Developer;
import de.dkutzer.buggy.developer.entity.DeveloperCreatedEvent;
import io.smallrye.reactive.messaging.amqp.AmqpMessage;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class DeveloperGateway {

    private final Logger log = LoggerFactory.getLogger(DeveloperGateway.class);

    private ObjectMapper objectMapper = new ObjectMapper();


    @Inject
    @Channel("developer")
    Emitter<AmqpMessage<String>> createdEventEmitter;

    @Incoming("developer")
    public CompletionStage<Void> consume(AmqpMessage<String> msg) {
        log.debug("Received Msg from 'developer' channel");
        log.trace("Headers: {}",msg.getHeader());
        log.trace("Address: {}",msg.getAddress());
        log.trace("Props: {}",msg.getApplicationProperties());
        log.trace("body: {}",msg.getBody());
        log.trace("type: {}",msg.getContentType());
        log.trace("Payload: {}",msg.getPayload());


        return msg.ack();
    }


    public void created(Developer developer) throws JsonProcessingException {
        createdEventEmitter.send(new AmqpMessage<String>(objectMapper.writeValueAsString(new DeveloperCreatedEvent(developer))));
    }

}
