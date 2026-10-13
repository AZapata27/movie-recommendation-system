package dev.azapata.movierecommendationsystem.recomendation.model.nodes;

import org.junit.jupiter.api.Test;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void newUserStartsWithEmptyMutableRatings() {
        User user = new User("alice");

        assertThat(user.getRatings()).isNotNull().isEmpty();

        user.getRatings().add(new Rating(null, 3, movie("Inception")));
        assertThat(user.getRatings()).hasSize(1);
    }
}
