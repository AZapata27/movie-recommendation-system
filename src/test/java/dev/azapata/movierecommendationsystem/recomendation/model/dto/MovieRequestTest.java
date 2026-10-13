package dev.azapata.movierecommendationsystem.recomendation.model.dto;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Genre;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieRequestTest {

    @Test
    void mapsRequestToMovieGraph() {
        MovieRequest request = new MovieRequest(
                "Inception",
                2010,
                List.of(new MovieRequest.DirectorRequest("Christopher Nolan")),
                List.of(new MovieRequest.ActorRequest("Leonardo DiCaprio", "Cobb"),
                        new MovieRequest.ActorRequest("Elliot Page", "Ariadne")),
                List.of(new MovieRequest.GenreRequest("Sci-Fi"), new MovieRequest.GenreRequest("Thriller")));

        Movie movie = request.toMovie();

        assertThat(movie.getTitle()).isEqualTo("Inception");
        assertThat(movie.getReleased()).isEqualTo(2010);
        assertThat(movie.getDirectors()).extracting(Person::getName).containsExactly("Christopher Nolan");
        assertThat(movie.getActors()).extracting(Person::getName).containsExactly("Leonardo DiCaprio", "Elliot Page");
        assertThat(movie.getGenres()).extracting(Genre::getName).containsExactly("Sci-Fi", "Thriller");
    }

    @Test
    void mapsEmptyCollections() {
        Movie movie = new MovieRequest("Untitled", null, List.of(), List.of(), List.of()).toMovie();

        assertThat(movie.getReleased()).isNull();
        assertThat(movie.getDirectors()).isEmpty();
        assertThat(movie.getActors()).isEmpty();
        assertThat(movie.getGenres()).isEmpty();
    }
}
