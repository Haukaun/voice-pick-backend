package no.ntnu.bachelor.voicepick.services;

import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.repositories.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
public class WarehouseService {

  private final EmailSender emailSender;
  private final WarehouseRepository warehouseRepository;

  /**
   * Sends an invitation email with a join code to the specified recipient
   * @param inviter the user which invites the recipient
   * @param recipient the user who is invited to the warehouse
   * @return 200 OK if valid, 404 if the inviter is not in a warehouse, or proper error
   * if the email cannot be sent.
   */
  public ResponseEntity<String> inviteToWarehouse(User inviter, EmailDto recipient) {
    Optional<Warehouse> optionalWarehouse = warehouseRepository.findWarehouseByUsersContaining(inviter);
    if (optionalWarehouse.isPresent()) {
      Email email = new Email(recipient, Email.Subject.INVITE_CODE);
      Future<String> futureResult = emailSender.sendMail(email);
      return emailSender.getResultFromFuture(futureResult);
    }
    return new ResponseEntity<>("No available warehouse for the requesting user", HttpStatus.NOT_FOUND);
  }

  /**
   * Creates a warehouse and adds the user creating the warehouse to it.
   * @param user the user who creates the warehouse
   * @param dto name and address of the warehouse to be added
   * @return the warehouse that was created
   */
  public Warehouse createWarehouse(User user, AddWarehouseDto dto) {
    Warehouse warehouse = new Warehouse(dto.getName(), dto.getAddress());
    warehouse.addUser(user);
    warehouseRepository.save(warehouse);
    return warehouse;
  }

  /**
   * Finds the warehouse the user belongs to.
   * @param user who's warehouse we find.
   * @return optional with the users warehouse or empty optional if not in any warehouse.
   */
  public Optional<Warehouse> findWarehouseByUser(User user) {
    return warehouseRepository.findWarehouseByUsersContaining(user);
  }

  /**
   * Finds a warehouse by the given id.
   * @param id the id of the warehouse to find.
   * @return optional warehouse or empty if there is none by the given id.
   */
  public Optional<Warehouse> findWarehouseById(Long id) {
    return warehouseRepository.findById(id);
  }

  /**
   * Finds a warehouse by the given name.
   * @param name name of the warehouse to find.
   * @return optional warehouse or empty if there is none by the given name.
   */
  public Optional<Warehouse> findByName(String name) {
    return this.warehouseRepository.findByName(name);
  }

  /**
   * Deletes a warehouse after clearing its belonging properties.
   * @param warehouse warehouse to delete.
   */
  public void deleteWarehouse(Warehouse warehouse) {
    warehouse.clear();
    this.warehouseRepository.delete(warehouse);
  }

  /**
   * Clears and deletes all warehouses.
   */
  public void deleteAll() {
    this.warehouseRepository.findAll().forEach(this::deleteWarehouse);
  }

}
