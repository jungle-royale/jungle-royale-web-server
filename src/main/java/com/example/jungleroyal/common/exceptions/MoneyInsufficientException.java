package com.example.jungleroyal.common.exceptions;

public class MoneyInsufficientException extends GameServerException {
    public MoneyInsufficientException(Long userId, int currentBalance, int itemPrice) {
        super("돈이 부족합니다. User ID: " + userId +
                ", 현재 잔액: " + currentBalance +
                ", 아이템 가격: " + itemPrice);    }
}
