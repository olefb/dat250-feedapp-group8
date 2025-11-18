package hvl.dat250.group8.feedapp.dto;

public class CreateVoteRequest {
    private Long voterId;
    private Long optionId;
    private Long pollId;

    public CreateVoteRequest() {}

    public Long getVoterId() { return voterId; }
    public void setVoterId(Long voterId) { this.voterId = voterId; }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public Long getPollId() { return pollId; }
    public void setPollId(Long pollId) { this.pollId = pollId; }
}
