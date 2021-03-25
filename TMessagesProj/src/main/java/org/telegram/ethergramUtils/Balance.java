package org.telegram.ethergramUtils;

import java.math.BigDecimal;

public class Balance {

    private BigDecimal balance;
    private String tokenSymbol;

    public Balance(String tokenSymbol, BigDecimal balance) {

        this.balance = balance;
        this.tokenSymbol = tokenSymbol;

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public void add(BigDecimal toAdd){
        balance = balance.add(toAdd);
    }

    public void subtract(BigDecimal toSubtract){
        balance = balance.subtract(toSubtract);
    }

}
