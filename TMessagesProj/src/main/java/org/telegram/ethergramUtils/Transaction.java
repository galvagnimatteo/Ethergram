package org.telegram.ethergramUtils;

public class Transaction implements Comparable{

    private String hash;
    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String from;
    private String to;
    private String value;
    private String gasPrice;
    private String gas;
    private String input;
    private String isError;
    private String timestamp;

    public Transaction() {
    }

    public Transaction(String timestamp, String hash, String blockHash, String blockNumber, String transactionIndex, String from, String to, String value, String gasPrice, String gas, String input, String isError) {
        this.timestamp = timestamp;
        this.hash = hash;
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.transactionIndex = transactionIndex;
        this.from = from;
        this.to = to;
        this.value = value;
        this.gasPrice = gasPrice;
        this.gas = gas;
        this.input = input;
        this.isError = isError;
    }

    public void setIsError(String isError) {
        this.isError = isError;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setError(String error) {
        isError = error;
    }

    public String getIsError() {
        return isError;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getValue() {
        return value;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public String getGas() {
        return gas;
    }

    public String getInput() {
        return input;
    }

    public String isError() {
        return isError;
    }

    @Override
    public int compareTo(Object o) {

        Transaction tocompare = (Transaction) o;

        return Integer.parseInt(tocompare.getTimestamp()) - Integer.parseInt(this.getTimestamp());

    }
}
