package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MovieRepository movieRepository;

    public List<Movie> recommendMoviesByUserName(String userName) {

        Objects.requireNonNull(userName, "User name must not be null.");

        return movieRepository.recommendMoviesForUser(userName);
    }
    public List<Movie> basicRecommendationByUserName(String userName){
        Objects.requireNonNull(userName, "User name must not be null.");

        return movieRepository.basicRecommendadition(userName);
    }

}
