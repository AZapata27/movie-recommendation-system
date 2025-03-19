package dev.azapata.movierecommendationsystem.recomendation.service;


import dev.azapata.movierecommendationsystem.recomendation.dto.request.MovieRatingRequest;
import dev.azapata.movierecommendationsystem.recomendation.exception.BadRequestException;
import dev.azapata.movierecommendationsystem.recomendation.exception.NotFoundException;
import dev.azapata.movierecommendationsystem.recomendation.model.Movie;
import dev.azapata.movierecommendationsystem.recomendation.model.Rating;
import dev.azapata.movierecommendationsystem.recomendation.model.User;
import dev.azapata.movierecommendationsystem.recomendation.repository.MovieRepository;
import dev.azapata.movierecommendationsystem.recomendation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;


    public void rateMovie(MovieRatingRequest request) {

        if (request.ratingValue() < 1 || request.ratingValue() > 5) {
            throw new BadRequestException("Rating must be a value between 1 and 5.");
        }

        Optional<User> optionalUser = userRepository.findById(request.userName());
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found: " + request.userName());
        }


        Optional<Movie> optionalMovie = movieRepository.findByTitle(request.movieTitle());
        if (optionalMovie.isEmpty()) {
            throw new NotFoundException("Movie not found: " + request.movieTitle());
        }

        User user = optionalUser.get();
        Movie movie = optionalMovie.get();

        Optional<Rating> existingRating = user.getRatings().stream()
                .filter(r -> r.getMovie().getTitle().equals(movie.getTitle()))
                .findFirst();

        if (existingRating.isPresent()) {
            Rating ratingToUpdate = existingRating.get();
            ratingToUpdate.setRatingValue(request.ratingValue());
        } else {
            Rating newRating = new Rating();
            newRating.setMovie(movie);
            newRating.setRatingValue(request.ratingValue());
            user.getRatings().add(newRating);
        }

        userRepository.save(user);
    }
}
