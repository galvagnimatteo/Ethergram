package org.telegram.ethergramUtils;

import org.ethereum.geth.Account;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

public class NodeHolder {

    private Web3j node;
    private Account account;
    private static NodeHolder instance = null;

    private NodeHolder() {
    }

    public static NodeHolder getInstance() {
        if (instance == null) {
            instance = new NodeHolder();
        }
        return instance;
    }

    public Web3j getNode() {
        return node;
    }

    public void setNode(Web3j node) {
        this.node = node;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}