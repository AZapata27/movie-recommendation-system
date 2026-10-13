package dev.azapata.movierecommendationsystem;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(MovieRecommendationSystemApplication.class);

    @Test
    void verifiesModuleStructure() {
        modules.verify();
    }
}
