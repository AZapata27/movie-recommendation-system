package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.exception.BadRequestException;
import dev.azapata.movierecommendationsystem.recomendation.exception.NotFoundException;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.MovieRatingRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Rating;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.User;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import dev.azapata.movierecommendationsystem.recomendation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static dev.azapata.movierecommendationsystem.recomendation.TestData.userWithRatings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingService ratingService;

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void addsNewRatingWhenUserHasNotRatedTheMovie(int value) {
        User alice = userWithRatings("alice");
        Movie inception = movie("Inception");
        when(userRepository.findById("alice")).thenReturn(Optional.of(alice));
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(inception));

        ratingService.rateMovie(new MovieRatingRequest("alice", "Inception", value));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getRatings())
                .singleElement()
                .satisfies(rating -> {
                    assertThat(rating.getMovie()).isSameAs(inception);
                    assertThat(rating.getRatingValue()).isEqualTo(value);
                });
    }

    @Test
    void addsFirstRatingForUserBuiltWithConstructor() {
        User bob = new User("bob");
        Movie inception = movie("Inception");
        when(userRepository.findById("bob")).thenReturn(Optional.of(bob));
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(inception));

        ratingService.rateMovie(new MovieRatingRequest("bob", "Inception", 4));

        verify(userRepository).save(bob);
        assertThat(bob.getRatings())
                .singleElement()
                .satisfies(rating -> {
                    assertThat(rating.getMovie()).isSameAs(inception);
                    assertThat(rating.getRatingValue()).isEqualTo(4);
                });
    }

    @Test
    void updatesExistingRatingInsteadOfDuplicatingIt() {
        Rating existing = new Rating(42L, 2, movie("Inception"));
        Rating other = new Rating(43L, 4, movie("Interstellar"));
        User alice = userWithRatings("alice", existing, other);
        when(userRepository.findById("alice")).thenReturn(Optional.of(alice));
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(movie("Inception")));

        ratingService.rateMovie(new MovieRatingRequest("alice", "Inception", 5));

        verify(userRepository).save(alice);
        assertThat(alice.getRatings()).hasSize(2);
        assertThat(existing.getRatingId()).isEqualTo(42L);
        assertThat(existing.getRatingValue()).isEqualTo(5);
        assertThat(other.getRatingValue()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 6, Integer.MAX_VALUE})
    void rejectsRatingOutsideOneToFive(int value) {
        MovieRatingRequest request = new MovieRatingRequest("alice", "Inception", value);

        assertThatThrownBy(() -> ratingService.rateMovie(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Rating must be a value between 1 and 5.");
        verifyNoInteractions(userRepository, movieRepository);
    }

    @Test
    void throwsNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById("ghost")).thenReturn(Optional.empty());
        MovieRatingRequest request = new MovieRatingRequest("ghost", "Inception", 3);

        assertThatThrownBy(() -> ratingService.rateMovie(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found: ghost");
        verifyNoInteractions(movieRepository);
        verify(userRepository, never()).save(any());
    }

    @Test
    void throwsNotFoundWhenMovieDoesNotExist() {
        when(userRepository.findById("alice")).thenReturn(Optional.of(userWithRatings("alice")));
        when(movieRepository.findByTitle("Unknown")).thenReturn(Optional.empty());
        MovieRatingRequest request = new MovieRatingRequest("alice", "Unknown", 3);

        assertThatThrownBy(() -> ratingService.rateMovie(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Movie not found: Unknown");
        verify(userRepository, never()).save(any());
    }
}
