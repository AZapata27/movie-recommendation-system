package dev.azapata.movierecommendationsystem.recomendation.controller;

import dev.azapata.movierecommendationsystem.recomendation.exception.BadRequestException;
import dev.azapata.movierecommendationsystem.recomendation.exception.NotFoundException;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.MovieRatingRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.service.MovieService;
import dev.azapata.movierecommendationsystem.recomendation.service.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private RatingService ratingService;

    @Test
    void createMovieReturns201WithMappedMovie() throws Exception {
        when(movieService.createMovie(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Inception",
                                  "released": 2010,
                                  "director": [{"name": "Christopher Nolan"}],
                                  "actors": [{"name": "Leonardo DiCaprio", "role": "Cobb"}],
                                  "genres": [{"name": "Sci-Fi"}]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.released").value(2010))
                .andExpect(jsonPath("$.directors[0].name").value("Christopher Nolan"))
                .andExpect(jsonPath("$.actors[0].name").value("Leonardo DiCaprio"))
                .andExpect(jsonPath("$.genres[0].name").value("Sci-Fi"));
    }

    @Test
    void getMovieByTitleReturnsMovie() throws Exception {
        when(movieService.getMovieByTitle("Inception")).thenReturn(movie("Inception"));

        mockMvc.perform(get("/api/v1/movies/{title}", "Inception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    @Test
    void getMovieByTitleMapsNotFoundToProblemDetail() throws Exception {
        when(movieService.getMovieByTitle("Unknown")).thenThrow(new NotFoundException("Movie not found: Unknown"));

        mockMvc.perform(get("/api/v1/movies/{title}", "Unknown"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Movie not found: Unknown"));
    }

    @Test
    void listMoviesPassesGenreFilter() throws Exception {
        when(movieService.getMoviesOrderedByVotes("Sci-Fi")).thenReturn(List.of(movie("Inception"), movie("Tenet")));

        mockMvc.perform(get("/api/v1/movies").param("genre", "Sci-Fi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Inception"))
                .andExpect(jsonPath("$[1].title").value("Tenet"));
    }

    @Test
    void listMoviesWithoutGenreQueriesAllMovies() throws Exception {
        when(movieService.getMoviesOrderedByVotes(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(movieService).getMoviesOrderedByVotes(isNull());
    }

    @Test
    void rateMovieReturns201AndForwardsRequest() throws Exception {
        mockMvc.perform(post("/api/v1/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName": "alice", "movieTitle": "Inception", "ratingValue": 4}
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<MovieRatingRequest> captor = ArgumentCaptor.forClass(MovieRatingRequest.class);
        verify(ratingService).rateMovie(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new MovieRatingRequest("alice", "Inception", 4));
    }

    @Test
    void rateMovieMapsInvalidRatingTo400() throws Exception {
        doThrow(new BadRequestException("Rating must be a value between 1 and 5."))
                .when(ratingService).rateMovie(any());

        mockMvc.perform(post("/api/v1/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName": "alice", "movieTitle": "Inception", "ratingValue": 9}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Rating must be a value between 1 and 5."));
    }

    @Test
    void rateMovieMapsMissingUserTo404() throws Exception {
        doThrow(new NotFoundException("User not found: ghost")).when(ratingService).rateMovie(any());

        mockMvc.perform(post("/api/v1/movies/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userName": "ghost", "movieTitle": "Inception", "ratingValue": 3}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("User not found: ghost"));
    }
}
