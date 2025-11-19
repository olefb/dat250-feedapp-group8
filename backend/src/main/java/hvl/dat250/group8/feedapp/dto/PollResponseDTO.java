package hvl.dat250.group8.feedapp.dto;

import hvl.dat250.group8.feedapp.model.Poll;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PollResponseDTO {
    private Long id;
    private String question;
    private LocalDateTime publishedAt;
    private LocalDateTime deadline;
    private Long creatorId;
    private List<VoteOptionResponseDTO> options;

    public PollResponseDTO() {}

    public static PollResponseDTO fromEntity(Poll poll) {
        PollResponseDTO dto = new PollResponseDTO();
        dto.setId(poll.getId());
        dto.setQuestion(poll.getQuestion());
        dto.setPublishedAt(poll.getPublishedAt());
        dto.setDeadline(poll.getDeadline());
        dto.setCreatorId(poll.getCreator() != null ? poll.getCreator().getId() : null);
        dto.setOptions(poll.getOptions().stream()
                .map(VoteOptionResponseDTO::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public List<VoteOptionResponseDTO> getOptions() { return options; }
    public void setOptions(List<VoteOptionResponseDTO> options) { this.options = options; }
}