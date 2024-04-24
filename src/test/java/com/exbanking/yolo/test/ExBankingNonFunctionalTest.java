package com.exbanking.yolo.test;

import com.exbanking.yolo.test.service.ExBankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ExBankingNonFunctionalTest {

    @Mock
    private ExBankingService exBankingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPerformanceCreateUser() {
       for (int t = 0; t < 100000; t++){
            CreateUserRequest createUserRequest = CreateUserRequest.newBuilder()
                    .setUserDetails(UserDetails.newBuilder()
                            .setName("Name " + t)
                            .setPersonalId("ID " + t)
                            .setEmail("user" + t + "@performance.test")
                            .setPassword("password" + t))
                    .build();

            long startTime = System.currentTimeMillis();

            exBankingService.createUserService(createUserRequest, null);
            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;
            System.out.println("Request " + (t + 1) + " - Response time " + responseTime + "ms");
        }
    }

    @Test
    public void testPerformanceWithdraw(){
        for (int t = 0; t < 100000; t++){
            WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                    .setAccountId(t)
                    .setAmount(99)
                    .build();

            long startTime = System.currentTimeMillis();

            exBankingService.withdrawService(withdrawRequest, null);
            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;
            System.out.println("Request " + (t + 1) + " - Response time " + responseTime + "ms");
        }
    }
}
