package com.blockchain.block;

import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
import com.blockchain.model.TransactionInput;
import com.blockchain.model.TransactionOutput;
import com.blockchain.model.Wallet;
import com.blockchain.security.CryptoUtil;

/**
 * 区块链测试
 */
@Slf4j
public class BlockServiceTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testBlockMine() throws Exception {
		// 创建一个空的区块链
		val blockChain = new ArrayList<Block>();
		// 生成创世区块
        val beginBlock = new Block(1, System.currentTimeMillis(), new ArrayList<Transaction>(), 1, "1", "1");
        blockChain.add(beginBlock);
        log.debug("{}", JSON.toJSON(blockChain));

		//发送者钱包
		Wallet walletSender = Wallet.generateWallet();
		//接收者钱包
		Wallet walletReceiver = Wallet.generateWallet();

		// 模拟前一个交易
		val txOut1 = new TransactionOutput(10, walletSender.getHashPubKey());
		val tx1 = new Transaction(CryptoUtil.UUID(), null, txOut1);

		// 正常交易
		val txIn = new TransactionInput(tx1.getId(), 10, null, walletSender.getPublicKey());
		val txOut = new TransactionOutput(10, walletReceiver.getHashPubKey());
		val tx2 = new Transaction(CryptoUtil.UUID(), txIn, txOut);
		tx2.sign(walletSender.getPrivateKey(), tx1);

        // 构建块内tx集合
        val txs = new ArrayList<Transaction>();
        //系统奖励的交易
        val sysTx = new Transaction();
		txs.add(sysTx);
		txs.add(tx1);
		txs.add(tx2);

        //获取当前区块链里最后一个区块
        val lastBlock = blockChain.get(blockChain.size() - 1);

		String hash = "";
		int nonce = 1;
		while (true) {
			//Hash = SHA256（最后一个区块的Hash + 交易记录信息 + 随机数）
			hash = CryptoUtil.SHA256(lastBlock.getHash() + JSON.toJSONString(txs) + nonce);

			if (hash.startsWith("0000")) {
				log.info("正确的挖矿hash结果：" + hash + ",计算次数：" + nonce);
				break;
            }
			log.debug("错误的挖矿hash结果：" + hash);
			nonce++;
        }

		val newBlock = new Block(lastBlock.getIndex() + 1, System.currentTimeMillis(), txs, nonce, lastBlock.getHash(), hash);
		blockChain.add(newBlock);
		log.info("挖矿后的区块链：" + JSON.toJSONString(blockChain));
	}
	
	@Test
	public void testGenWallet() throws Exception {
		Wallet wallet = Wallet.generateWallet();
		System.out.println(JSON.toJSON(wallet));
	}
	

}
