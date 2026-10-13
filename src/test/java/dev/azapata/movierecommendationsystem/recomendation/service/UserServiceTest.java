package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.exception.ConflictException;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.UserRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.User;
import dev.azapata.movierecommendationsystem.recomendation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createsUserWhenNameIsFree() {
        when(userRepository.existsById("alice")).thenReturn(false);

        userService.createUser(new UserRequest("alice"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("alice");
    }

    @Test
    void rejectsDuplicateUserWithConflict() {
        when(userRepository.existsById("alice")).thenReturn(true);
        UserRequest request = new UserRequest("alice");

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User already exists: alice");
        verify(userRepository, never()).save(any());
    }
}
