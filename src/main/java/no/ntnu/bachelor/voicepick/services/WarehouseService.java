package no.ntnu.bachelor.voicepick.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import no.ntnu.bachelor.voicepick.dtos.AddWarehouseDto;
import no.ntnu.bachelor.voicepick.dtos.EmailDto;
import no.ntnu.bachelor.voicepick.exceptions.InvalidInviteCodeException;
import no.ntnu.bachelor.voicepick.features.authentication.dtos.VerificationCodeInfo;
import no.ntnu.bachelor.voicepick.features.authentication.models.User;
import no.ntnu.bachelor.voicepick.features.authentication.services.UserService;
import no.ntnu.bachelor.voicepick.features.smtp.models.Email;
import no.ntnu.bachelor.voicepick.features.smtp.services.EmailSender;
import no.ntnu.bachelor.voicepick.models.Warehouse;
import no.ntnu.bachelor.voicepick.pojos.WarehouseInviteCode;
import no.ntnu.bachelor.voicepick.repositories.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class WarehouseService {

  private final EmailSender emailSender;
  private final WarehouseRepository warehouseRepository;
  private final UserService userService;

  private final TokenStore<String, WarehouseInviteCode> inviteCodeStore = new TokenStore<>(8, 10);


  /**
   * Sends an invitation email with a join code to the specified recipient
   *
   * @param inviter the user which invites the recipient
   * @param recipient the user who is invited to the warehouse
   * @return 200 OK if valid, 404 if the inviter is not in a warehouse, or proper error
   * if the email cannot be sent.
   * @throws EntityNotFoundException if the user is not in a warehouse, or the user they are
   * inviting does not exist..
   */
  public ResponseEntity<String> inviteToWarehouse(User inviter, EmailDto recipient) {
    var optionalWarehouse = this.warehouseRepository.findWarehouseByUsersContaining(inviter);
    if (optionalWarehouse.isEmpty()) {
      throw new EntityNotFoundException("Could not find warehouse for user requesting to invite");
    }

    var optionalReceiver = this.userService.getUserByEmail(recipient.getEmail());
    if (optionalReceiver.isEmpty()) {
      throw new EntityNotFoundException("Could not find receiver with email: " + recipient.getEmail());
    }

    // Generate invite code and store it
    var uuid = optionalReceiver.get().getUuid();
    var warehouse = optionalWarehouse.get();
    var code = this.inviteCodeStore.generateCode();
    this.inviteCodeStore.addToken(uuid, new WarehouseInviteCode(warehouse.getId(), code));

    // Send email with generated code
    var email = new Email(recipient, Email.Subject.INVITE_CODE, code);
    var futureResult = emailSender.sendMail(email);

    return emailSender.getResultFromFuture(futureResult);
  }

  /**
   * Join a warehouse using the verification code found in email.
   * @param verificationCodeInfo the verificationCodeInfo to join with.
   * @param user the user that should join the warehouse.
   * @throws EntityNotFoundException if it doesn't find the joincode or the warehouse in the db.
   */
  public Warehouse joinWarehouse(VerificationCodeInfo verificationCodeInfo, User user) throws InvalidInviteCodeException {
    var uuid = user.getUuid();
    var code = verificationCodeInfo.getVerificationCode();

    // Check if invite code is correct
    if (this.inviteCodeStore.isValidToken(uuid, code)) {
      // If yes, add user to warehouse
      var warehouseId = this.inviteCodeStore.getToken(uuid).getWarehouseId();
      var optionalWarehouse = this.findWarehouseById(warehouseId);
      if (optionalWarehouse.isEmpty()) {
        throw new EntityNotFoundException("Could not find warehouse with id: " + warehouseId);
      }

      var warehouse = optionalWarehouse.get();
      warehouse.addUser(user);
      this.warehouseRepository.save(warehouse);

      // Delete token from store
      this.inviteCodeStore.removeToken(uuid);

      return warehouse;
    } else {
      throw new InvalidInviteCodeException("Code given is not valid");
    }
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
