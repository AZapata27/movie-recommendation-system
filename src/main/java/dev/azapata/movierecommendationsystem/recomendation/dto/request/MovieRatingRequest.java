package dev.azapata.movierecommendationsystem.recomendation.dto.request;

public record MovieRatingRequest(
            String userName,
            String movieTitle,
            int ratingValue
    ) { }