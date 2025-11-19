package hvl.dat250.group8.feedapp.dto;

import hvl.dat250.group8.feedapp.model.Vote;
import java.time.LocalDateTime;

public class VoteResponseDTO {
    private Long id;
    private LocalDateTime votedAt;
    private Long voterId;
    private Long optionId;
    private Long pollId;

    public VoteResponseDTO() {}

    public static VoteResponseDTO fromEntity(Vote vote) {
        VoteResponseDTO dto = new VoteResponseDTO();
        dto.setId(vote.getId());
        dto.setVotedAt(vote.getVotedAt());
        dto.setVoterId(vote.getVoter() != null ? vote.getVoter().getId() : null);
        dto.setOptionId(vote.getOption() != null ? vote.getOption().getId() : null);
        dto.setPollId(vote.getPoll() != null ? vote.getPoll().getId() : null);
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
    public Long getVoterId() { return voterId; }
    public void setVoterId(Long voterId) { this.voterId = voterId; }
    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
    public Long getPollId() { return pollId; }
    public void setPollId(Long pollId) { this.pollId = pollId; }
}