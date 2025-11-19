package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.dto.CreatePollRequest;
import hvl.dat250.group8.feedapp.dto.CreateVoteRequest;
import hvl.dat250.group8.feedapp.dto.VoteEvent;
import hvl.dat250.group8.feedapp.model.User;
import hvl.dat250.group8.feedapp.model.Poll;
import hvl.dat250.group8.feedapp.model.VoteOption;
import hvl.dat250.group8.feedapp.model.Vote;
import hvl.dat250.group8.feedapp.repository.PollRepository;
import hvl.dat250.group8.feedapp.repository.UserRepository;
import hvl.dat250.group8.feedapp.repository.VoteOptionRepository;
import hvl.dat250.group8.feedapp.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PollManager {

    private final UserRepository userRepository;
    private final PollRepository pollRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRepository voteRepository;

    private final ValkeyVoteService valkeyVoteService;
    private final EventPublisher eventPublisher;

    @Autowired
    public PollManager(UserRepository userRepository, PollRepository pollRepository,
                       VoteOptionRepository voteOptionRepository, VoteRepository voteRepository,
                       ValkeyVoteService valkeyVoteService, EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.pollRepository = pollRepository;
        this.voteOptionRepository = voteOptionRepository;
        this.voteRepository = voteRepository;
        this.valkeyVoteService = valkeyVoteService;
        this.eventPublisher = eventPublisher;
    }

    // Users
    public List<User> listUsers() {
        return userRepository.findAll();
    }
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public Long createUser(User user) {
        return userRepository.save(user).getId();
    }

    @Transactional
    public void updateUser(User user) {
        if (user.getId() != null) {
            userRepository.save(user);
        }
    }

    // Poll methods
    public List<Poll> listPolls() {
        return pollRepository.findAll();
    }
    public Poll getPoll(Long id) {
        return pollRepository.findById(id).orElse(null);
    }

    @Transactional
    public Poll createPollWithOptions(CreatePollRequest request) {
        // Find the creator user
        Long creatorId = Long.parseLong(request.getUserId());
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorId));

        // Create the Poll entity
        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion());
        poll.setPublishedAt(LocalDateTime.now());
        poll.setCreator(creator);

        // Persist the poll to get an ID
        Poll savedPoll = pollRepository.save(poll);

        // Create and persist vote options
        List<VoteOption> voteOptions = new ArrayList<>();
        for (int i = 0; i < request.getOptions().size(); i++) {
            VoteOption voteOption = new VoteOption();
            voteOption.setCaption(request.getOptions().get(i));
            voteOption.setPresentationOrder(i);
            voteOption.setPoll(savedPoll);

            VoteOption savedOption = voteOptionRepository.save(voteOption);
            voteOptions.add(savedOption);
        }

        savedPoll.setOptions(voteOptions);
        return savedPoll;
    }

    @Transactional
    public void updatePoll(Poll poll) {
        if (poll.getId() != null) {
            pollRepository.save(poll);
        }
    }

    @Transactional
    public void deletePoll(Long id) {
        pollRepository.deleteById(id);
    }

    // VoteOption methods
    public List<VoteOption> listVoteOptions() {
        return voteOptionRepository.findAll();
    }
    public VoteOption getVoteOption(Long id) {
        return voteOptionRepository.findById(id).orElse(null);
    }

    @Transactional
    public VoteOption createVoteOptionForPoll(Long pollId, String caption) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + pollId));

        VoteOption voteOption = new VoteOption();
        voteOption.setCaption(caption);
        voteOption.setPoll(poll);
        // Determine presentation order based on existing options
        voteOption.setPresentationOrder(poll.getOptions().size());

        return voteOptionRepository.save(voteOption);
    }

    @Transactional
    public void updateVoteOption(VoteOption voteOption) {
        if (voteOption.getId() != null) {
            // Ensure Poll reference is maintained
            VoteOption existing = voteOptionRepository.findById(voteOption.getId())
                    .orElseThrow(() -> new RuntimeException("Vote Option not found"));
            voteOption.setPoll(existing.getPoll());
            voteOptionRepository.save(voteOption);
        }
    }

    // Vote methods
    public List<Vote> listVotes() {
        return voteRepository.findAll();
    }

    public Vote getVote(Long id) {
        return voteRepository.findById(id).orElse(null);
    }

    @Transactional
    public void createVoteFromRequest(CreateVoteRequest request) {
        // Find entities to validate existence
        Long voterId = request.getVoterId();

        if (voterId != null && voterId != 0L) {
            userRepository.findById(voterId)
                    .orElseThrow(() -> new RuntimeException("Registered voter not found with id: " + voterId));
        }

        voteOptionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Vote option not found with id: " + request.getOptionId()));

        pollRepository.findById(request.getPollId())
                .orElseThrow(() -> new RuntimeException("Poll not found with id: " + request.getPollId()));

        // Build the event
        VoteEvent event = new VoteEvent(
                request.getPollId(),
                request.getOptionId(),
                request.getVoterId(),
                LocalDateTime.now()
        );

        // Publish the event
        eventPublisher.publishVoteEvent(event);
    }

    @Transactional
    public Long recordVoteFromEvent(VoteEvent event) {
        User voter = null;

        if (event.voterId() != null && event.voterId() != 0L) {
            voter = userRepository.findById(event.voterId()).orElse(null);
        }

        Poll poll = pollRepository.findById(event.pollId()).orElse(null);
        VoteOption option = voteOptionRepository.findById(event.optionId()).orElse(null);

        // Validation
        if (poll == null || option == null) {
            System.err.println("Event data invalid: Poll or Option not found for vote event: " + event);
            return -1L;
        }

        // Create the Vote entity
        Vote vote = new Vote();
        vote.setVotedAt(event.votedAt());
        vote.setVoter(voter);
        vote.setOption(option);
        vote.setPoll(poll);

        // Persist and update the cache
        Long id = voteRepository.save(vote).getId();

        valkeyVoteService.incrementVote(vote.getPoll().getId(), vote.getOption().getId());

        System.out.println("Vote recorded from event: " + id + (voter == null ? " (Anonymous)" : ""));
        return id;
    }

    /**
     * Implements the Cache-Aside pattern to get aggregated vote counts.
     */
    public Map<Long, Long> getPollResults(Long pollId) {
        Map<String, String> cachedCounts = valkeyVoteService.getVoteCounts(pollId);

        if (cachedCounts != null && !cachedCounts.isEmpty()) {
            return cachedCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()), // optionId
                            e -> Long.valueOf(e.getValue()) // count
                    ));
        }

        Poll poll = pollRepository.findById(pollId).orElse(null);
        if (poll == null) {
            return Map.of();
        }

        // Use the Poll entity's votes collection (relies on JPA to fetch it)
        Map<Long, Long> dbAggregatedCounts = poll.getVotes().stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getOption().getId(),
                        Collectors.counting()
                ));

        // Ensure all options are represented, even those with 0 votes
        Map<Long, Long> finalResults = poll.getOptions().stream()
                .collect(Collectors.toMap(
                        VoteOption::getId,
                        option -> dbAggregatedCounts.getOrDefault(option.getId(), 0L)
                ));

        Map<String, String> cacheMap = finalResults.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                ));

        valkeyVoteService.populateCache(pollId, cacheMap);

        return finalResults;
    }

    @Transactional
    public void deleteVote(Long id) {
        voteRepository.deleteById(id);
    }
}