package dev.azapata.movierecommendationsystem.recomendation.controller;

import dev.azapata.movierecommendationsystem.recomendation.dto.request.UserRequest;
import dev.azapata.movierecommendationsystem.recomendation.model.User;
import dev.azapata.movierecommendationsystem.recomendation.repository.UserRepository;
import dev.azapata.movierecommendationsystem.recomendation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequest request) {

        userService.createUser(request);
    }

}