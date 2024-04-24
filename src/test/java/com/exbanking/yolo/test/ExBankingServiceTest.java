package com.exbanking.yolo.test;

import com.exbanking.yolo.test.datarepo.DatabaseRepoExBanking;
import com.exbanking.yolo.test.service.ExBankingService;
import com.exbanking.yolo.test.validation.ExBankingValidation;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExBankingServiceTest {
    private ExBankingService exBankingService;
    private ExBankingValidation exBankingValidationMock;
    private DatabaseRepoExBanking databaseRepoExBankingMock;
    private StreamObserver<CreateUserResponse> responseObserverMock;

    @BeforeEach
    void setUp() {
        exBankingValidationMock = mock(ExBankingValidation.class);
        databaseRepoExBankingMock = mock(DatabaseRepoExBanking.class);
        exBankingService = new ExBankingService(exBankingValidationMock, databaseRepoExBankingMock);
        responseObserverMock = mock(StreamObserver.class);
    }

    @Test
    void testCreateUserSuccessfully() {
        //test data
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setUserDetails(UserDetails.newBuilder()
                        .setName("Bianca Test")
                        .setPersonalId("49406151236")
                        .setEmail("bianca@test.com")
                        .setPassword("password123"))
                .build();

        //executing method to be tested
        exBankingService.createUserService(request, responseObserverMock);

        //check if .onNext() method was called on the StreamObserver with expected results
        verify(responseObserverMock).onNext(any(CreateUserResponse.class));
        verify(responseObserverMock).onCompleted();
    }

    @Test
    void testWithdrawSuccessfully() {
        //test data
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountId(2587416)
                .setAmount(100)
                .build();

        AccountDetails initialAccountDetails = AccountDetails.newBuilder()
                .setAccountId(2587416)
                .setBalance(300)
                .build();

        AccountDetails updatedAccountDetails = AccountDetails.newBuilder()
                .setAccountId(2587416)
                .setBalance(200)
                .build();

        //mock behavior definition
        when(databaseRepoExBankingMock.getAccountDetails(2587416)).thenReturn(initialAccountDetails);

        //executing method under test
        exBankingService.withdrawService(withdrawRequest, mock(StreamObserver.class));

        //check if the validation method was called
        verify(exBankingValidationMock).withdrawRequestValidation(withdrawRequest);

        //get argument passed to updateAccountDetails
        ArgumentCaptor<AccountDetails> argumentCaptor = ArgumentCaptor.forClass(AccountDetails.class);
        verify(databaseRepoExBankingMock).updateAccountDetails(argumentCaptor.capture());

        //assert the account details was passed to updateAccountDetails
        AccountDetails capturedAccountDetails = argumentCaptor.getValue();
        assertEquals(updatedAccountDetails, capturedAccountDetails);
    }

    @Test
    void testSendSuccessfully() {
        //test data
        SendRequest sendRequest = SendRequest.newBuilder()
                .setFromAccountId(325478)
                .setToAccountId(547852)
                .setAmount(50)
                .build();

        AccountDetails fromAccountDetails = AccountDetails.newBuilder()
                .setAccountId(325478)
                .setBalance(100)
                .build();

        AccountDetails toAccountDetails = AccountDetails.newBuilder()
                .setAccountId(547852)
                .setBalance(20)
                .build();

        int expectedNewFromBalance = fromAccountDetails.getBalance() - sendRequest.getAmount();
        int expectedNewToBalance = toAccountDetails.getBalance() + sendRequest.getAmount();

        //mock behavior definition
        when(databaseRepoExBankingMock.getAccountDetails(325478)).thenReturn(fromAccountDetails);
        when(databaseRepoExBankingMock.getAccountDetails(547852)).thenReturn(toAccountDetails);

        //execute method under test
        exBankingService.sendService(sendRequest, mock(StreamObserver.class));

        //check the validation method is being called
        verify(exBankingValidationMock).sendRequestValidation(sendRequest);

        //capture arguments passed to updateAccountDetails
        ArgumentCaptor<AccountDetails> argumentCaptor = ArgumentCaptor.forClass(AccountDetails.class);
        verify(databaseRepoExBankingMock, times(2)).updateAccountDetails(argumentCaptor.capture());

        //assert the captured account details
        List<AccountDetails> capturedAccountDetailsList = argumentCaptor.getAllValues();
        AccountDetails capturedFromAccountDetails = capturedAccountDetailsList.get(0);
        AccountDetails capturedToAccountDetails = capturedAccountDetailsList.get(1);
        assertEquals(expectedNewFromBalance, capturedFromAccountDetails.getBalance());
        assertEquals(expectedNewToBalance, capturedToAccountDetails.getBalance());
    }

    @Test
    void testDepositSuccessfully() {
        //test data
        DepositRequest depositRequest = DepositRequest.newBuilder()
                .setAccountId(748596)
                .setAmount(66)
                .build();

        AccountDetails accountDetails = AccountDetails.newBuilder()
                .setAccountId(748596)
                .setBalance(34)
                .build();

        AccountDetails updatedAccountDetails = AccountDetails.newBuilder()
                .setAccountId(748596)
                .setBalance(100)
                .build();

        //mock behavior definition
        when(databaseRepoExBankingMock.getAccountDetails(748596)).thenReturn(accountDetails);

        //executing method
        StreamObserver<DepositResponse> responseStreamObserver = mock(StreamObserver.class);
        exBankingService.depositService(depositRequest, responseStreamObserver);

        //check the validation method
        verify(exBankingValidationMock).depositRequestValidation(depositRequest);
        verify(exBankingValidationMock).depositAccountValidation(accountDetails);

        //updated accountdetails
        verify(databaseRepoExBankingMock).updateAccountDetails(any(AccountDetails.class));

        //capture the response via api
        verify(responseStreamObserver, times(1)).onNext(any(DepositResponse.class));
        verify(responseStreamObserver, times(1)).onCompleted();

        ArgumentCaptor<DepositResponse> argumentCaptor = ArgumentCaptor.forClass(DepositResponse.class);
        verify(responseStreamObserver, times(1)).onNext(argumentCaptor.capture());
        DepositResponse capturedDepositResponse = argumentCaptor.getValue();
        assertEquals(updatedAccountDetails, capturedDepositResponse.getAccountDetails());
    }

    @Test
    public void testGetBalanceSuccessfully(){

        StreamObserver<GetBalanceResponse> responseObserverMock = mock(StreamObserver.class);

        //test data
        int accountID = 546321;
        GetBalanceRequest getBalanceRequest = GetBalanceRequest.newBuilder()
                .setAccountId(accountID)
                .build();

        AccountDetails accountDetails = AccountDetails.newBuilder()
                .setAccountId(accountID)
                .setBalance(2541)
                .build();

        GetBalanceResponse getBalanceResponse = GetBalanceResponse.newBuilder()
                .setAccountDetails(accountDetails)
                .build();

        //mock the behavior
        when(databaseRepoExBankingMock.getAccountDetails(accountID)).thenReturn(accountDetails);

        //method under test
        ExBankingService exBankingService = new ExBankingService(exBankingValidationMock, databaseRepoExBankingMock);
        exBankingService.getBalanceService(getBalanceRequest, responseObserverMock);

        //verify expected response
        verify(responseObserverMock).onNext(getBalanceResponse);
        verify(responseObserverMock, times(1)).onCompleted();
    }
}
