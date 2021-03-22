package org.telegram.ethergramUtils;

public class ERC20Transaction extends Transaction implements Comparable{

    private String contractAddress;
    private String tokenName;
    private String tokenSymbol;
    private String tokenDecimal;

    public ERC20Transaction(){


    }

    public ERC20Transaction(String timestamp, String hash, String blockHash, String blockNumber, String transactionIndex, String from, String to, String value, String gasPrice, String gas, String input, String isError, String contractAddress, String tokenName, String tokenSymbol, String tokenDecimal) {
        super(timestamp, hash, blockHash, blockNumber, transactionIndex, from, to, value, gasPrice, gas, input, isError);
        this.contractAddress = contractAddress;
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.tokenDecimal = tokenDecimal;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public void setTokenDecimal(String tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getTokenName() {
        return tokenName;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public String getTokenDecimal() {
        return tokenDecimal;
    }

}
