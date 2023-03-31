package no.ntnu.bachelor.voicepick.mappers;

import no.ntnu.bachelor.voicepick.dtos.ProductDto;
import no.ntnu.bachelor.voicepick.models.Product;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
  public abstract ProductDto toProductDto(Product product);
  public abstract Collection<ProductDto> toProductDto(Collection<Product> product);

}
