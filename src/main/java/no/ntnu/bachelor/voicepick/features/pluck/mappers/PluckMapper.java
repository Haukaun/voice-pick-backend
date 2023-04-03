package no.ntnu.bachelor.voicepick.features.pluck.mappers;

import no.ntnu.bachelor.voicepick.dtos.ProductDto;
import no.ntnu.bachelor.voicepick.features.pluck.dtos.PluckDto;
import no.ntnu.bachelor.voicepick.features.pluck.models.Pluck;
import no.ntnu.bachelor.voicepick.mappers.ProductMapper;
import no.ntnu.bachelor.voicepick.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class PluckMapper {

  private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

  @Mapping(target = "product", source = "product", qualifiedByName = "productToProductDto")
  public abstract PluckDto toPluckDto(Pluck pluck);

  public abstract Set<PluckDto> toPluckDto(Set<Pluck> plucks);

  @Named("productToProductDto")
  public ProductDto productToProductDto(Product product) {
    return productMapper.toProductDto(product);
  }

}
