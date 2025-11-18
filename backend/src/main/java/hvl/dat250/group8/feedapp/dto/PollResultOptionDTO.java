package hvl.dat250.group8.feedapp.dto;

public class PollResultOptionDTO {
    private Long optionId;
    private String caption;
    private Long voteCount;

    public PollResultOptionDTO() {}

    public Long getOptionId() {
        return optionId;
    }
    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Long getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(Long orDefault) {
        this.voteCount = orDefault;
    }

    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
}