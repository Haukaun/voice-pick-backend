package no.ntnu.bachelor.voicepick.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import no.ntnu.bachelor.voicepick.dtos.ProductDto;
import no.ntnu.bachelor.voicepick.features.authentication.exceptions.UnauthorizedException;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.mappers.ProductMapper;
import no.ntnu.bachelor.voicepick.models.Product;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddProductRequest;
import no.ntnu.bachelor.voicepick.dtos.UpdateProductRequest;
import no.ntnu.bachelor.voicepick.services.ProductService;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
  private final UserService userService;

  /**
   * Endpoint for adding a new product
   * 
   * @param request a request body containing information about the product and
   *                warehouse
   * @return {@code 200 OK} if added, {@code 404 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  @PostMapping
  @Operation(summary = "Add a new product")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Product created", content = @Content),
          @ApiResponse(responseCode = "401", description = "User not authorized", content = @Content),
          @ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content)
  })
  public ResponseEntity<String> addProduct(@RequestBody AddProductRequest request) {
    ResponseEntity<String> response;
    try {
      this.productService.addProduct(request, userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }
    return response;
  }

  /**
   * Endpoint for deleting a product
   *
   * @param id a path-variable containing the product id
   * @return {@code 200 OK} if removed, {@code 405 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a product")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
          @ApiResponse(responseCode = "401", description = "User not authorized", content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)

  })
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
    ResponseEntity<String> response;
    try {
      this.productService.deleteSpecificProduct(id, userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e){
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return response;
  }

  /**
   * Endpoint for getting all products
   * 
   * @return {@code 200 OK} if added, {@code 404 METHOD_NOT_ALLOWED} if request
   *         body is incorrect
   */
  @GetMapping
  @Operation(summary = "Get all products in an user's warehouse")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Success", content = {
                  @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))
          }),
          @ApiResponse(responseCode = "404", description = "The user's warehouse was not found", content = @Content),
          @ApiResponse(responseCode = "401", description = "User not authorized", content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<Object> getProducts() {
    ResponseEntity<Object> response;
    try {
      List<Product> activeProducts = this.productService.getAllAvailableProductsByWarehouse(this.userService.getCurrentUser().getWarehouse());
      response = new ResponseEntity<>(productMapper.toProductDto(activeProducts), HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (UnauthorizedException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    return response;
  }

  /**
   * Endpoint for updating a product
   * 
   * @param request body containing information about the product
   * @return {@code 200 Ok} if added, {@code 404 METHOD_NOT_ALLOWED} if body
   *         incorrect.
   */
  @PatchMapping("/{id}")
  @Operation(summary = "Updates a product")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Product updated", content = @Content),
          @ApiResponse(responseCode = "401", description = "User not authorized", content = @Content),
          @ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content)
  })
  @PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
  public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
    ResponseEntity<Object> response;
    try {
      var product = this.productService.updateProduct(id, request);
      response = new ResponseEntity<>(this.productMapper.toProductDto(product), HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      response = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return response;
  }

}
