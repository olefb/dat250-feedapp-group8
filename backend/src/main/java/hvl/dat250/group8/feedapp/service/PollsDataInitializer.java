package hvl.dat250.group8.feedapp.service;

import hvl.dat250.group8.feedapp.dto.CreatePollRequest;
import hvl.dat250.group8.feedapp.dto.VoteEvent;
import hvl.dat250.group8.feedapp.model.Poll;
import hvl.dat250.group8.feedapp.model.User;
import hvl.dat250.group8.feedapp.model.VoteOption;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PollsDataInitializer {

    private final PollManager pollManager;
    private final PasswordEncoder passwordEncoder;

    public PollsDataInitializer(PollManager pollManager, PasswordEncoder passwordEncoder) {
        this.pollManager = pollManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void initializeData() {
        populateDataDirectly();
    }

    private void populateDataDirectly() {
        if (!pollManager.listUsers().isEmpty()) {
            System.out.println("Persistent Database detected. Skipping data initialization.");
            return;
        }

        String defaultPassword = passwordEncoder.encode("password");

        // Create users
        User alice = new User("alice", "alice@online.com", defaultPassword);
        pollManager.createUser(alice); // ID 1
        User bob = new User("bob", "bob@bob.home", defaultPassword);
        pollManager.createUser(bob); // ID 2
        User eve = new User("eve", "eve@mail.org", defaultPassword);
        pollManager.createUser(eve); // ID 3

        // Create Poll 1 (Vim or Emacs?)
        CreatePollRequest poll1Request = new CreatePollRequest();
        poll1Request.setUserId(alice.getId().toString());
        poll1Request.setQuestion("Vim or Emacs?");
        poll1Request.setOptions(List.of("Vim", "Emacs"));

        Poll poll1 = pollManager.createPollWithOptions(poll1Request);
        // We need to fetch the options to get their IDs for voting
        VoteOption vim = poll1.getOptions().stream()
                .filter(o -> o.getCaption().equals("Vim")).findFirst().get();
        VoteOption emacs = poll1.getOptions().stream()
                .filter(o -> o.getCaption().equals("Emacs")).findFirst().get();

        // Create Poll 2 (Pineapple on Pizza)
        CreatePollRequest poll2Request = new CreatePollRequest();
        poll2Request.setUserId(eve.getId().toString());
        poll2Request.setQuestion("Pineapple on Pizza");
        poll2Request.setOptions(List.of("Yes! Yammy!", "Mamma mia: Nooooo!"));

        Poll poll2 = pollManager.createPollWithOptions(poll2Request);
        // We need to fetch the options to get their IDs for voting
        VoteOption yes = poll2.getOptions().stream()
                .filter(o -> o.getCaption().equals("Yes! Yammy!")).findFirst().get();
        VoteOption no = poll2.getOptions().stream()
                 .filter(o -> o.getCaption().equals("Mamma mia: Nooooo!")).findFirst().get();

        // Alice votes for Vim (Poll 1, Option 1)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), vim.getId(), alice.getId(), LocalDateTime.now().minusSeconds(5)
        ));

        // Bob votes for Vim (Poll 1, Option 1)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), vim.getId(), bob.getId(), LocalDateTime.now().minusSeconds(4)
        ));

        // Eve votes for Emacs (Poll 1, Option 2)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), emacs.getId(), eve.getId(), LocalDateTime.now().minusSeconds(3)
        ));

        // Eve votes for Yes (Poll 2, Option 3)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll2.getId(), yes.getId(), eve.getId(), LocalDateTime.now().minusSeconds(2)
        ));
    }
}