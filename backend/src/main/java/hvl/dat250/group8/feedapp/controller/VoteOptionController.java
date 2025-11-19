package hvl.dat250.group8.feedapp.controller;

import hvl.dat250.group8.feedapp.dto.VoteOptionResponseDTO;
import hvl.dat250.group8.feedapp.service.PollManager;
import hvl.dat250.group8.feedapp.model.Poll;
import hvl.dat250.group8.feedapp.model.VoteOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vote-options")
@CrossOrigin
public class VoteOptionController {

    private final PollManager pollManager;

    @Autowired
    public VoteOptionController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping("/poll/{pollId}")
    public ResponseEntity<VoteOptionResponseDTO> create(@PathVariable Long pollId, @RequestBody VoteOption voteOption) {
        try {
            VoteOption createdOption = pollManager.createVoteOptionForPoll(pollId, voteOption.getCaption());
            return new ResponseEntity<>(VoteOptionResponseDTO.fromEntity(createdOption), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/poll/{pollId}")
    public List<VoteOptionResponseDTO> getByPoll(@PathVariable Long pollId) {
        Poll poll = pollManager.getPoll(pollId);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return poll.getOptions().stream()
                .map(VoteOptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/{optionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long optionId, @RequestBody VoteOption voteOption) {
        voteOption.setId(optionId);
        pollManager.updateVoteOption(voteOption);
    }
}