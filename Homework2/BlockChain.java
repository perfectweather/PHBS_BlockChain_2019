// Block Chain should maintain only limited block nodes to satisfy the functions
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockChain {
    public static int CUT_OFF_AGE = 10;
    public BlockChainNode highestNode;
    public List<BlockChainNode> chain;
    public TransactionPool transactionPool;

    public static class BlockChainNode {
        public Block blcok;
        public UTXOPool utxoPool;
        public int height;


        public BlockChainNode(Block block, UTXOPool utxoPool, int height) {

            this.blcok = block;
            this.utxoPool = new UTXOPool(utxoPool);
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public Block getBlcok() {
            return blcok;
        }

        public UTXOPool getUtxoPool() {
            return utxoPool;
        }
    }

    /**
     * create an empty block chain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    public BlockChain(Block genesisBlock) {
        UTXOPool utxoPool = new UTXOPool();
        //utxoPool.addUTXO(new UTXO(genesisBlock.getCoinbase().getHash(), 0), genesisBlock.getCoinbase().getOutput(0));
        BlockChainNode genesisNode = new BlockChainNode(genesisBlock, utxoPool,2);
        List BlockChainNode = new ArrayList<BlockChainNode>();
        BlockChainNode.add(genesisNode);
        this.chain = BlockChainNode;
        this.highestNode = genesisNode;
        this.transactionPool = new TransactionPool();
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        return highestNode.getBlcok();
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        return highestNode.getUtxoPool();

    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    /** Get the chain */
    public List<BlockChainNode> getChain() {
        return chain;
    }

    /** Get the  highestNode*/
    public BlockChainNode getHighestNode() {
        return highestNode;
    }

    /**
     * Add {@code block} to the block chain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}.
     *
     * <p>
     * For example, you can try creating a new block over the genesis block (block height 2) if the
     * block chain height is {@code <=
     * CUT_OFF_AGE + 1}. As soon as {@code height > CUT_OFF_AGE + 1}, you cannot create a new block
     * at height 2.
     *
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        if (hasPreBlockHashAndNotGenesis(block) && heightRight(block)  && txsValid(block)){
            List<Transaction> txs = block.getTransactions();
            int newHeight = 0;
            UTXOPool utxoPool = new UTXOPool();
            TransactionPool txPool = new TransactionPool();
            for (Transaction tx : transactionPool.getTransactions()) {
                if (!txs.contains(tx)) {
                    txPool.addTransaction(tx);//update transactionPool
                }
            }
            transactionPool = txPool;
            for (BlockChainNode bn : chain) {
                if (bn.blcok.getHash() == block.getPrevBlockHash()) {
                    newHeight = bn.height + 1;
                    utxoPool = new UTXOPool(bn.utxoPool);
                    utxoPool.addUTXO(new UTXO(bn.blcok.getCoinbase().getHash(),0),bn.blcok.getCoinbase().getOutput(0));
                    break;
                }
            }

            TxHandler txHandler = new TxHandler(utxoPool);
            Transaction[] validTx = txHandler.handleTxs(txs.toArray(new Transaction[txs.size()]));
            BlockChainNode newNode = new BlockChainNode(block, txHandler.getUtxoPool(),newHeight);
            chain.add(newNode);//update chain
            if(highestNode.height == newHeight-1){
                highestNode = newNode;//update highestNode
            }
            return true;
        }
        return false;
    }

    public boolean hasPreBlockHashAndNotGenesis(Block block) {
        for (BlockChainNode bn:chain){
            if (bn.blcok.getHash() == block.getPrevBlockHash()) {
                return true;
            }
        }
        return false;
    }

    public boolean heightRight(Block block) {
        int height = 0;
        for (BlockChainNode bn:chain){
            if (bn.blcok.getHash() == block.getPrevBlockHash()) {
                height= bn.height;
                break;
            }
        }
        return height > highestNode.getHeight() - CUT_OFF_AGE;
    }


    public boolean txsValid(Block block) {
        UTXOPool utxoPool = new UTXOPool();
        for (BlockChainNode bn:chain){
            if (bn.blcok.getHash() == block.getPrevBlockHash()) {
                utxoPool= new UTXOPool(bn.utxoPool);
                utxoPool.addUTXO(new UTXO(bn.blcok.getCoinbase().getHash(),0),bn.blcok.getCoinbase().getOutput(0));
                break;
            }
        }
        List<Transaction> txs = block.getTransactions();
        List<Transaction> validTxs = Arrays.asList(new TxHandler(utxoPool).handleTxs(txs.toArray(new Transaction[txs.size()])));
        return validTxs.size() == txs.size();
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        transactionPool.addTransaction(tx);
    }

    public void clear(){
        ArrayList<BlockChainNode> newChain= new ArrayList<BlockChainNode>();
        for (BlockChainNode bn:chain){
            if (bn.height >= highestNode.height-CUT_OFF_AGE ) {
                newChain.add(bn);
            }
        }
        chain = newChain;
    }

}