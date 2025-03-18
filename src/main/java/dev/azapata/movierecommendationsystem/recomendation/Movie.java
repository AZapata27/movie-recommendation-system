package dev.azapata.movierecommendationsystem.recomendation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;


@Node
@AllArgsConstructor
@Getter
public class Movie {

    @Id
    private String title;

    @Property("released")
    private Integer released;

    @Relationship(type = "DIRECTED_BY", direction = Relationship.Direction.INCOMING)
    private Person director;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List<Person> actors;

    @Relationship(type = "HAS_GENRE", direction = Relationship.Direction.OUTGOING)
    private List<Genre> genres;

}
