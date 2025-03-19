package dev.azapata.movierecommendationsystem.recomendation.model;

import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Getter
public class User {

    @Id
    private String name;

    @Relationship(type = "RATED")
    private List<Rating> ratings;

    public User(String name) {
        this.name = name;
    }
}
