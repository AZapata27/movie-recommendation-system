package dev.azapata.movierecommendationsystem.recomendation;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Genre;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Person;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Rating;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Small factory for domain objects used across unit and web tests. */
public final class TestData {

    private TestData() {
    }

    public static Movie movie(String title) {
        return new Movie(title, 2010,
                List.of(new Person("Christopher Nolan")),
                List.of(new Person("Leonardo DiCaprio")),
                List.of(new Genre("Sci-Fi")));
    }

    /** A user whose ratings list is mutable, as Spring Data Neo4j would hydrate it. */
    public static User userWithRatings(String name, Rating... ratings) {
        User user = new User(name);
        ReflectionTestUtils.setField(user, "ratings", new ArrayList<>(Arrays.asList(ratings)));
        return user;
    }
}
