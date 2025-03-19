package dev.azapata.movierecommendationsystem.recomendation.controller;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{userName}")
    public List<Movie> recommendMoviesByUserName(@PathVariable String userName) {
        return recommendationService.recommendMoviesByUserName(userName);
    }

    @GetMapping("/basic/{userName}")
    public List<Movie> basicRecommendMoviesByUserName(@PathVariable String userName) {
        return recommendationService.basicRecommendationByUserName(userName);
    }
}
