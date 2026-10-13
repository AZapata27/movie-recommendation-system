package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void personalizedRecommendationsUseScoredQueryAndPreserveRanking() {
        List<Movie> ranked = List.of(movie("Interstellar"), movie("Tenet"), movie("Dunkirk"));
        when(movieRepository.recommendMoviesForUser("alice")).thenReturn(ranked);

        List<Movie> result = recommendationService.recommendMoviesByUserName("alice");

        assertThat(result).extracting(Movie::getTitle).containsExactly("Interstellar", "Tenet", "Dunkirk");
        verify(movieRepository, never()).basicRecommendadition("alice");
    }

    @Test
    void basicRecommendationsUsePopularityQuery() {
        List<Movie> popular = List.of(movie("Inception"));
        when(movieRepository.basicRecommendadition("alice")).thenReturn(popular);

        assertThat(recommendationService.basicRecommendationByUserName("alice")).containsExactlyElementsOf(popular);
        verify(movieRepository, never()).recommendMoviesForUser("alice");
    }

    @Test
    void returnsEmptyListWhenNothingToRecommend() {
        when(movieRepository.recommendMoviesForUser("newcomer")).thenReturn(List.of());

        assertThat(recommendationService.recommendMoviesByUserName("newcomer")).isEmpty();
    }

    @Test
    void personalizedRecommendationsRejectNullUserName() {
        assertThatThrownBy(() -> recommendationService.recommendMoviesByUserName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User name must not be null.");
        verifyNoInteractions(movieRepository);
    }

    @Test
    void basicRecommendationsRejectNullUserName() {
        assertThatThrownBy(() -> recommendationService.basicRecommendationByUserName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User name must not be null.");
        verifyNoInteractions(movieRepository);
    }
}
