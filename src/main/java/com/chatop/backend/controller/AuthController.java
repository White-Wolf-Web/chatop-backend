package com.chatop.backend.controller;

import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chatop.backend.service.JWTService;

@RestController
// @RequestMapping("/api/auth")
public class AuthController {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JWTService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> getToken(Authentication authentication) {
        try {
            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok(jwtService.generateToken(authentication));
            } else {
                throw new BadCredentialsException("Utilisateur non authentifié");
            }
        } catch (UsernameNotFoundException ex) {                            // Retourne un statut 404 Not Found
            return ResponseEntity.notFound().build();
        } catch (BadCredentialsException ex) {                              // Retourne un statut 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {                                            // Retourne un statut 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (userRepository.findByName(user.getName())!= null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur déjà utilisé");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode le mot de passe

            User savedUser = userRepository.save(user);                   // Enregistre le nouvel utilisateur

            String token = jwtService.generateTokenForUser(savedUser);    // Génère un token JWT

            return ResponseEntity.ok().body(token);                       // Retourne le token JWT avec une réponse 200 OK

        } catch (IllegalArgumentException ex) {                           // Gère les erreurs liées aux arguments invalides
            return ResponseEntity.badRequest().body("Données invalides fournies");

        } catch (Exception ex) {                                          // Gère toute autre exception inattendue
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'enregistrement");
        }
    }
}