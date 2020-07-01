package com.blockchain.model;

import lombok.Data;

/**
 * 交易接口参数
 * 
 * @author aaron
 *
 */
@Data
public class TransactionParam {

	/**
	 * 发送方钱包地址
	 */
	private String sender;
	/**
	 * 接收方钱包地址
	 */
	private String recipient;
	/**
	 * 发送金额
	 */
	private int Amount;

}
