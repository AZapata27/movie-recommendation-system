package dev.azapata.movierecommendationsystem;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Full application context backed by a real Neo4j started with Testcontainers.
 * Skipped automatically when no Docker daemon is available (e.g. a local machine
 * without Docker); it always runs in CI, where Docker is present.
 */
@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MovieRecommendationSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
