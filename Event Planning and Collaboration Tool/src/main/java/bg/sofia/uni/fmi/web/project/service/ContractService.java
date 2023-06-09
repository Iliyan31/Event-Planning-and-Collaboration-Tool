package bg.sofia.uni.fmi.web.project.service;

import bg.sofia.uni.fmi.web.project.dto.ContractDto;
import bg.sofia.uni.fmi.web.project.model.Contract;
import bg.sofia.uni.fmi.web.project.repository.ContractRepository;
import bg.sofia.uni.fmi.web.project.validation.ResourceNotFoundException;
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
public class ContractService {
    private final ContractRepository contractRepository;

    public long addContract(@NotNull(message = "The given vendor cannot be null!")
                            Contract contractToSave) {
        return contractRepository.save(contractToSave).getId();
    }

    public List<Contract> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        validateContractsList(contracts);
        return contracts;
    }

    public Contract getContractById(@Positive(message = "The given id cannot be 0 or less!") long id) {
        Contract contract = contractRepository.findContractByIdEquals(id);
        validateContract(contract);

        return contract;
    }

    public Contract getContractByEventIdAndVendorId(long eventId, long vendorId) {
        return contractRepository.findContractsByAssociatedEventIdEqualsAndAssociatedVendorIdEquals(eventId, vendorId);
    }

    public List<Contract> getContractsByFinished(boolean finished) {
        List<Contract> contracts =
            contractRepository.findContractsByFinishedEquals(finished);

        validateContractsList(contracts);
        return contracts;
    }

    public List<Contract> getContractsByAssociatedEventId(@Positive(message = "The given event id must be above 0!")
                                                          long eventId) {
        List<Contract> contracts =
            contractRepository.findContractsByAssociatedEventIdEquals(eventId);

        validateContractsList(contracts);
        return contracts;
    }

    public List<Contract> getContractsByAssociatedVendorId(@Positive(message = "The given vendor id must be above 0!")
                                                           long vendorId) {
        List<Contract> contracts = contractRepository.findContractsAssociatedVendorIdEquals(vendorId);
        validateContractsList(contracts);

        return contracts;
    }

    public boolean setContractByContractId(@Positive(message = "The contract id must be positive!")
                                           long contractId,
                                           @NotNull(message = "The given contract dto cannot be null!")
                                           ContractDto contractToUpdateDto) {
        Contract contract = getContractById(contractId);
        validateContract(contract);

        Contract newContractToSave = updateFields(contractToUpdateDto, contract);
        newContractToSave.setUpdatedBy("b");
        newContractToSave.setLastUpdatedTime(LocalDateTime.now());

        contractRepository.save(newContractToSave);

        return true;
    }

    public boolean delete(@Positive(message = "The given ID cannot be less than zero!") long contractId) {
        Contract contract = getContractById(contractId);
        validateContract(contract);

        contract.setDeleted(true);
        contractRepository.save(contract);
        return true;
    }

    private Contract updateFields(ContractDto contractToUpdateDto, Contract newContractToSave) {
        if (contractToUpdateDto.getTotalPrice() != null &&
            !contractToUpdateDto.getTotalPrice().equals(newContractToSave.getTotalPrice())) {

            newContractToSave.setTotalPrice(contractToUpdateDto.getTotalPrice());
        }
        if (contractToUpdateDto.isFinished() != newContractToSave.isFinished()) {
            newContractToSave.setFinished(contractToUpdateDto.isFinished());
        }

        return newContractToSave;
    }

    private void validateContract(Contract contract) {
        if (contract == null) {
            throw new ResourceNotFoundException("There is no such contract in the database!");
        }
    }

    private void validateContractsList(List<Contract> contracts) {
        if (contracts == null) {
            throw new ResourceNotFoundException("There are no such contracts in the database or have been deleted!");
        }
    }
}