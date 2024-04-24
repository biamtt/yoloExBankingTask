package com.exbanking.yolo.test.errormessage;

public class ErrorMessage {
    private ErrorMessage(){
    }

    public static final String EMPTY_NAME = "Please enter your name";
    public static final String EMPTY_PERSONAL_ID = "Please enter your personal ID";
    public static final String EMPTY_EMAIL = "Please enter your email";
    public static final String EMPTY_PASSWORD = "Please enter your password";
    public static final String EMPTY_ACCOUNT_NUMBER = "Please enter your Account Number";
    public static final String INVALID_ACCOUNT_NUMBER = "Please enter a valid Account Number";
    public static final String INVALID_FROM_ACCOUNT_NUMBER = "Please enter a valid sender Account Number";
    public static final String INVALID_TO_ACCOUNT_NUMBER = "Please enter a valid Account Number to send money";
    public static final String EMPTY_AMOUNT = "Please enter the amount";
    public static final String INVALID_AMOUNT = "Please enter a valid amount";
    public static final String INSUFFICIENT_FUNDS = "You don't have enough balance";
    public static final String NULL_REQUEST = "Please enter a valid data";
    public static final String INTERNAL_ERROR = "Internal error";
}
