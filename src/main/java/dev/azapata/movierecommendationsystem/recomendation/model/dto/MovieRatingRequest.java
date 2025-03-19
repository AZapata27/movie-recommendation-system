package dev.azapata.movierecommendationsystem.recomendation.model.dto;

public record MovieRatingRequest(
            String userName,
            String movieTitle,
            int ratingValue
    ) { }