package dev.azapata.movierecommendationsystem.recomendation;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Director")
public class Director {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Relationship(type = "DIRECTED_IN", direction = Relationship.Direction.OUTGOING)
    private List<Movie> movies;

}
