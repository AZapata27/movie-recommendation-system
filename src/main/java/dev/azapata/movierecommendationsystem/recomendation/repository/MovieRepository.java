package dev.azapata.movierecommendationsystem.recomendation.repository;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends Neo4jRepository<Movie, String> {

    Optional<Movie> findByTitle(String title);

    @Query("""
            MATCH (m:Movie)
            OPTIONAL MATCH (:User)-[v:VOTED]->(m)
            OPTIONAL MATCH (m)<-[rd:DIRECTED]-(d:Person)
            OPTIONAL MATCH (m)<-[ra:ACTED_IN]-(a:Person)
            OPTIONAL MATCH (m)-[rg:HAS_GENRE]->(g:Genre)
            
            WHERE $genre IS NULL OR g.name = $genre
            WITH m, count(v) AS voteCount, rd, d, ra, a, rg, g
            ORDER BY voteCount DESC
            RETURN m, collect(DISTINCT rd), collect(DISTINCT d), collect(DISTINCT ra), collect(DISTINCT a), collect(DISTINCT rg), collect(DISTINCT g)
            """)
    List<Movie> findMoviesOrderedByVotes(@Param("genre") String genre);


    @Query("""
            MATCH (targetUser:User {name: $userName})
            
            MATCH (recommendedMovie:Movie)
            WHERE NOT EXISTS {
                MATCH (targetUser)-[:VOTED]->(recommendedMovie)
            }
            
            OPTIONAL MATCH (recommendedMovie)<-[vote:VOTED]-()
            WITH recommendedMovie,
                 count(vote) AS voteCount,
                 CASE WHEN count(vote) > 0 THEN avg(vote.ratingValue) ELSE 0 END AS avgRating
            
            ORDER BY voteCount DESC, avgRating DESC
            RETURN recommendedMovie {
                .title,
                .released,
                directors: [(recommendedMovie)<-[:DIRECTED]-(d:Person) | d],
                actors: [(recommendedMovie)<-[:ACTED_IN]-(a:Person) | a],
                genres: [(recommendedMovie)-[:HAS_GENRE]->(g:Genre) | g]
            } AS movie,
            avgRating,
            voteCount
            LIMIT 10
            """)
    List<Movie> basicRecommendadition(@Param("userName") String userName);


    @Query("""
            MATCH (targetUser:User {name: $userName})
            
            
            MATCH (movie:Movie)
            WHERE NOT EXISTS {
                MATCH (targetUser)-[:VOTED]->(movie)
            }
            
            
            OPTIONAL MATCH (targetUser)-[:VOTED]->(likedMovie:Movie)-[:HAS_GENRE]->(genre:Genre)<-[:HAS_GENRE]-(movie)
            WITH targetUser, movie, count(DISTINCT genre) AS genreOverlap
            
            
            OPTIONAL MATCH (targetUser)-[:VOTED]->(directorMovie:Movie)<-[:DIRECTED]-(director:Person)-[:DIRECTED]->(movie)
            WITH targetUser, movie, genreOverlap, count(DISTINCT director) AS sharedDirectors
            
            
            OPTIONAL MATCH (targetUser)-[:VOTED]->(actorMovie:Movie)<-[:ACTED_IN]-(actor:Person)-[:ACTED_IN]->(movie)
            WITH targetUser, movie, genreOverlap, sharedDirectors, count(DISTINCT actor) AS sharedActors
            
            
            OPTIONAL MATCH (movie)<-[vote:VOTED]-()
            WITH movie, genreOverlap, sharedDirectors, sharedActors,
                 count(vote) AS voteCount,
                 CASE WHEN count(vote) > 0 THEN avg(vote.ratingValue) ELSE 0 END AS avgRating
            
            
            WITH movie,
                 (genreOverlap * 3) +
                 (sharedDirectors * 5) +
                 (sharedActors * 2) +
                 (avgRating * 0.5) +
                 (voteCount * 0.1) AS score
            
            ORDER BY score DESC
            
            RETURN movie {
                .title,
                .released,
                directors: [(movie)<-[:DIRECTED]-(d:Person) | d],
                actors: [(movie)<-[:ACTED_IN]-(a:Person) | a],
                genres: [(movie)-[:HAS_GENRE]->(g:Genre) | g]
            } AS recommendedMovie,
            score
            LIMIT 10
            """)
    List<Movie> recommendMoviesForUser(@Param("userName") String userName);


}
