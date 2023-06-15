package bg.sofia.uni.fmi.web.project.service;

import bg.sofia.uni.fmi.web.project.enums.VendorType;
import bg.sofia.uni.fmi.web.project.model.Review;
import bg.sofia.uni.fmi.web.project.model.Vendor;
import bg.sofia.uni.fmi.web.project.repository.VendorRepository;
import bg.sofia.uni.fmi.web.project.validation.MethodNotAllowed;
import bg.sofia.uni.fmi.web.project.validation.ResourceNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@AllArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;

    public long addVendor(@NotNull(message = "The given vendor cannot be null!")
                          Vendor vendorToSave,
                          @NotNull(message = "The vendor type cannot be null!")
                          @NotEmpty(message = "The vendor type cannot be empty!")
                          @NotBlank(message = "The vendor type cannot be blank!")
                          String vendorType) {
//        if (!validateForExistingGuestByNameAndSurname(guestToSave) || !validateForExistingGuestByEventId(guestToSave)) {
//            throw new ApiBadRequest("There is already a guest with the same credentials");
//        }

        VendorType newVendorType = VendorType.valueOf(vendorType.toUpperCase());
        vendorToSave.setVendorType(newVendorType);
        vendorToSave.setCreatedBy("a");
        vendorToSave.setCreationTime(LocalDateTime.now());
        vendorToSave.setDeleted(false);

        Vendor vendor = vendorRepository.save(vendorToSave);
        checkForSaveException(vendor);
        return vendor.getId();
    }

    public List<Vendor> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll().parallelStream()
            .filter(g -> !g.isDeleted())
            .toList();

        validateVendorsList(vendors);

        return vendors;
    }

    public Vendor getVendorById(@Positive(message = "The given id cannot be 0 or less!")
                                long id) {
        Vendor vendor = vendorRepository.findVendorByIdEquals(id);
        validateVendor(vendor);
        validateForDeletedVendor(vendor);

        return vendor;
    }

    public Vendor getVendorByPhoneNumber(@NotNull(message = "The given phone number cannot be null!")
                                         @NotEmpty(message = "The given phone number cannot be empty!")
                                         @NotBlank(message = "The given phone number cannot be blank!")
                                         String phoneNumber) {
        Vendor vendor = vendorRepository.findVendorByPhoneNumberEquals(phoneNumber);
        validateVendor(vendor);
        validateForDeletedVendor(vendor);

        return vendor;
    }

    public Vendor getVendorByEmail(@NotNull(message = "The given email cannot be null!")
                                   @NotEmpty(message = "The given email cannot be empty!")
                                   @NotBlank(message = "The given email cannot be blank!")
                                   String email) {
        Vendor vendor = vendorRepository.findVendorByEmailEquals(email);
        validateVendor(vendor);
        validateForDeletedVendor(vendor);

        return vendor;
    }

    public List<Vendor> getVendorsByVendorType(@NotNull(message = "The given vendor type cannot be null!")
                                               VendorType vendorType) {
        List<Vendor> vendors = vendorRepository.findVendorsByVendorTypeEquals(vendorType).parallelStream()
            .filter(g -> !g.isDeleted())
            .toList();

        validateVendorsList(vendors);

        return vendors;
    }

    public boolean delete(boolean deleted,
                          @Positive(message = "The given ID cannot be less than zero!")
                          long vendorId) {
        Vendor vendor = vendorRepository.findVendorByIdEquals(vendorId);
        validateVendor(vendor);
        validateForDeletedVendor(vendor);

        vendor.setDeleted(deleted);
        vendorRepository.save(vendor);
        return true;
    }

    private void validateVendor(Vendor vendor) {
        if (vendor == null) {
            throw new ResourceNotFoundException("There is no such vendor in the database!");
        }
    }

    private void validateVendorsList(List<Vendor> vendors) {
        if (vendors == null) {
            throw new ResourceNotFoundException("There are no such vendors in the database or have been deleted!");
        }
    }

    private void checkForSaveException(Vendor vendor) {
        if (vendor == null) {
            throw new RuntimeException("There was problem while saving the vendor in the database!");
        }
    }

    private void validateForDeletedVendor(Vendor vendor) {
        if (vendor.isDeleted()) {
            throw new MethodNotAllowed("The current record has already been deleted!");
        }
    }
}