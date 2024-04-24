package com.exbanking.yolo.test.datarepo;

import com.exbanking.yolo.test.AccountDetails;
import com.exbanking.yolo.test.errormessage.ErrorMessage;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DatabaseRepoExBanking {
    private static final Map<Integer, AccountDetails> accountDetailsMap = new HashMap<>();

    private static int createAccountNumber(){
        Random rand = new Random();
        return rand.nextInt(900000) + 100000;
    }

    public static AccountDetails createAccountDetails(String personalId) {
        AccountDetails accountDetails = AccountDetails.newBuilder()
                .setAccountId(createAccountNumber())
                .setBalance(0)
                .build();
        accountDetailsMap.put(accountDetails.getAccountId(), accountDetails);
        return accountDetails;
    }

    public AccountDetails getAccountDetails(int accountId) {
        return accountDetailsMap.get(accountId);
    }

    public AccountDetails updateAccountDetails(AccountDetails accountDetails) {
        if (accountDetailsMap.containsKey(accountDetails.getAccountId())) {
            return accountDetailsMap.put(accountDetails.getAccountId(), accountDetails);
        }
        throw new StatusRuntimeException(Status.NOT_FOUND.withDescription(ErrorMessage.INVALID_ACCOUNT_NUMBER));
    }
}
