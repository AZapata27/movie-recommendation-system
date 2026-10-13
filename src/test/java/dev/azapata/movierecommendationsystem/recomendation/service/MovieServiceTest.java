package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.exception.NotFoundException;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void createMovieSavesAndReturnsPersistedMovie() {
        Movie inception = movie("Inception");
        when(movieRepository.save(inception)).thenReturn(inception);

        Movie result = movieService.createMovie(inception);

        assertThat(result).isSameAs(inception);
        verify(movieRepository).save(inception);
    }

    @Test
    void createMovieRejectsNull() {
        assertThatThrownBy(() -> movieService.createMovie(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Movie request must not be null.");
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getMovieByTitleReturnsMovieWhenFound() {
        Movie inception = movie("Inception");
        when(movieRepository.findByTitle("Inception")).thenReturn(Optional.of(inception));

        assertThat(movieService.getMovieByTitle("Inception")).isSameAs(inception);
    }

    @Test
    void getMovieByTitleThrowsNotFoundWhenMissing() {
        when(movieRepository.findByTitle("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMovieByTitle("Unknown"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Movie not found: Unknown");
    }

    @Test
    void getMovieByTitleRejectsNullTitle() {
        assertThatThrownBy(() -> movieService.getMovieByTitle(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Movie title must not be null.");
        verifyNoInteractions(movieRepository);
    }

    @Test
    void getMoviesOrderedByVotesDelegatesGenreFilter() {
        List<Movie> movies = List.of(movie("Inception"), movie("Interstellar"));
        when(movieRepository.findMoviesOrderedByVotes("Sci-Fi")).thenReturn(movies);

        assertThat(movieService.getMoviesOrderedByVotes("Sci-Fi")).containsExactlyElementsOf(movies);
    }

    @Test
    void getMoviesOrderedByVotesAcceptsNullGenreForAllMovies() {
        when(movieRepository.findMoviesOrderedByVotes(null)).thenReturn(List.of());

        assertThat(movieService.getMoviesOrderedByVotes(null)).isEmpty();
        verify(movieRepository).findMoviesOrderedByVotes(null);
    }
}
