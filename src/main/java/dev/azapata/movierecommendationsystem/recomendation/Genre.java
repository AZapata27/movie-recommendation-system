package dev.azapata.movierecommendationsystem.recomendation;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Genre")
public class Genre {

    @Id
    private String id;

    @Property("name")
    private String name;

}
