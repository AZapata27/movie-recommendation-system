package dev.azapata.movierecommendationsystem.recomendation.repository;

import dev.azapata.movierecommendationsystem.TestcontainersConfiguration;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.MovieRatingRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.UserRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Genre;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Person;
import dev.azapata.movierecommendationsystem.recomendation.service.RatingService;
import dev.azapata.movierecommendationsystem.recomendation.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the custom Cypher queries of {@link MovieRepository} against a real Neo4j (Testcontainers).
 * Ratings are written through {@link RatingService}, i.e. exactly as the API persists them
 * ({@code (:User)-[:RATED {ratingValue}]->(:Movie)}), so the queries are checked against the real model.
 * <p>
 * Seeded graph:
 * <pre>
 * Inception    (Nolan;    DiCaprio;     Sci-Fi, Thriller)  rated by alice 5, bob 4, carol 3  -> 3 votes
 * Interstellar (Nolan;    McConaughey;  Sci-Fi)            rated by bob 5, carol 2          -> 2 votes, avg 3.5
 * The Revenant (Inarritu; DiCaprio;     Drama)             rated by carol 4, dave 5         -> 2 votes, avg 4.5
 * Amelie       (Jeunet;   Tautou;       Romance)           not rated                        -> 0 votes
 * </pre>
 * Skipped automatically when no Docker daemon is available; it always runs in CI.
 */
@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MovieRepositoryIntegrationTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private Neo4jClient neo4jClient;

    @BeforeEach
    void seedGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();
        neo4jClient.query("""
                CREATE (nolan:Person {name: 'Christopher Nolan'}),
                       (inarritu:Person {name: 'Alejandro G. Inarritu'}),
                       (jeunet:Person {name: 'Jean-Pierre Jeunet'}),
                       (leo:Person {name: 'Leonardo DiCaprio'}),
                       (mcc:Person {name: 'Matthew McConaughey'}),
                       (tautou:Person {name: 'Audrey Tautou'}),
                       (scifi:Genre {name: 'Sci-Fi'}),
                       (thriller:Genre {name: 'Thriller'}),
                       (drama:Genre {name: 'Drama'}),
                       (romance:Genre {name: 'Romance'}),
                       (inception:Movie {title: 'Inception', released: 2010}),
                       (interstellar:Movie {title: 'Interstellar', released: 2014}),
                       (revenant:Movie {title: 'The Revenant', released: 2015}),
                       (amelie:Movie {title: 'Amelie', released: 2001}),
                       (nolan)-[:DIRECTED]->(inception),
                       (nolan)-[:DIRECTED]->(interstellar),
                       (inarritu)-[:DIRECTED]->(revenant),
                       (jeunet)-[:DIRECTED]->(amelie),
                       (leo)-[:ACTED_IN]->(inception),
                       (leo)-[:ACTED_IN]->(revenant),
                       (mcc)-[:ACTED_IN]->(interstellar),
                       (tautou)-[:ACTED_IN]->(amelie),
                       (inception)-[:HAS_GENRE]->(scifi),
                       (inception)-[:HAS_GENRE]->(thriller),
                       (interstellar)-[:HAS_GENRE]->(scifi),
                       (revenant)-[:HAS_GENRE]->(drama),
                       (amelie)-[:HAS_GENRE]->(romance)
                """).run();

        for (String user : List.of("alice", "bob", "carol", "dave")) {
            userService.createUser(new UserRequest(user));
        }

        // alice first rates 1 and then changes her mind: it must stay a single vote.
        rate("alice", "Inception", 1);
        rate("alice", "Inception", 5);
        rate("bob", "Inception", 4);
        rate("bob", "Interstellar", 5);
        rate("carol", "Inception", 3);
        rate("carol", "Interstellar", 2);
        rate("carol", "The Revenant", 4);
        rate("dave", "The Revenant", 5);
    }

    @Test
    void ratingsArePersistedAsRatedRelationships() {
        Long rated = neo4jClient.query("MATCH (:User)-[r:RATED]->(:Movie) RETURN count(r)")
                .fetchAs(Long.class).one().orElseThrow();

        assertThat(rated).isEqualTo(7L);
    }

    @Test
    void ordersAllMoviesByNumberOfRatingsWhenGenreIsNull() {
        List<Movie> movies = movieRepository.findMoviesOrderedByVotes(null);

        assertThat(movies).extracting(Movie::getTitle)
                .containsExactly("Inception", "Interstellar", "The Revenant", "Amelie");
    }

    @Test
    void hydratesDirectorsActorsAndGenres() {
        Movie inception = movieRepository.findMoviesOrderedByVotes(null).getFirst();

        assertThat(inception.getTitle()).isEqualTo("Inception");
        assertThat(inception.getReleased()).isEqualTo(2010);
        assertThat(inception.getDirectors()).extracting(Person::getName).containsExactly("Christopher Nolan");
        assertThat(inception.getActors()).extracting(Person::getName).containsExactly("Leonardo DiCaprio");
        assertThat(inception.getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Sci-Fi", "Thriller");
    }

    @Test
    void genreFilterRestrictsTheMoviesReturned() {
        assertThat(movieRepository.findMoviesOrderedByVotes("Sci-Fi"))
                .extracting(Movie::getTitle)
                .containsExactly("Inception", "Interstellar");

        assertThat(movieRepository.findMoviesOrderedByVotes("Drama"))
                .extracting(Movie::getTitle)
                .containsExactly("The Revenant");
    }

    @Test
    void genreFilterKeepsAllGenresOfTheMatchingMovies() {
        List<Movie> thrillers = movieRepository.findMoviesOrderedByVotes("Thriller");

        assertThat(thrillers).extracting(Movie::getTitle).containsExactly("Inception");
        assertThat(thrillers.getFirst().getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Sci-Fi", "Thriller");
    }

    @Test
    void unknownGenreReturnsNoMovies() {
        assertThat(movieRepository.findMoviesOrderedByVotes("Documentary")).isEmpty();
    }

    @Test
    void basicRecommendationExcludesRatedMoviesAndOrdersByVotesThenAverage() {
        // Interstellar and The Revenant both have 2 votes; The Revenant wins on average (4.5 vs 3.5).
        List<Movie> recommended = movieRepository.basicRecommendadition("alice");

        assertThat(recommended).extracting(Movie::getTitle)
                .containsExactly("The Revenant", "Interstellar", "Amelie");
    }

    @Test
    void personalizedRecommendationUsesTheUsersRatedMovies() {
        // alice rated Inception: Interstellar shares director + genre (score 9.95),
        // The Revenant shares an actor (4.45), Amelie shares nothing (0).
        List<Movie> recommended = movieRepository.recommendMoviesForUser("alice");

        assertThat(recommended).extracting(Movie::getTitle)
                .containsExactly("Interstellar", "The Revenant", "Amelie");
        assertThat(recommended.getFirst().getDirectors()).extracting(Person::getName)
                .containsExactly("Christopher Nolan");
    }

    @Test
    void recommendationsExcludeEveryMovieTheUserRated() {
        assertThat(movieRepository.recommendMoviesForUser("carol"))
                .extracting(Movie::getTitle)
                .containsExactly("Amelie");
        assertThat(movieRepository.basicRecommendadition("carol"))
                .extracting(Movie::getTitle)
                .containsExactly("Amelie");
    }

    @Test
    void unknownUserGetsNoRecommendations() {
        assertThat(movieRepository.recommendMoviesForUser("ghost")).isEmpty();
        assertThat(movieRepository.basicRecommendadition("ghost")).isEmpty();
    }

    private void rate(String user, String title, int value) {
        ratingService.rateMovie(new MovieRatingRequest(user, title, value));
    }
}
