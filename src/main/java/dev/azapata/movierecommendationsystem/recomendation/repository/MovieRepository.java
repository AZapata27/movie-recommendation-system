package dev.azapata.movierecommendationsystem.recomendation.repository;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Ratings are persisted by {@code User.ratings} as {@code (:User)-[:RATED {ratingValue}]->(:Movie)},
 * so every query that counts votes or averages ratings traverses {@code RATED}.
 * <p>
 * Custom queries return the movie node as the single root plus lists of its relationships and
 * related nodes, which is the shape Spring Data Neo4j needs to hydrate directors, actors and genres.
 * Ties are broken by title so the order is deterministic.
 */
public interface MovieRepository extends Neo4jRepository<Movie, String> {

    Optional<Movie> findByTitle(String title);

    @Query("""
            MATCH (m:Movie)
            WHERE $genre IS NULL OR EXISTS { (m)-[:HAS_GENRE]->(:Genre {name: $genre}) }
            OPTIONAL MATCH (:User)-[rating:RATED]->(m)
            WITH m, count(rating) AS voteCount
            RETURN m,
                   [(m)<-[rd:DIRECTED]-(:Person) | rd] AS directedRels,
                   [(m)<-[:DIRECTED]-(d:Person) | d] AS directors,
                   [(m)<-[ra:ACTED_IN]-(:Person) | ra] AS actedInRels,
                   [(m)<-[:ACTED_IN]-(a:Person) | a] AS actors,
                   [(m)-[rg:HAS_GENRE]->(:Genre) | rg] AS genreRels,
                   [(m)-[:HAS_GENRE]->(g:Genre) | g] AS genres
            ORDER BY voteCount DESC, m.title ASC
            """)
    List<Movie> findMoviesOrderedByVotes(@Param("genre") String genre);


    @Query("""
            MATCH (targetUser:User {name: $userName})
            MATCH (recommendedMovie:Movie)
            WHERE NOT EXISTS { (targetUser)-[:RATED]->(recommendedMovie) }

            OPTIONAL MATCH (:User)-[rating:RATED]->(recommendedMovie)
            WITH recommendedMovie,
                 count(rating) AS voteCount,
                 coalesce(avg(rating.ratingValue), 0) AS avgRating

            RETURN recommendedMovie,
                   [(recommendedMovie)<-[rd:DIRECTED]-(:Person) | rd] AS directedRels,
                   [(recommendedMovie)<-[:DIRECTED]-(d:Person) | d] AS directors,
                   [(recommendedMovie)<-[ra:ACTED_IN]-(:Person) | ra] AS actedInRels,
                   [(recommendedMovie)<-[:ACTED_IN]-(a:Person) | a] AS actors,
                   [(recommendedMovie)-[rg:HAS_GENRE]->(:Genre) | rg] AS genreRels,
                   [(recommendedMovie)-[:HAS_GENRE]->(g:Genre) | g] AS genres
            ORDER BY voteCount DESC, avgRating DESC, recommendedMovie.title ASC
            LIMIT 10
            """)
    List<Movie> basicRecommendadition(@Param("userName") String userName);


    @Query("""
            MATCH (targetUser:User {name: $userName})
            MATCH (movie:Movie)
            WHERE NOT EXISTS { (targetUser)-[:RATED]->(movie) }

            OPTIONAL MATCH (targetUser)-[:RATED]->(:Movie)-[:HAS_GENRE]->(genre:Genre)<-[:HAS_GENRE]-(movie)
            WITH targetUser, movie, count(DISTINCT genre) AS genreOverlap

            OPTIONAL MATCH (targetUser)-[:RATED]->(:Movie)<-[:DIRECTED]-(director:Person)-[:DIRECTED]->(movie)
            WITH targetUser, movie, genreOverlap, count(DISTINCT director) AS sharedDirectors

            OPTIONAL MATCH (targetUser)-[:RATED]->(:Movie)<-[:ACTED_IN]-(actor:Person)-[:ACTED_IN]->(movie)
            WITH movie, genreOverlap, sharedDirectors, count(DISTINCT actor) AS sharedActors

            OPTIONAL MATCH (:User)-[rating:RATED]->(movie)
            WITH movie, genreOverlap, sharedDirectors, sharedActors,
                 count(rating) AS voteCount,
                 coalesce(avg(rating.ratingValue), 0) AS avgRating

            WITH movie,
                 (genreOverlap * 3) +
                 (sharedDirectors * 5) +
                 (sharedActors * 2) +
                 (avgRating * 0.5) +
                 (voteCount * 0.1) AS score

            RETURN movie,
                   [(movie)<-[rd:DIRECTED]-(:Person) | rd] AS directedRels,
                   [(movie)<-[:DIRECTED]-(d:Person) | d] AS directors,
                   [(movie)<-[ra:ACTED_IN]-(:Person) | ra] AS actedInRels,
                   [(movie)<-[:ACTED_IN]-(a:Person) | a] AS actors,
                   [(movie)-[rg:HAS_GENRE]->(:Genre) | rg] AS genreRels,
                   [(movie)-[:HAS_GENRE]->(g:Genre) | g] AS genres
            ORDER BY score DESC, movie.title ASC
            LIMIT 10
            """)
    List<Movie> recommendMoviesForUser(@Param("userName") String userName);


}
