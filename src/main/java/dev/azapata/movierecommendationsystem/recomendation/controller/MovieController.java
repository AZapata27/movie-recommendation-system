package dev.azapata.movierecommendationsystem.recomendation.controller;

import dev.azapata.movierecommendationsystem.recomendation.model.dto.MovieRatingRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.dto.MovieRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.service.MovieService;
import dev.azapata.movierecommendationsystem.recomendation.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movie createMovie(@RequestBody MovieRequest request) {

        Movie movie = request.toMovie();
        return movieService.createMovie(movie);
    }

    @GetMapping("/{title}")
    public Movie getMovieByTitle(@PathVariable String title) {
        return movieService.getMovieByTitle(title);
    }

    @GetMapping
    public List<Movie> getMoviesOrderedByVotes(@RequestParam(name = "genre", required = false) String genre) {
        return movieService.getMoviesOrderedByVotes(genre);
    }


    @PostMapping("/rate")
    @ResponseStatus(HttpStatus.CREATED)
    public void rateMovie(@RequestBody MovieRatingRequest request) {
        ratingService.rateMovie(request);
    }

}