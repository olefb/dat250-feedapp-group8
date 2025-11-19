package hvl.dat250.group8.feedapp.repository;

import hvl.dat250.group8.feedapp.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {}