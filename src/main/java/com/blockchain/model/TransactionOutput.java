package com.blockchain.model;

import lombok.Data;

/**
 * 交易输出
 * 
 * @author aaron
 *
 */
@Data
public class TransactionOutput {

	/**
	 * 交易金额
	 */
	private int value;
	/**
	 * 交易接收方的钱包公钥的hash值
	 */
	private String publicKeyHash;

	public TransactionOutput() {
		super();
	}

	public TransactionOutput(int value, String publicKeyHash) {
		this.value = value;
		this.publicKeyHash = publicKeyHash;
	}
}
