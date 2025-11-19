package hvl.dat250.group8.feedapp.controller;

import hvl.dat250.group8.feedapp.dto.UserResponseDTO;
import hvl.dat250.group8.feedapp.model.User;
import hvl.dat250.group8.feedapp.repository.UserRepository;
import hvl.dat250.group8.feedapp.security.JwtUtil;
import hvl.dat250.group8.feedapp.security.UserDetailsServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // Simple DTO for login request
    record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        // Authenticate the user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password())
            );
        } catch (Exception e) {
            // Throw 403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username or password");
        }

        // Load the full user details to get the ID
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found after authentication");
        }

        User user = userOptional.get();

        // Generate JWT
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(), user.getId());

        // Prepare response with token in header and user details in body
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        return new ResponseEntity<>(UserResponseDTO.fromEntity(user), headers, HttpStatus.OK);
    }
}