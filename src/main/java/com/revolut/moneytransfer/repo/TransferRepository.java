package com.revolut.moneytransfer.repo;

import com.revolut.moneytransfer.constants.DBConstants;
import com.revolut.moneytransfer.db.H2DataUtil;
import com.revolut.moneytransfer.exception.TransferError;
import com.revolut.moneytransfer.exception.TransferException;
import com.revolut.moneytransfer.protocols.currency.Currency;
import com.revolut.moneytransfer.protocols.transfer.Transfer;
import com.revolut.moneytransfer.protocols.transfer.TransferStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransferRepository {

    private final Logger logger = LoggerFactory.getLogger(TransferRepository.class);

    private H2DataUtil db;

    public TransferRepository() {

        db = H2DataUtil.getInstance();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DBConstants.TRANSFER_TABLE_NAME +
                " (senderAccount VARCHAR(100), receiverAccount VARCHAR(100), amount DECIMAL(18,2), " +
                "currency VARCHAR(10), createTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "transferId VARCHAR(100), status VARCHAR(10))";

        logger.info("Creating Transfers table " + createTableQuery);
        int response = db.insertUpdateQuery(createTableQuery);
        if (response != 0) {
            System.exit(1);
        }
    }

    public Transfer saveTransaction(Transfer transfer) {
        String saveQuery = "insert into " + DBConstants.TRANSFER_TABLE_NAME + " (" +
                "senderAccount, " +
                "receiverAccount, " +
                "amount, " +
                "currency, " +
                "transferId, " +
                "status) values ('" +
                transfer.getSenderAccount() + "', '" +
                transfer.getReceiverAccount() + "', " +
                transfer.getAmount() + ", '" +
                transfer.getCurrency().getPath() + "', '" +
                transfer.getTransferId() + "', '" +
                transfer.getStatus().getPath() + "')";

        logger.info("Saving Transfer to table : " + saveQuery);
        int response = db.insertUpdateQuery(saveQuery);
        logger.info("Query response: " + response);

        if (response != 1) {
            throw new TransferException(TransferError.TRANSFER_CREATION_FAILED);
        }
        return transfer;
    }

    public Transfer getTransactionById(String transferId) {

        String getQuery = "select senderAccount, " +
                "receiverAccount, " +
                "amount, " +
                "currency, " +
                "createTimestamp, " +
                "transferId, " +
                "status " +
                "from " + DBConstants.TRANSFER_TABLE_NAME + " " +
                "where transferId = '" + transferId + "'";

        logger.info("Getting transfer from table for transferId " + transferId + " : " + getQuery);
        ArrayList<HashMap> rs = db.selectQuery(getQuery);
        List<Transfer> transfers = extractTransfer(rs);
        logger.info("Number of transfers received for transferId " + transferId + ": " + transfers.size());
        return transfers.isEmpty() ? null : transfers.get(0);
    }

    public List<Transfer> getTransactionByStatus(TransferStatus transferStatus, int transferThrottle) {
        String getQuery = "select senderAccount, " +
                "receiverAccount, " +
                "amount, " +
                "currency, " +
                "createTimestamp, " +
                "transferId, " +
                "status " +
                "from " + DBConstants.TRANSFER_TABLE_NAME + " " +
                "where status = '" + transferStatus.getPath() + "' " +
                "order by createTimestamp limit " + transferThrottle;

        logger.info("Getting transfers from table for status " + transferStatus.getPath() + " : " + getQuery);
        ArrayList<HashMap> rs = db.selectQuery(getQuery);
        List<Transfer> transfers = extractTransfer(rs);
        logger.info("Number of transfers received for status " + transferStatus.getPath() + ": " + transfers.size());
        return transfers;
    }

    public void updateTransferStatus(String transferId, TransferStatus status) {
        String updateQuery = "UPDATE " + DBConstants.TRANSFER_TABLE_NAME + " " +
                "SET status = '" + status.getPath() + "' " +
                "WHERE transferId = '" + transferId + "'";

        logger.info("Updating transfer with transferId " + transferId + ": " + updateQuery);
        int modifiedRows = db.insertUpdateQuery(updateQuery);
        logger.info("Number of rows affected by update query for transferId " + transferId + ": " + modifiedRows);
    }

    public List<Transfer> getTransferHistoryByAccountId(String accountId) {
        String getQuery = "select senderAccount, " +
                "receiverAccount, " +
                "amount, " +
                "currency, " +
                "createTimestamp, " +
                "transferId, " +
                "status " +
                "from " + DBConstants.TRANSFER_TABLE_NAME + " " +
                "where senderAccount = '" + accountId + "' or receiverAccount = '" + accountId + "' " +
                "order by createTimestamp";

        logger.info("Getting transfers for account " + accountId + ": " + getQuery);
        ArrayList<HashMap> rs = db.selectQuery(getQuery);
        List<Transfer> transfers = extractTransfer(rs);
        logger.info("Number of transfers received for accountId " + accountId + ": " + transfers.size());
        return transfers;
    }

    private static ZonedDateTime getDateTime(Timestamp timestamp) {
        return timestamp != null ? ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp.getTime()), ZoneOffset.UTC) : null;
    }

    private List<Transfer> extractTransfer(ArrayList<HashMap> rs) {

        List<Transfer> transfers = new ArrayList<>();
        for (HashMap r : rs) {
            Transfer transfer = new Transfer();
            transfer.setSenderAccount((String) r.get("SENDERACCOUNT"));
            transfer.setReceiverAccount((String) r.get("RECEIVERACCOUNT"));
            transfer.setAmount(((BigDecimal) r.get("AMOUNT")).doubleValue());
            transfer.setCurrency(Currency.valueOf((String) r.get("CURRENCY")));
            transfer.setCreateTimestamp(getDateTime((Timestamp) r.get("CREATETIMESTAMP")));
            transfer.setTransferId((String) r.get("TRANSFERID"));
            transfer.setStatus(TransferStatus.valueOf((String) r.get("STATUS")));
            logger.info("Transfer result row from table : " + transfer.toString());
            transfers.add(transfer);
        }
        return transfers;
    }
}
