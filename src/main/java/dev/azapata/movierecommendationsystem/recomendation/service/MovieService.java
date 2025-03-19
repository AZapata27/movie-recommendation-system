package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.exception.NotFoundException;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie createMovie(Movie movie) {

        Objects.requireNonNull(movie, "Movie request must not be null.");
        return movieRepository.save(movie);
    }


    public Movie getMovieByTitle(String title) {
        Objects.requireNonNull(title, "Movie title must not be null.");

        Optional<Movie> movie = movieRepository.findByTitle(title);
        return movie.orElseThrow(() -> new NotFoundException("Movie not found: " + title));
    }

    @GetMapping
    public List<Movie> getMoviesOrderedByVotes(String genre) {
        return movieRepository.findMoviesOrderedByVotes(genre);
    }

}
