package dev.azapata.movierecommendationsystem.recomendation.controller;

import dev.azapata.movierecommendationsystem.recomendation.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static dev.azapata.movierecommendationsystem.recomendation.TestData.movie;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    void returnsPersonalizedRecommendationsInRankedOrder() throws Exception {
        when(recommendationService.recommendMoviesByUserName("alice"))
                .thenReturn(List.of(movie("Interstellar"), movie("Tenet")));

        mockMvc.perform(get("/api/v1/recommendations/{userName}", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Interstellar"))
                .andExpect(jsonPath("$[0].genres[0].name").value("Sci-Fi"))
                .andExpect(jsonPath("$[1].title").value("Tenet"));
    }

    @Test
    void returnsBasicRecommendations() throws Exception {
        when(recommendationService.basicRecommendationByUserName("alice")).thenReturn(List.of(movie("Inception")));

        mockMvc.perform(get("/api/v1/recommendations/basic/{userName}", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Inception"));
    }

    @Test
    void returnsEmptyArrayWhenNoRecommendations() throws Exception {
        when(recommendationService.recommendMoviesByUserName("newcomer")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/recommendations/{userName}", "newcomer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
