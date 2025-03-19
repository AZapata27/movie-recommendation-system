package dev.azapata.movierecommendationsystem.recomendation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;


@Node
@Getter
@AllArgsConstructor
public class Movie {

    @Id
    private String title;

    @Property("released")
    private Integer released;

    @Relationship(type = "DIRECTED", direction = Relationship.Direction.INCOMING)
    private List<Person> directors;

    @Relationship(type = "ACTED_IN", direction = Relationship.Direction.INCOMING)
    private List<Person> actors;

    @Relationship(type = "HAS_GENRE")
    private List<Genre> genres;

}
