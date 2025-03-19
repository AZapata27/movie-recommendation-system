package dev.azapata.movierecommendationsystem.recomendation.repository;

import dev.azapata.movierecommendationsystem.recomendation.model.nodes.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface UserRepository extends Neo4jRepository<User, String> {
}
