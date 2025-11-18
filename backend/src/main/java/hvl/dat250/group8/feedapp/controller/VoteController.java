package hvl.dat250.group8.feedapp.controller;

import hvl.dat250.group8.feedapp.dto.CreateVoteRequest;
import hvl.dat250.group8.feedapp.dto.VoteResponseDTO;
import hvl.dat250.group8.feedapp.service.PollManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/votes")
@CrossOrigin
public class VoteController {

    private final PollManager pollManager;

    @Autowired
    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public ResponseEntity<VoteResponseDTO> create(@RequestBody CreateVoteRequest request) {
        try {
            // The manager handles the creation of the Vote entity and publishing the event.
            // The actual persisted ID is returned asynchronously via the event listener,
            // so we return a placeholder DTO with the request data and a 202 Accepted status.
            pollManager.createVoteFromRequest(request);

            VoteResponseDTO responseDto = new VoteResponseDTO();
            responseDto.setVoterId(request.getVoterId());
            responseDto.setOptionId(request.getOptionId());
            responseDto.setPollId(request.getPollId());
            // ID will be null/placeholder as persistence is asynchronous

            // 202 Accepted
            return new ResponseEntity<>(responseDto, HttpStatus.ACCEPTED);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<VoteResponseDTO> list() {
        return pollManager.listVotes().stream()
                .map(VoteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pollManager.deleteVote(id);
    }
}