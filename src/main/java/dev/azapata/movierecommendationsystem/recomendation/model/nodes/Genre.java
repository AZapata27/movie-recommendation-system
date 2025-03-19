package dev.azapata.movierecommendationsystem.recomendation.model.nodes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@AllArgsConstructor
@Getter
public class Genre {

    @Id
    private String name;

}
