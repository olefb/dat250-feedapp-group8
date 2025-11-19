package hvl.dat250.group8.feedapp.repository;

import hvl.dat250.group8.feedapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}