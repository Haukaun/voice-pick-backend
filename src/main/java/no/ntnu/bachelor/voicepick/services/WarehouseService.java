package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.VerificationCodeInfo;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.repositories.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
public class WarehouseService {

  private final EmailSender emailSender;
  private final WarehouseRepository warehouseRepository;
  private final UserService userService;

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
      Email email = new Email(optionalWarehouse.get().getId(), recipient);
      Future<String> futureResult = emailSender.sendMail(email);
      return emailSender.getResultFromFuture(futureResult);
    }
    return new ResponseEntity<>("No available warehouse for the requesting user", HttpStatus.NOT_FOUND);
  }

  /**
   * Join a warehouse using the verification code found in email.
   * @param verificationCodeInfo the verificationCodeInfo to join with.
   * @param user the user that should join the warehouse.
   * @throws EntityNotFoundException if it doesn't find the joincode or the warehouse in the db.
   */
  public Warehouse joinWarehouse(VerificationCodeInfo verificationCodeInfo, User user) {
    var warehouseId = Email.containsJoinCode(verificationCodeInfo);
    if (warehouseId == null) {
      throw new EntityNotFoundException("Verification code (" + verificationCodeInfo.getVerificationCode() + ") does not exist.");
    }
    var optionalWarehouse = this.findWarehouseById(warehouseId);
    Warehouse warehouse;
    if (optionalWarehouse.isPresent()) {
      warehouse = optionalWarehouse.get();
      warehouse.addUser(user);
      warehouseRepository.save(warehouse);
    } else {
      throw new EntityNotFoundException("Warehouse with id (" + warehouseId + ") does not exist.");
    }
    return warehouse;
  }

  /**
   * Creates a warehouse and adds the user creating the warehouse to it.
   * @param user the user who creates the warehouse
   * @param dto name and address of the warehouse to be added
   */
  public Warehouse createWarehouse(User user, AddWarehouseDto dto) {
    Warehouse warehouse = new Warehouse(dto.getName(), dto.getAddress());
    warehouse.addUser(user);
    warehouseRepository.save(warehouse);
    return user.getWarehouse();
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

  public void removeUserFromWarehouse(Warehouse warehouse, String userId) {
    Optional<User> optionalUserToRemove = userService.getUserByUuid(userId);
    if (optionalUserToRemove.isEmpty()) {
      throw new EntityNotFoundException("User with userId: ( " + userId + " ) could not be removed because it does not exist.");
    }
    warehouse.removeUser(optionalUserToRemove.get());
    warehouseRepository.save(warehouse);
  }

  /**
   * Returns a set of the users in a given warehouse
   * @param warehouse the warehouse to get users from
   * @return set of the users, or empty set if there are no users in the warehouse
   * @throws IllegalArgumentException if the warehouse of the current user is null.
   */
  public Set<User> findAllUsersInWarehouse(Warehouse warehouse) {
    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse if the current user is null.");
    }
    return warehouse.getUsers();
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
