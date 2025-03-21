package dev.azapata.movierecommendationsystem.recomendation.model.nodes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue
    private Long ratingId;

    @Property
    private Integer ratingValue;

    @TargetNode
    private Movie movie;

}