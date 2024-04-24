package com.exbanking.yolo.test.validation;

import com.exbanking.yolo.test.*;
import com.exbanking.yolo.test.errormessage.ErrorMessage;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class ExBankingValidation {

    public void createUserValidation(CreateUserRequest createUserRequest){
        if (createUserRequest == null){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.NULL_REQUEST), new Metadata());
        }

        UserDetails userDetails = createUserRequest.getUserDetails();

        if (userDetails.getName().trim().isEmpty()){
            throw new StatusRuntimeException(io.grpc.Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_NAME), new Metadata());
        }

        if (userDetails.getPersonalId().trim().isEmpty()){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_PERSONAL_ID), new Metadata());
        }

        if (userDetails.getEmail().trim().isEmpty()){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_EMAIL), new Metadata());
        }

        if (userDetails.getPassword().trim().isEmpty()){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_PASSWORD), new Metadata());
        }
    }

    public void withdrawRequestValidation(WithdrawRequest withdrawRequest){
        if (withdrawRequest == null){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.NULL_REQUEST), new Metadata());
        }
        if (withdrawRequest.getAccountId() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_ACCOUNT_NUMBER), new Metadata());
        }
        if (withdrawRequest.getAmount() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_AMOUNT), new Metadata());
        }
        if (withdrawRequest.getAmount() < 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INVALID_AMOUNT), new Metadata());
        }
    }

    public void sendRequestValidation(SendRequest sendRequest){
        if (sendRequest == null){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.NULL_REQUEST), new Metadata());
        }
        if (sendRequest.getFromAccountId() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INVALID_FROM_ACCOUNT_NUMBER), new Metadata());
        }
        if (sendRequest.getToAccountId() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INVALID_TO_ACCOUNT_NUMBER), new Metadata());
        }
        if (sendRequest.getAmount() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_AMOUNT), new Metadata());
        }
        if (sendRequest.getAmount() < 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INVALID_AMOUNT), new Metadata());
        }
        if (sendRequest.getPassword().trim().isEmpty()){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_PASSWORD), new Metadata());
        }
    }

    public void depositRequestValidation(DepositRequest depositRequest){
        if (depositRequest == null){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.NULL_REQUEST), new Metadata());
        }
        if (depositRequest.getAccountId() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_ACCOUNT_NUMBER), new Metadata());
        }
        if (depositRequest.getAmount() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_AMOUNT), new Metadata());
        }
        if (depositRequest.getAmount() < 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INVALID_AMOUNT), new Metadata());
        }
    }

    public void getBalanceRequestValidation(GetBalanceRequest getBalanceRequest){
        if (getBalanceRequest == null){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.NULL_REQUEST), new Metadata());
        }
        if (getBalanceRequest.getAccountId() == 0){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.EMPTY_ACCOUNT_NUMBER), new Metadata());
        }
    }

    public void withdrawAccountValidation(AccountDetails accountDetails, int amount){
        checkAccountExistence(accountDetails, ErrorMessage.INVALID_ACCOUNT_NUMBER);
        checkBalance(accountDetails.getBalance(), amount);
    }

    public void depositAccountValidation(AccountDetails accountDetails){
        checkAccountExistence(accountDetails, ErrorMessage.INVALID_ACCOUNT_NUMBER);
    }

    public void checkAccountExistence(AccountDetails accountDetails, String description){
        if (accountValidation(accountDetails)){
            throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(description), new Metadata());
        }
    }

    private void checkBalance(int balance, int amount){
        if (balanceCheck(balance, amount)){
            throw new StatusRuntimeException((Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INSUFFICIENT_FUNDS)), new Metadata());
        }
    }

    private boolean accountValidation(AccountDetails accountDetails){
        return accountDetails != null;
    }

    private boolean balanceCheck(int balance, int amount){
        return balance < amount;
    }

}

