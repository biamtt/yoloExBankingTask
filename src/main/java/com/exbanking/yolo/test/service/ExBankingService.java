package com.exbanking.yolo.test.service;

import com.exbanking.yolo.test.*;
import com.exbanking.yolo.test.datarepo.DatabaseRepoExBanking;
import com.exbanking.yolo.test.errormessage.ErrorMessage;
import com.exbanking.yolo.test.validation.ExBankingValidation;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExBankingService extends ExBankingServiceGrpc.ExBankingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ExBankingService.class);
    private final ExBankingValidation exBankingValidation;
    private final DatabaseRepoExBanking databaseRepoExBanking;

    public ExBankingService(ExBankingValidation exBankingValidation, DatabaseRepoExBanking databaseRepoExBanking) {
        this.exBankingValidation = exBankingValidation;
        this.databaseRepoExBanking = databaseRepoExBanking;
    }

    @Override
    public void createUserService(CreateUserRequest createUserRequest, StreamObserver<CreateUserResponse> createUserResponseStreamObserver) {
        try {
            exBankingValidation.createUserValidation(createUserRequest);
            log.info("MockDatabaseService createUserService createUserRequest {}", createUserRequest);
            AccountDetails accountDetails = databaseRepoExBanking.createAccountDetails(createUserRequest.getUserDetails().getPersonalId());
            CreateUserResponse createUserResponse = CreateUserResponse
                    .newBuilder()
                    .setAccountDetails(accountDetails)
                    .build();
            log.info("MockDatabaseService createUserService createUserResponse {}", createUserResponse);

            createUserResponseStreamObserver.onNext(createUserResponse);
            createUserResponseStreamObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            createUserResponseStreamObserver.onError(e);
        }
    }


    @Override
    public void withdrawService(WithdrawRequest withdrawRequest, StreamObserver<WithdrawResponse> responseObserver) {
        try {
            //withdraw request validation
            exBankingValidation.withdrawRequestValidation(withdrawRequest);
            log.info("MockDatabaseService withdrawService withdrawRequest {}", withdrawRequest);

            //accountdetails validation
            AccountDetails accountDetails = databaseRepoExBanking.getAccountDetails(withdrawRequest.getAccountId());

            //account and balance validation
            exBankingValidation.withdrawAccountValidation(accountDetails, withdrawRequest.getAmount());

            //account balance update
            int newBalance = accountDetails.getBalance() - withdrawRequest.getAmount();
            AccountDetails updatedAccountDetails = AccountDetails.newBuilder()
                    .mergeFrom(accountDetails)
                    .setBalance(newBalance)
                    .build();
            databaseRepoExBanking.updateAccountDetails(updatedAccountDetails);

            WithdrawResponse withdrawResponse = WithdrawResponse.newBuilder()
                    .setAccountDetails(updatedAccountDetails)
                    .build();

            log.info("MockDatabaseService withdrawService withdrawResponse {}", withdrawResponse);
            responseObserver.onNext(withdrawResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(ErrorMessage.INTERNAL_ERROR).withCause(e).asRuntimeException());
        }
    }


    @Override
    public void sendService(SendRequest sendRequest, StreamObserver<SendResponse> responseObserver) {
        try {
            //send request validation
            exBankingValidation.sendRequestValidation(sendRequest);
            log.info("MockDatabaseService sendService sendRequest {}", sendRequest);

            //get the sender account details
            AccountDetails fromAccountDetails = databaseRepoExBanking.getAccountDetails(sendRequest.getFromAccountId());

            //get the receiver account details
            AccountDetails toAccountDetails = databaseRepoExBanking.getAccountDetails(sendRequest.getToAccountId());

            //validate the balance from the sender
            int amount = sendRequest.getAmount();
            if (fromAccountDetails.getBalance() < amount) {
                throw new StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(ErrorMessage.INSUFFICIENT_FUNDS));
            }

            //updated the sender account balance
            int newFromBalance = fromAccountDetails.getBalance() - amount;
            AccountDetails updatedFromAccountDetails = AccountDetails.newBuilder()
                    .mergeFrom(fromAccountDetails)
                    .setBalance(newFromBalance)
                    .build();
            databaseRepoExBanking.updateAccountDetails(updatedFromAccountDetails);

            //updated the receiver account balance
            int newToBalance = toAccountDetails.getBalance() + amount;
            AccountDetails updatedToAccountDetails = AccountDetails.newBuilder()
                    .mergeFrom(toAccountDetails)
                    .setBalance(newToBalance)
                    .build();
            databaseRepoExBanking.updateAccountDetails(updatedToAccountDetails);

            //create the response
            SendResponse sendResponse = SendResponse.newBuilder()
                    .build();
            log.info("MockDataBaseService sendService sendResponse{}", sendResponse);
            responseObserver.onNext(sendResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error occurred during Send Service", e);
            responseObserver.onError(Status.INTERNAL.withDescription(ErrorMessage.INTERNAL_ERROR).withCause(e).asRuntimeException());
        }

    }

    @Override
    public void depositService(DepositRequest depositRequest, StreamObserver<DepositResponse> responseObserver) {
        try {
            //deposit request validation
            exBankingValidation.depositRequestValidation(depositRequest);
            log.info("MockDatabaseService depositService depositRequest {}", depositRequest);

            //account details
            AccountDetails accountDetails = databaseRepoExBanking.getAccountDetails(depositRequest.getAccountId());
            exBankingValidation.depositAccountValidation(accountDetails);

            //account balance update
            int newBalance = accountDetails.getBalance() + depositRequest.getAmount();
            AccountDetails updatedAccountDetails = AccountDetails.newBuilder()
                    .mergeFrom(accountDetails)
                    .setBalance(newBalance)
                    .build();
            databaseRepoExBanking.updateAccountDetails(updatedAccountDetails);

            //deposit response
            DepositResponse depositResponse = DepositResponse.newBuilder()
                    .setAccountDetails(updatedAccountDetails)
                    .build();
            log.info("ExBankingService depositService depositResponse {}", depositResponse);

            responseObserver.onNext(depositResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e){
            responseObserver.onError(e);
        } catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(ErrorMessage.INTERNAL_ERROR).withCause(e).asRuntimeException());
        }
    }

    @Override
    public void getBalanceService(GetBalanceRequest getBalanceRequest, StreamObserver<GetBalanceResponse> responseObserver) {
        try {
            //get balance validation
            exBankingValidation.getBalanceRequestValidation(getBalanceRequest);
            log.info("MockDatabaseService getBalanceService getBalanceRequest {}", getBalanceRequest);
            AccountDetails accountDetails = databaseRepoExBanking.getAccountDetails(getBalanceRequest.getAccountId());
            exBankingValidation.depositAccountValidation(accountDetails);

            GetBalanceResponse getBalanceResponse = GetBalanceResponse.newBuilder()
                    .setAccountDetails(accountDetails)
                    .build();
            log.info("ExBankingService getBalanceService getBalanceResponse {}", getBalanceResponse);

            responseObserver.onNext(getBalanceResponse);
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e){
            responseObserver.onError(e);
        } catch (Exception e){
            responseObserver.onError(Status.INTERNAL.withDescription(ErrorMessage.INTERNAL_ERROR).withCause(e).asRuntimeException());
        }

    }
}
