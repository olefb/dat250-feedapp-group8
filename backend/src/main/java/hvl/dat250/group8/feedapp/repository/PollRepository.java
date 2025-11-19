package hvl.dat250.group8.feedapp.repository;

import hvl.dat250.group8.feedapp.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {}