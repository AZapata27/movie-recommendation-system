package dev.azapata.movierecommendationsystem;

import org.springframework.boot.SpringApplication;

public class TestMovieRecommendationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.from(MovieRecommendationSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
