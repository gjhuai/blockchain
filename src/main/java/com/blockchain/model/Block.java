package com.blockchain.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * 区块结构
 * 
 * @author aaron
 *
 */
@Data
public class Block {

	/**
	 * 区块索引号
	 */
	@NonNull
	private Integer index;
	/**
	 * 当前区块的hash值,区块唯一标识
	 */
	@NonNull
	private String hash;
	/**
	 * 生成区块的时间戳
	 */
	@NonNull
	private Long timestamp;
	/**
	 * 当前区块的交易集合
	 */
	private List<Transaction> transactions;
	/**
	 * 工作量证明，计算正确hash值的次数
	 */
	@NonNull
	private Integer nonce;
	/**
	 * 前一个区块的hash值
	 */
	private String previousHash;

	public Block() {
		super();
	}

	public Block(int index, long timestamp, List<Transaction> transactions, int nonce, String previousHash, String hash) {
		super();
		this.index = index;
		this.timestamp = timestamp;
		this.transactions = transactions;
		this.nonce = nonce;
		this.previousHash = previousHash;
		this.hash = hash;
	}

}
