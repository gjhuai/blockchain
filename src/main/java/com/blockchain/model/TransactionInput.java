package com.blockchain.model;

import lombok.Data;

/**
 * 交易输入
 * 
 * @author aaron
 *
 */
@Data
public class TransactionInput {

	/**
	 * 前一次交易id
	 */
	private String txId;
	/**
	 * 交易金额
	 */
	private int value;
	/**
	 * 交易签名
	 */
	private String signature;
	/**
	 * 交易发送方的钱包公钥
	 */
	private String publicKey;

	public TransactionInput() {
	}

	public TransactionInput(String txId, int value, String signature, String publicKey) {
		super();
		this.txId = txId;
		this.value = value;
		this.signature = signature;
		this.publicKey = publicKey;
	}

}
