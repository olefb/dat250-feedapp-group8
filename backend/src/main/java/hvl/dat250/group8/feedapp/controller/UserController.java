package hvl.dat250.group8.feedapp.controller;

import hvl.dat250.group8.feedapp.dto.CreateUserRequest;
import hvl.dat250.group8.feedapp.dto.UserResponseDTO;
import hvl.dat250.group8.feedapp.service.PollManager;
import hvl.dat250.group8.feedapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final PollManager pollManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(PollManager pollManager, PasswordEncoder passwordEncoder)
    {
        this.pollManager = pollManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        Long id = pollManager.createUser(user);
        user.setId(id); // ID is set by the manager after persistence

        return new ResponseEntity<>(UserResponseDTO.fromEntity(user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public UserResponseDTO get(@PathVariable Long id) {
        User user = pollManager.getUser(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return UserResponseDTO.fromEntity(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        pollManager.updateUser(user);
    }
}