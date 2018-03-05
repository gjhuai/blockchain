package com.blockchain.http;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.alibaba.fastjson.JSON;
import com.blockchain.block.BlockService;
import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
import com.blockchain.model.TransactionParam;
import com.blockchain.model.Wallet;
import com.blockchain.p2p.Message;
import com.blockchain.p2p.P2PClient;
import com.blockchain.p2p.P2PServer;
import com.blockchain.p2p.P2PService;

/**
 * 区块链对外http服务
 * @author aaron
 *
 */
public class HTTPService {
    private BlockService blockService;
    private P2PServer   p2pServer;
    private P2PClient   p2pClient;

    public HTTPService(BlockService blockService, P2PServer p2pServer, P2PClient p2pClient) {
        this.blockService = blockService;
        this.p2pServer = p2pServer;
        this.p2pClient = p2pClient;
    }

    public void initHTTPServer(int port) {
        try {
            Server server = new Server(port);
            System.out.println("listening http port on: " + port);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            
            //查询区块链
            context.addServlet(new ServletHolder(new ChainServlet()), "/chain");
            //创建钱包
            context.addServlet(new ServletHolder(new CreateWalletServlet()), "/wallet/create");
            //查询钱包
            context.addServlet(new ServletHolder(new GetWalletsServlet()), "/wallet/get");
            //挖矿
            context.addServlet(new ServletHolder(new MineServlet()), "/mine");
            //转账交易
            context.addServlet(new ServletHolder(new NewTransactionServlet()), "/transactions/new");
            //查询钱包余额
            context.addServlet(new ServletHolder(new GetWalletBalanceServlet()), "/wallet/balance/get");
            
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println("init http server is error:" + e.getMessage());
        }
    }

    private class ChainServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.setCharacterEncoding("UTF-8");
        	resp.getWriter().print("当前区块链：" + JSON.toJSONString(blockService.getBlockchain()));
        }
    }
    
    private class MineServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.setCharacterEncoding("UTF-8");
        	String address = req.getParameter("address");
        	Wallet myWallet = blockService.getMyWalletMap().get(address);
        	if (myWallet == null) {
        		resp.getWriter().print("挖矿指定的钱包不存在");
			}
        	Block newBlock = blockService.mine(address);
        	Block[] blocks = {newBlock};
            String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_BLOCKCHAIN, JSON.toJSONString(blocks)));
        	p2pServer.broatcast(msg);
        	p2pClient.broatcast(msg);
        	resp.getWriter().print("挖矿生成的新区块：" + JSON.toJSONString(newBlock));
        }
    }
    
    private class CreateWalletServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.setCharacterEncoding("UTF-8");
            Wallet wallet = blockService.createWallet();
            String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_WALLET, JSON.toJSONString(new Wallet(wallet.getPublicKey()))));
            p2pServer.broatcast(msg);
            p2pClient.broatcast(msg);
            resp.getWriter().print("创建钱包成功，钱包地址： " + wallet.getAddress());
        }
    }
    
    private class GetWalletsServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        	resp.setCharacterEncoding("UTF-8");
        	resp.getWriter().print("当前节点钱包：" + JSON.toJSONString(blockService.getMyWalletMap().values()));
        }
    }
    
    private class NewTransactionServlet extends HttpServlet {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 5928806783225163929L;

		@Override
    	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		resp.setCharacterEncoding("UTF-8");
    		TransactionParam txParam = JSON.parseObject(getReqBody(req), TransactionParam.class); 
    		
    		Wallet senderWallet = blockService.getMyWalletMap().get(txParam.getSender());
        	Wallet recipientWallet = blockService.getMyWalletMap().get(txParam.getRecipient());
        	if (recipientWallet == null) {
        		recipientWallet = blockService.getOtherWalletMap().get(txParam.getRecipient());
    		}
        	if (senderWallet == null || recipientWallet == null) {
        		resp.getWriter().print("钱包不存在");
    		}
        	
    		Transaction newTransaction = blockService.createTransaction(senderWallet, recipientWallet, txParam.getAmount());
    		if (newTransaction == null) {
				resp.getWriter().print("钱包"+ txParam.getSender() +"余额不足或该钱包找不到一笔等于" +txParam.getAmount()+ "BTC的UTXO");
			} else {
				resp.getWriter().print("新生成交易：" + JSON.toJSONString(newTransaction));
				String msg = JSON.toJSONString(new Message(P2PService.RESPONSE_Transaction, JSON.toJSONString(newTransaction)));
	            p2pServer.broatcast(msg);
	            p2pClient.broatcast(msg);
			}
    	}
    }
    
    private class GetWalletBalanceServlet extends HttpServlet {
    	@Override
    	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		resp.setCharacterEncoding("UTF-8");
    		String address = req.getParameter("address");
    		resp.getWriter().print("钱包余额为：" + blockService.getWalletBalance(address) + "BTC");
    	}
    }

    private String getReqBody(HttpServletRequest req) throws IOException {
		BufferedReader br = req.getReader();
    	String str, body = "";
    	while((str = br.readLine()) != null){
    		body += str;
    	}
		return body;
	}
}

