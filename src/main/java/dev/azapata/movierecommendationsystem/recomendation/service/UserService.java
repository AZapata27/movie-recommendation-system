package dev.azapata.movierecommendationsystem.recomendation.service;

import dev.azapata.movierecommendationsystem.recomendation.model.dto.UserRequest;
import dev.azapata.movierecommendationsystem.recomendation.exception.ConflictException;
import dev.azapata.movierecommendationsystem.recomendation.model.nodes.User;
import dev.azapata.movierecommendationsystem.recomendation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserRequest request) {

        if (userRepository.existsById(request.name())) {
            throw new ConflictException("User already exists: " + request.name());
        }

        User user = new User(request.name());

        userRepository.save(user);

    }

}
