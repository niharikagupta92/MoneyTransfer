package com.revolut.moneytransfer.repo;

import com.revolut.moneytransfer.protocols.transfer.Transfer;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransferRepositoryMock extends TransferRepository {

    private Map<String, Transfer> transferStore;

    public TransferRepositoryMock() {
        this.transferStore = new HashMap<>();
    }

    @Override
    public Transfer saveTransaction(Transfer transfer) {
        transferStore.put(transfer.getTransferId(), transfer);
        return transfer;
    }

    @Override
    public Transfer getTransactionById(String transferId) {
        return transferStore.getOrDefault(transferId, null);
    }

    @Override
    public List<Transfer> getTransactionByStatus(TransferStatus transferStatus, int transferThrottle) {
        return transferStore.values()
                .stream()
                .filter(transfer -> transfer.getStatus() == TransferStatus.Pending)
                .sorted(Comparator.comparingLong(o -> o.getCreateTimestamp().toInstant().getEpochSecond()))
                .collect(Collectors.toList())
                .subList(0, transferThrottle);
    }

    @Override
    public void updateTransferStatus(String transferId, TransferStatus status) {
        Transfer transfer = transferStore.get(transferId);
        transfer.setStatus(status);
        transferStore.put(transferId, transfer);
    }

    @Override
    public List<Transfer> getTransferHistoryByAccountId(String accountId) {
        return transferStore.values().stream()
                .filter(transfer -> transfer.getReceiverAccount().equals(accountId) || transfer.getSenderAccount().equals(accountId))
                .collect(Collectors.toList());
    }
}
