package dev.azapata.movierecommendationsystem.recomendation.dto.request;

import dev.azapata.movierecommendationsystem.recomendation.model.Genre;
import dev.azapata.movierecommendationsystem.recomendation.model.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.Person;

import java.util.List;

public record MovieRequest(
        String title,
        Integer released,
        List<DirectorRequest> director,
        List<ActorRequest> actors,
        List<GenreRequest> genres
) {

    public Movie toMovie() {
        return new Movie(
                this.title,
                this.released,
                this.director.stream()
                        .map(directorRequest -> new Person(directorRequest.name))
                        .toList(),
                this.actors.stream()
                        .map(actorRequest -> new Person(actorRequest.name()))
                        .toList(),
                this.genres.stream()
                        .map(genreRequest -> new Genre(genreRequest.name()))
                        .toList()
        );
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