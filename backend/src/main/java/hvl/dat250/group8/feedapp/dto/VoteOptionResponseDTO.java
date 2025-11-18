package hvl.dat250.group8.feedapp.dto;

import hvl.dat250.group8.feedapp.model.VoteOption;

public class VoteOptionResponseDTO {
    private Long id;
    private String caption;
    private int presentationOrder;

    public VoteOptionResponseDTO() {}

    public VoteOptionResponseDTO(Long id, String caption, int presentationOrder) {
        this.id = id;
        this.caption = caption;
        this.presentationOrder = presentationOrder;
    }

    public static VoteOptionResponseDTO fromEntity(VoteOption option) {
        return new VoteOptionResponseDTO(option.getId(), option.getCaption(), option.getPresentationOrder());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public int getPresentationOrder() { return presentationOrder; }
    public void setPresentationOrder(int presentationOrder) { this.presentationOrder = presentationOrder; }
}