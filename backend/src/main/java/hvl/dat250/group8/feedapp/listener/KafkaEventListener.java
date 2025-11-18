package hvl.dat250.group8.feedapp.listener;

import hvl.dat250.group8.feedapp.dto.VoteEvent;
import hvl.dat250.group8.feedapp.service.PollManager;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class KafkaEventListener {

    private static final String POLL_VOTE_TOPIC_PREFIX = "poll-votes-";
    private static final String ALL_POLL_VOTE_TOPICS_REGEX = POLL_VOTE_TOPIC_PREFIX + ".*";

    private final PollManager pollManager;

    public KafkaEventListener(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @KafkaListener(topicPattern = ALL_POLL_VOTE_TOPICS_REGEX, groupId = "${spring.kafka.consumer.group-id}")
    public void handleVoteEvent(VoteEvent event) {
        System.out.println("\n[KAFKA LISTENER] Received Vote Event: " + event);
        Long newVoteId = pollManager.recordVoteFromEvent(event);
        System.out.println("[KAFKA LISTENER] Vote successfully persisted with ID: " + newVoteId + "\n");
    }
}