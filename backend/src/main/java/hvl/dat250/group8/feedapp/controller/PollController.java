package hvl.dat250.group8.feedapp.controller;

import hvl.dat250.group8.feedapp.dto.PollResultOptionDTO;
import hvl.dat250.group8.feedapp.service.PollManager;
import hvl.dat250.group8.feedapp.dto.CreatePollRequest;
import hvl.dat250.group8.feedapp.dto.PollResponseDTO;
import hvl.dat250.group8.feedapp.model.Poll;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/polls")
@CrossOrigin
public class PollController {

    private final PollManager pollManager;

    @Autowired
    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public ResponseEntity<PollResponseDTO> create(@RequestBody CreatePollRequest request, HttpServletRequest httpRequest) {
        try {
            Long creatorId = (Long) httpRequest.getAttribute("currentUserId");
            if (creatorId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Creator ID not found in security context.");
            }
            request.setUserId(creatorId.toString()); // Override the user ID from the request body

            Poll poll = pollManager.createPollWithOptions(request);
            return new ResponseEntity<>(PollResponseDTO.fromEntity(poll), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Catches "User not found" from PollManager
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public List<PollResponseDTO> list() {
        return pollManager.listPolls().stream()
                .map(PollResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/results")
    public List<PollResultOptionDTO> getResults(@PathVariable Long id) {
        // Get the raw poll object to fetch option captions
        Poll poll = pollManager.getPoll(id);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }

        // Get the aggregated counts from the service
        Map<Long, Long> voteCounts = pollManager.getPollResults(id);

        // Combine counts with option details and return DTO
        return poll.getOptions().stream()
                .map(option -> {
                    PollResultOptionDTO dto = new PollResultOptionDTO();
                    dto.setOptionId(option.getId());
                    dto.setCaption(option.getCaption());
                    dto.setVoteCount(voteCounts.getOrDefault(option.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PollResponseDTO get(@PathVariable Long id) {
        Poll poll = pollManager.getPoll(id);
        if (poll == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return PollResponseDTO.fromEntity(poll);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody Poll poll) {
        poll.setId(id);
        pollManager.updatePoll(poll);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        //  Get the ID of the currently authenticated user from the request attribute.
        Long currentUserId = (Long) httpRequest.getAttribute("currentUserId");
        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }

        Poll poll = pollManager.getPoll(id);

        // Authorization check
        if (poll == null) {
            return;
        }

        if (!poll.getCreator().getId().equals(currentUserId)) {
            // If the user is not the creator, deny access.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this poll.");
        }
        pollManager.deletePoll(id);
    }
}