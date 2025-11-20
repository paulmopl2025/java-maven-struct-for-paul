package com.example.vetclinic.application.service;

import com.example.vetclinic.application.dto.user.UpdateUserDTO;
import com.example.vetclinic.application.dto.user.UserDTO;
import com.example.vetclinic.domain.model.User;
import com.example.vetclinic.infrastructure.mapper.UserMapper;
import com.example.vetclinic.infrastructure.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@test.com");
        user.setPassword("encodedPassword");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("admin");
        userDTO.setEmail("admin@test.com");

        updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setEmail("newemail@test.com");
        updateUserDTO.setPassword("newPassword123");
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() {
        // Given
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("admin");
        verify(userRepository).findAll();
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("admin");
        verify(userRepository).findById(1L);
        verify(userMapper).toDTO(user);
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(999L);
        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenEmailIsUpdated() {
        // Given
        userDTO.setEmail("newemail@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@test.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("newemail@test.com");
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("newemail@test.com");
        verify(userRepository).save(user);
        verify(userMapper).toDTO(user);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenPasswordIsUpdated() {
        // Given
        updateUserDTO.setEmail(null);
        updateUserDTO.setPassword("newPassword123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // When
        UserDTO result = userService.updateUser(1L, updateUserDTO);

        // Then
        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyInUse() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("newemail@test.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updateUserDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("newemail@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(999L, updateUserDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldCallRepository_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }
}
