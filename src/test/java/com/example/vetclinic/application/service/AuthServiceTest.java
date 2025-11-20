package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.auth.JwtAuthenticationResponse;
import com.example.vetclinic.application.dto.auth.LoginRequest;
import com.example.vetclinic.application.dto.auth.SignUpRequest;
import com.example.vetclinic.domain.model.Role;
import com.example.vetclinic.domain.model.User;
import com.example.vetclinic.infrastructure.persistence.RoleJpaRepository;
import com.example.vetclinic.infrastructure.persistence.UserJpaRepository;
import com.example.vetclinic.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private SignUpRequest signUpRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testuser");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .roles(Collections.singleton(role))
                .build();
    }

    @Test
    void authenticateUser_ShouldReturnJwtResponse_WhenCredentialsAreValid() {
        // Given
        String token = "jwt-token";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn(token);

        // When
        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(token);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void authenticateUser_ShouldThrowException_WhenCredentialsAreInvalid() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider, never()).generateToken(any());
    }

    @Test
    void registerUser_ShouldReturnUser_WhenUsernameAndEmailAreAvailable() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = authService.registerUser(signUpRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsTaken() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username is already taken");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsTaken() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email Address already in use");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenRoleNotFound() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User Role not set");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository, never()).save(any());
    }
}

