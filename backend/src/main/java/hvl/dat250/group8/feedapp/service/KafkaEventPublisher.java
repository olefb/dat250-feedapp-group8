package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.dto.VoteEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("kafka")
public class KafkaEventPublisher implements EventPublisher {

    private static final String POLL_VOTE_TOPIC_PREFIX = "poll-votes-";

    private final KafkaTemplate<Long, VoteEvent> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<Long, VoteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send event with the pollId as the key.
    @Override
    public void publishVoteEvent(VoteEvent event) {
        String topic = POLL_VOTE_TOPIC_PREFIX + event.pollId();

        System.out.println("Publishing Vote Event to Kafka Topic: " + topic +
                " with Key: " + event.pollId());

        kafkaTemplate.send(topic, event.pollId(), event);
    }


    // For the purpose of the high-throughput test, we will send a null key
    // to ensure messages are distributed across all partitions in a round-robin fashion.
/*
    @Override
    public void publishVoteEvent(VoteEvent event) {
        String topic = POLL_VOTE_TOPIC_PREFIX + event.pollId();

        System.out.println("Publishing Vote Event to Kafka Topic: " + topic + " with Key: null (for load balancing)");

        kafkaTemplate.send(topic, null, event);
    }
*/
}


