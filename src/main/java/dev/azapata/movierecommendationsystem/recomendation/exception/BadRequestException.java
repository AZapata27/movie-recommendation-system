package dev.azapata.movierecommendationsystem.recomendation.exception;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
