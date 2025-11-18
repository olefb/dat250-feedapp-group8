package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.config.RabbitConfig;
import hvl.dat250.group8.feedapp.dto.VoteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("rabbitmq")
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishVoteEvent(VoteEvent event) {
        String routingKey = "poll." + event.pollId() + ".vote";

        System.out.println("Publishing Vote Event to RabbitMQ Exchange: " + RabbitConfig.POLL_VOTE_EVENTS_EXCHANGE +
                " with Routing Key: " + routingKey);

        rabbitTemplate.convertAndSend(RabbitConfig.POLL_VOTE_EVENTS_EXCHANGE, routingKey, event);
    }
}