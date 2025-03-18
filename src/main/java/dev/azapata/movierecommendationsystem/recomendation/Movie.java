package dev.azapata.movierecommendationsystem.recomendation;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Book")
public class Movie {

    @Id
    private String id;
    
    @Property("title")
    private String title;

    @Property("year")
    private Integer year;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List<Actor> actors;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.OUTGOING)
    private List<Genre> genres;


}
