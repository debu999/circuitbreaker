package org.doogle.circuitbreaker;// Import the necessary dependencies

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

// Use MockitoExtension to enable mock annotations
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
public class CircuitBreakerControllerTest {

  // Mock the WebClient bean
  @Autowired
  protected CircuitBreakerRegistry registry;

  // Create a CircuitBreakerController instance
  @Autowired
  private APIController apiController;

  // Define a test case for the getCountries method
  @Test
  public void testGetCountries() throws InterruptedException {
    CircuitBreaker circuitBreaker = registry.circuitBreaker("countries");

    // Call the getCountries method and verify the result
    StepVerifier.create(apiController.getCountries())
        .expectNext("INDIA") // Expect the result to be "INDIA"
        .verifyComplete(); // Verify the completion of the Mono

    StepVerifier.create(apiController.toggleOnOff())
        .expectNext("OFF") // Expect the result to be "INDIA"
        .verifyComplete(); // Verify the completion of the Mono

    IntStream.rangeClosed(0, 10).forEach(i ->
        StepVerifier.create(apiController.getCountries())
            .expectNext("ERROR") // Expect the result to be "INDIA"
            .verifyComplete() // Verify the completion of the Mono
    );
    StepVerifier.create(apiController.toggleOnOff())
        .expectNext("ON") // Expect the result to be "INDIA"
        .verifyComplete(); // Verify the completion of the Mono
    IntStream.rangeClosed(0, 10).forEach(i ->
        StepVerifier.create(apiController.getCountries())
            .expectNext("ERROR") // Expect the result to be "INDIA"
            .verifyComplete() // Verify the completion of the Mono
    );
    Thread.sleep(5000);
    StepVerifier.create(apiController.getCountries())
        .expectNext("INDIA") // Expect the result to be "INDIA"
        .verifyComplete(); // Verify the completion of the Mono
  }
}