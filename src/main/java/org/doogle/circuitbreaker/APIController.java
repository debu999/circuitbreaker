package org.doogle.circuitbreaker;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "http://localhost:9090")
public class APIController {

  public static String ON = "ON";

  @GetMapping()
  public Mono<List<String>> getData() {
    return Mono.just(List.of("a", "b", "c"));
  }

  @PutMapping()
  public Mono<String> toggleOnOff() {
    ON = "ON".equals(ON) ? "OFF" : "ON";
    return Mono.just(ON);
  }

  // Define a method that calls an external API and returns a response
  // Annotate the method with @CircuitBreaker and specify the name and fallback method
  @GetMapping("/countries")
  @CircuitBreaker(name = "countries", fallbackMethod = "fallback")
  @SneakyThrows
  public Mono<String> getCountries() {
    if ("ON".equals(ON)) {
      return Mono.just("INDIA");
    } else {
      return Mono.error(new Exception("Testing Circuit Breaker..."));
    }
  }

  // Define a fallback method that returns a String with message "ERROR"
  // The fallback method must have the same parameters as the original method, plus a Throwable parameter
  public Mono<String> fallback(Throwable e) {
    // Return the error message
    return Mono.just("ERROR");
  }

}
