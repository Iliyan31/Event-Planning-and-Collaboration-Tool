package bg.sofia.uni.fmi.web.project.service;

import bg.sofia.uni.fmi.web.project.dto.UserDto;
import bg.sofia.uni.fmi.web.project.model.User;
import bg.sofia.uni.fmi.web.project.repository.UserRepository;
import bg.sofia.uni.fmi.web.project.validation.ApiBadRequest;
import bg.sofia.uni.fmi.web.project.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(@NotNull(message = "The provided user cannot be null")
                           User userToSave) {

        for (User currentUser : userRepository.findByUsernameAndDeletedFalse(userToSave.getUsername())) {
            if (!currentUser.isDeleted()) {
                throw new ApiBadRequest("There is already a user associated with that credentials");
            }
        }

        for (User currentUser : userRepository.findByEmailAndDeletedFalse(userToSave.getEmail())) {
            if (!currentUser.isDeleted()) {
                throw new ApiBadRequest("There is already a user associated with that credentials");
            }
        }

        userToSave.setCreationTime(LocalDateTime.now());
        userToSave.setCreatedBy(userToSave.getUsername());
        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));

        return userRepository.save(userToSave);
    }

    public List<User> getUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    public Optional<User> getUserById(@NotNull(message = "The provided user id cannot be null")
                                      @Positive(message = "The provided user id must be positive")
                                      Long id) {

        Optional<User> potentialUserToReturn = userRepository.findByIdAndDeletedFalse(id);

        if (potentialUserToReturn.isPresent()) {
            return potentialUserToReturn;
        }

        throw new ResourceNotFoundException("User with such an id cannot be found");
    }

    public User getUserByEmail(@NotNull(message = "THe provided email cannot be null")
                                         @NotBlank(message = "The provided email cannot be empty or blank")
                                         String email) {
        List<User> potentialUsersToReturn = userRepository.findByEmailAndDeletedFalse(email);

        for (User currentUser : potentialUsersToReturn) {
            if (!currentUser.isDeleted()) {
                return currentUser;
            }
        }

        throw new ResourceNotFoundException("User with such an email cannot be found");
    }

    public User getUserByUsername(@NotNull(message = "The provided username cannot be null")
                                            @NotBlank(message = "The provided username cannot be empty or blank")
                                            String username) {
        List<User> potentialUsersToReturn = userRepository.findByUsernameAndDeletedFalse(username);


        for (User currentUser : potentialUsersToReturn) {
            if (!currentUser.isDeleted()) {
                return currentUser;
            }
        }

        throw new ResourceNotFoundException("User with such a username cannot be found");
    }

    public User getUserByUsernameAndPassword(@NotNull(message = "The provided username cannot be null")
                                                       @NotBlank(message = "The provided username cannot be empty or blank")
                                                       String username,
                                                       @NotNull(message = "The provided password cannot be null")
                                                       @NotBlank(message = "The provided password cannot be empty or blank")
                                                       String password) {

        List<User> potentialUsersToReturn = userRepository.findByUsernameAndDeletedFalse(username);

        for (User currentUser : potentialUsersToReturn) {
            if (!currentUser.isDeleted() && passwordEncoder.matches(password, currentUser.getPassword())) {
                return currentUser;
            }
        }

        throw new ResourceNotFoundException("User with such a username and password cannot be found");
    }

    public boolean setUsernameByProvidingOldUsernameAndPassword(@NotNull(message = "The provided new username cannot be null")
                                                                @NotBlank(message = "The provided new username cannot be empty or blank")
                                                                String newUserName,
                                                                @NotNull(message = "The provided old username cannot be null")
                                                                @NotBlank(message = "The provided old username cannot be empty or blank")
                                                                String oldUsername,
                                                                @NotNull(message = "The provided password cannot be null")
                                                                @NotBlank(message = "The provided password cannot be empty or blank")
                                                                String password,
                                                                @NotNull(message = "The user who made changes cannot be null")
                                                                User userToMakeChanges) {

        List<User> optionalUsersToChange = userRepository
            .findByUsernameAndDeletedFalse(oldUsername);

        for (User currentUser : optionalUsersToChange) {

            if (!currentUser.isDeleted() && passwordEncoder.matches(password, currentUser.getPassword())) {
                for (User user : userRepository.findByUsernameAndDeletedFalse(newUserName)) {
                    if (!user.isDeleted()) {
                        throw new ApiBadRequest("The new username is already taken");
                    }
                }

                currentUser.setUsername(newUserName);
                currentUser.setUpdatedBy(userToMakeChanges.getUsername());
                currentUser.setLastUpdatedTime(LocalDateTime.now());
                userRepository.save(currentUser);
                return true;
            }
        }

        throw new ResourceNotFoundException("User with such a username and password cannot be found");
    }

    public boolean setPasswordByProvidingUsernameAndOldPassword(@NotNull(message = "The provided new password cannot be null")
                                                                @NotBlank(message = "The provided new password cannot be empty or blank")
                                                                String newPassword,
                                                                @NotNull(message = "The provided username cannot be null")
                                                                @NotBlank(message = "The provided username cannot be empty or blank")
                                                                String username,
                                                                @NotNull(message = "The provided old password cannot be null")
                                                                @NotBlank(message = "The provided old password cannot be empty or blank")
                                                                String oldPassword,
                                                                @NotNull(message = "The user who makes changes cannot be null")
                                                                User userToMakeChanges) {

        List<User> optionalUsersToChange = userRepository.findByUsernameAndDeletedFalse(username);

        for (User currentUser : optionalUsersToChange) {

            if (!currentUser.isDeleted() && passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
                currentUser.setUpdatedBy(userToMakeChanges.getUsername());
                currentUser.setLastUpdatedTime(LocalDateTime.now());
                userRepository.save(currentUser);
                return true;
            }
        }

        throw new ResourceNotFoundException("User with such a username and password cannot be found");
    }

    private User setUserNonNullFields(
        @NotNull(message = "The provided user dto cannot be null")
        UserDto userFieldsToChange,
        @NotNull(message = "The provided user cannot be null")
        User userToUpdate) {

        if (userFieldsToChange.getName() != null) {
            userToUpdate.setName(userFieldsToChange.getName());
        }

        if (userFieldsToChange.getSurname() != null) {
            userToUpdate.setSurname(userFieldsToChange.getSurname());
        }

        if (userFieldsToChange.getEmail() != null) {
            userToUpdate.setEmail(userFieldsToChange.getEmail());
        }

        if (userFieldsToChange.getProfilePhotoLink() != null) {
            userToUpdate.setProfilePhotoLink(userFieldsToChange.getProfilePhotoLink());
        }

        if (userFieldsToChange.getAddress() != null) {
            userToUpdate.setAddress(userFieldsToChange.getAddress());
        }

        return userToUpdate;
    }

    public boolean setUserById(
        @NotNull(message = "The provided user dto cannot be null")
        UserDto userFieldsToChange,
        @NotNull(message = "The provided user id cannot be null")
        @Positive(message = "The provided user id must be positive")
        Long userId,
        @NotNull(message = "The user who makes changes cannot be null")
        User userToMakeChanges) {

        Optional<User> optionalUserToUpdate = userRepository.findById(userId);

        if (optionalUserToUpdate.isPresent() && !optionalUserToUpdate.get().isDeleted()) {

            User userToUpdate = setUserNonNullFields(userFieldsToChange, optionalUserToUpdate.get());;
            userToUpdate.setLastUpdatedTime(LocalDateTime.now());
            userToUpdate.setUpdatedBy(userToMakeChanges.getUsername());
            userRepository.save(userToUpdate);
            return true;
        }

        throw new ResourceNotFoundException("There is not a user with such an id");
    }

    public User deleteUser(
        @NotNull(message = "The provided username cannot be null")
        @NotBlank(message = "The provided username cannot be empty or blank")
        String username,
        @NotNull(message = "The provided password cannot be null")
        @NotBlank(message = "The provided password cannot be empty or blank")
        String password,
        @NotNull(message = "The user who makes changes cannot be null")
        User userToMakeChanges) {

        List<User> optionalUsersToDelete = userRepository.findByUsernameAndDeletedFalse(username);

        for (User currentUser : optionalUsersToDelete) {

            if (!currentUser.isDeleted() && passwordEncoder.matches(password, currentUser.getPassword())) {
                currentUser.setLastUpdatedTime(LocalDateTime.now());
                currentUser.setUpdatedBy(userToMakeChanges.getUsername());
                currentUser.setDeleted(true);
                userRepository.save(currentUser);
                return currentUser;
            }
        }

        throw new ResourceNotFoundException("User with such a username and password cannot be found");
    }
}