package dev.azapata.movierecommendationsystem.recomendation.model.dto;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Genre;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Person;

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