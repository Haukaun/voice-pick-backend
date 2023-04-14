package no.ntnu.bachelor.voicepick.features.pluck.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.ntnu.bachelor.voicepick.models.Product;

/**
 * An entity that represnets a pluck
 * 
 * @author Joakim
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = Pluck.TABLE_NAME)
public class Pluck {

  public static final String TABLE_NAME = "plucks";
  public static final String PRIMARY_KEY = "pluck_id";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = Pluck.PRIMARY_KEY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = Product.PRIMARY_KEY)
  private Product product;

  @Column(name = "amount")
  private int amount;

  @Column(name = "amountPlucked")
  private int amountPlucked;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "plucked_at")
  private LocalDateTime pluckedAt;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = PluckList.PRIMARY_KEY)
  private PluckList pluckList;

  public Pluck(Product product, int amount, LocalDateTime createdAt) {
    this.product = product;
    this.amount = amount;
    this.amountPlucked = 0;
    this.createdAt = createdAt;
  }
}
