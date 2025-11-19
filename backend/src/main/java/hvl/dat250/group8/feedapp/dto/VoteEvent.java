package hvl.dat250.group8.feedapp.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record VoteEvent(
    Long pollId,
    Long optionId,
    Long voterId,
    LocalDateTime votedAt
) {}
