package no.ntnu.bachelor.voicepick.controllers;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.services.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;

  private final UserService userService;

  /**
   * Endpoint for adding a new product
   * 
   * @param request a request body containing information about the product and warehouse
   * @return {@code 200 OK} if added, {@code 404 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @PostMapping
  public ResponseEntity<String> addProduct(@RequestBody AddProductRequest request) {
    ResponseEntity<String> response;
    try {
      this.service.addProduct(request, userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    return response;
  }

}
