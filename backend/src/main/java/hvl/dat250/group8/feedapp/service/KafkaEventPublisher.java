package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.dto.VoteEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaEventPublisher implements EventPublisher {

    private static final String POLL_VOTE_TOPIC = "poll-votes";

    private final KafkaTemplate<Long, VoteEvent> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<Long, VoteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishVoteEvent(VoteEvent event) {
        System.out.println("Publishing to Topic: " + POLL_VOTE_TOPIC +
                " | Key: " + event.pollId());

        kafkaTemplate.send(POLL_VOTE_TOPIC, event.pollId(), event);
    }
}