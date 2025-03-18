package dev.azapata.movierecommendationsystem.recomendation;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends Neo4jRepository<Movie, String> {

    Movie findByTitle(String title);

    List<Movie> findAllByGenres(List<Genre> genres);
}
