package dev.azapata.movierecommendationsystem.recomendation;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends Neo4jRepository<Movie, String> {

    Optional<Movie> findByTitle(String title);

    List<Movie> findAllByGenres(List<Genre> genres);
}
