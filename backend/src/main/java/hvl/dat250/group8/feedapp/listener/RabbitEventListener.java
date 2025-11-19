package hvl.dat250.group8.feedapp.listener;

import hvl.dat250.group8.feedapp.config.RabbitConfig;
import hvl.dat250.group8.feedapp.dto.VoteEvent;
import hvl.dat250.group8.feedapp.service.PollManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rabbitmq")
public class RabbitEventListener {

    private final PollManager pollManager;

    public RabbitEventListener(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @RabbitListener(queues = RabbitConfig.POLL_APP_LISTENER_QUEUE)
    public void handleVoteEvent(VoteEvent event) {
        System.out.println("\n[RABBITMQ LISTENER] Received Vote Event: " + event);
        Long newVoteId = pollManager.recordVoteFromEvent(event);
        System.out.println("[RABBITMQ LISTENER] Vote successfully persisted with ID: " + newVoteId + "\n");
    }
}