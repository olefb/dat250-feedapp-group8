package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.dto.VoteEvent;

public interface EventPublisher {
    void publishVoteEvent(VoteEvent event);
}