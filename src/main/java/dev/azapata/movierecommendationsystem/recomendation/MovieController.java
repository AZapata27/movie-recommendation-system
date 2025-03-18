package dev.azapata.movierecommendationsystem.recomendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieRepository movieRepository;

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody MovieRequest request) {

        Movie movie = request.toMovie();

        Movie newMovie = movieRepository.save(movie);
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
    }

    @GetMapping("/{title}")
    public ResponseEntity<Movie> getMovieByTitle(@PathVariable String title) {
        Optional<Movie> movie = movieRepository.findByTitle(title);
        return movie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    public record MovieRequest(
            String title,
            Integer released,
            DirectorRequest director,
            List<ActorRequest> actors,
            List<GenreRequest> genres
    ) {

        Movie toMovie() {
            return new Movie(
                    this.title,
                    this.released,
                    new Person(this.director.name),
                    this.actors.stream()
                    .map(actorRequest -> new Person(actorRequest.name()))
                    .toList(),
                    this.genres.stream()
                            .map(genreRequest -> new Genre(genreRequest.name()))
                            .toList()
            );
        }
    }

    public record DirectorRequest(
            String name
    ) {
    }

    public record ActorRequest(
            String name,
            String role
    ) {
    }

    public record GenreRequest(
            String name
    ) {
    }


}