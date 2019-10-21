import java.lang.reflect.Array;
import java.security.*;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BlockChainTest {
    public ArrayList<Transaction> tx = new ArrayList<Transaction>();
    public ArrayList<KeyPair> keyPair = createUsersKeyPair(5);
    public Block  genesisBlock = new Block(null, keyPair.get(0).getPublic());
    public BlockChain blockChain;
    public BlockChainTest() throws NoSuchAlgorithmException {
        this.tx = new ArrayList<Transaction>();
    }

    //public BlockChain blockChain;
    /**before is used to generate txs without error and test valid txs(do not depend on each) can be judged to valid(isValid == True)
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    @Before
    public void before() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        //UTXOPool utxoPool = new UTXOPool();

        tx = new ArrayList<Transaction>();
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        tx1.addInput(genesisBlock.getCoinbase().getHash(), 0);
        tx2.addInput(genesisBlock.getCoinbase().getHash(), 0);
        for (int i = 0; i < 5; i++) {
            //int j = random.nextInt(oldTxNumber);
            //int u = random.nextInt(users);
            Random random = new Random();
            double v = Math.random() * 2 + 0.1;
            tx1.addOutput(v, keyPair.get(1).getPublic());
            tx2.addOutput(v, keyPair.get(2).getPublic());
            //findOwners.put(tx.getOutput(i), 0);
            //UTXO utxo0 = new UTXO(tx.get(0).getHash(), i);
            //UTXO utxo1 = new UTXO(tx.get(1).getHash(), i);
            //utxoPool.addUTXO(utxo0, tx.get(0).getOutput(i));
            //utxoPool.addUTXO(utxo1, tx.get(1).getOutput(i));
        }
        tx1.addSignature(sign(keyPair.get(0).getPrivate(), tx1.getRawDataToSign(0)), 0);
        tx2.addSignature(sign(keyPair.get(0).getPrivate(), tx2.getRawDataToSign(0)), 0);
        tx1.finalize();
        tx2.finalize();
        tx.add(tx1);
        tx.add(tx2);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        //TestData data = new TestData(blockChain,tx);


    }

    //Test BlockChain.addTransaction(Transaction tx);
    @org.junit.Test
    public void test1()  {
        List<Transaction> txs = blockChain.getTransactionPool().getTransactions();
        assertTrue("the function is inValid",txs.contains(tx.get(0)));
        assertTrue("the function is inValid",txs.contains(tx.get(1)));
        assertTrue("the function is inValid",txs.size()==2);
    }
    //Test  BlockChain.getMaxHeightBlock();
    @org.junit.Test
    public void test2()  {
        assertTrue("the function is inValid",blockChain.getMaxHeightBlock().equals(genesisBlock) );
    }

    //Test  BlockChain.getMaxHeightUTXOPool();
    @org.junit.Test
    public void test3()  {
        UTXOPool utxoPool = new UTXOPool();
        List<UTXO> utxoList = new ArrayList<UTXO>();
        assertTrue("the function is inValid",blockChain.getMaxHeightUTXOPool().getAllUTXO().equals(utxoPool.getAllUTXO()));
        assertTrue("the function is inValid",blockChain.getMaxHeightUTXOPool().getAllUTXO().equals(utxoList));
        //assertTrue("the function is inValid",blockChain.getMaxHeightUTXOPool().getTxOutput(utxo).equals(utxoPool.getTxOutput(utxo)));
    }


    //Test BlockChain.addBlock(Block block) by adding a new block on the block with max height already in the blockchain
    @org.junit.Test
    public void test4()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        UTXOPool utxoPool = blockChain.highestNode.getUtxoPool();
        utxoPool.addUTXO(new UTXO(genesisBlock.getCoinbase().getHash(),0),genesisBlock.getCoinbase().getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPool);
        Transaction[] validTx = txHandler.handleTxs(block1.getTransactions().toArray(new Transaction[block1.getTransactions().size()]));
        BlockChain.BlockChainNode blockChainNode1 = new BlockChain.BlockChainNode(block1,txHandler.getUtxoPool(),3);
        TransactionPool txPool = new TransactionPool();
        txPool.addTransaction(tx.get(1));
        List<BlockChain.BlockChainNode> chain1 = blockChain.getChain();
        blockHandler.processBlock(block1);
        chain1.add(blockChainNode1);
        assertTrue("the function is inValid",blockChain.getHighestNode().getBlcok().equals(block1));
        assertTrue("the function is inValid",blockChain.getHighestNode().getHeight()==3);
        assertTrue("the function is inValid",blockChain.getHighestNode().getUtxoPool().getAllUTXO().equals(txHandler.getUtxoPool().getAllUTXO()));
        for(UTXO utxo:blockChain.getHighestNode().getUtxoPool().getAllUTXO()){
            assertTrue("the function is inValid",blockChain.getHighestNode().getUtxoPool().getTxOutput(utxo).equals(txHandler.getUtxoPool().getTxOutput(utxo)));
        }
        assertTrue("the function is inValid",blockChain.getChain().equals(chain1));
    }

    //Test  BlockChain.addBlock(Block block) by adding a new block on the block(height bigger than maxheight - cut off age) already in the blockchain
    @org.junit.Test
    public void test5()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        Block block2 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        block2.addTransaction(tx.get(1));
        block2.finalize();
        UTXOPool utxoPool = new UTXOPool(blockChain.highestNode.getUtxoPool());
        blockHandler.processBlock(block1);
        blockHandler.processBlock(block2);
        UTXO utxo = new UTXO(genesisBlock.getCoinbase().getHash(), 0);
        utxoPool.addUTXO(utxo, genesisBlock.getCoinbase().getOutput(0));
        TxHandler txHandler1 = new TxHandler(utxoPool);
        UTXOPool utxoPool2 = new UTXOPool(utxoPool);
        Transaction[] validTx1 = txHandler1.handleTxs(block1.getTransactions().toArray(new Transaction[block1.getTransactions().size()]));
        BlockChain.BlockChainNode blockChainNode1 = new BlockChain.BlockChainNode(block1,txHandler1.getUtxoPool(),3);
        TxHandler txHandler2 = new TxHandler(utxoPool2);
        Transaction[] validTx2 = txHandler2.handleTxs(block2.getTransactions().toArray(new Transaction[block1.getTransactions().size()]));
        BlockChain.BlockChainNode blockChainNode2 = new BlockChain.BlockChainNode(block2,txHandler2.getUtxoPool(),3);
        List<BlockChain.BlockChainNode> chain1 = blockChain.getChain();
        chain1.add(blockChainNode1);
        chain1.add(blockChainNode2);
        assertTrue("the function is inValid",blockChain.getHighestNode().getBlcok().equals(block1));
        assertTrue("the function is inValid",blockChain.getHighestNode().getHeight()==3);
        //assertTrue("the function is inValid",blockChain.getHighestNode().getUtxoPool().getAllUTXO().equals(txHandler1.getUtxoPool().getAllUTXO()));
        for(UTXO Utxo:blockChain.getHighestNode().getUtxoPool().getAllUTXO()){
            assertTrue("the function is inValid",blockChain.getHighestNode().getUtxoPool().getTxOutput(Utxo).equals(txHandler1.getUtxoPool().getTxOutput(Utxo)));
        }
        assertTrue("the function is inValid",blockChain.getChain().equals(chain1));
    }

    //Test  BlockChain.addBlock(Block block) by adding another genesis block
    @org.junit.Test
    public void test6()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(null,keyPair.get(1).getPublic());
        block1.finalize();
        assertFalse(blockHandler.processBlock(block1));
        assertFalse(blockChain.addBlock(block1));
        assertFalse(blockChain.hasPreBlockHashAndNotGenesis(block1));
        assertTrue("the function is inValid",blockChain.txsValid(block1));
    }

    //Test  BlockChain.addBlock(Block block) by adding a block without pre block
    @org.junit.Test
    public void test7()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        byte[] a = new byte[256];
        java.util.Arrays.fill(a, (byte) 0);
        Block block1 = new Block(a,keyPair.get(1).getPublic());
        block1.finalize();
        assertFalse(blockHandler.processBlock(block1));
        assertFalse(blockChain.addBlock(block1));
        assertFalse(blockChain.hasPreBlockHashAndNotGenesis(block1));
        assertTrue("the function is inValid",blockChain.txsValid(block1));
    }

    //Test  BlockChain.addBlock(Block block) by adding a new block on the block the height of witch is <= than maxheight - cut off age)
    @org.junit.Test
    public void test8()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        blockChain.CUT_OFF_AGE = 1;
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        Block block2 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block2.addTransaction(tx.get(1));
        block2.finalize();
        blockHandler.processBlock(block1);
        assertFalse(blockHandler.processBlock(block2));
        assertFalse(blockChain.addBlock(block2));
        assertFalse(blockChain.heightRight(block2));
        assertTrue("the function is inValid",blockChain.txsValid(block2));
        assertTrue("the function is inValid",blockChain.hasPreBlockHashAndNotGenesis(block2));
    }

    //Test  BlockChain.addBlock(Block block) by adding a new block whose transaction are not all valid
    @org.junit.Test
    public void test9() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        Transaction wrongTx = new Transaction();
        wrongTx.addInput(genesisBlock.getCoinbase().getHash(),0);
        wrongTx.addOutput(26,keyPair.get(0).getPublic());
        wrongTx.addSignature(sign(keyPair.get(0).getPrivate(),wrongTx.getRawDataToSign(0)),0);
        wrongTx.finalize();
        block1.addTransaction(wrongTx);
        block1.finalize();
        blockHandler.processBlock(block1);
        assertFalse(blockHandler.processBlock(block1));
        assertFalse(blockChain.addBlock(block1));
        assertFalse(blockChain.txsValid(block1));
        assertTrue("the function is inValid",blockChain.heightRight(block1));
        assertTrue("the function is inValid",blockChain.hasPreBlockHashAndNotGenesis(block1));
    }

    //Test  BlockChain.addBlock(Block block) about a coinbase transaction of a block is available to be spent in the next block mined on top of it.
    @org.junit.Test
    public void test10() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        Block block2 = new Block(block1.getHash(),keyPair.get(1).getPublic());
        Transaction txWithCoinBaseTxFromParentBlock = new Transaction();
        txWithCoinBaseTxFromParentBlock.addInput(block1.getCoinbase().getHash(),0);
        txWithCoinBaseTxFromParentBlock.addOutput(1,keyPair.get(1).getPublic());
        txWithCoinBaseTxFromParentBlock.addSignature(sign(keyPair.get(1).getPrivate(),txWithCoinBaseTxFromParentBlock.getRawDataToSign(0)),0);
        txWithCoinBaseTxFromParentBlock.finalize();
        block2.addTransaction(txWithCoinBaseTxFromParentBlock);
        block2.finalize();
        assertTrue("the function is inValid",blockHandler.processBlock(block1));
        assertTrue("the function is inValid",blockHandler.processBlock(block2));
    }

    //Test  BlockChain.addBlcok() by adding a new block with transaction has been in other blocks
    @org.junit.Test
    public void test11()  {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        Block block2 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block2.addTransaction(tx.get(0));
        block2.finalize();
        assertTrue("the function is inValid",blockHandler.processBlock(block1));
        assertTrue("the function is inValid",blockHandler.processBlock(block2));
    }

    //Test  BlockChain.clear()
    @org.junit.Test
    public void test12() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        genesisBlock.finalize();
        blockChain = new BlockChain(genesisBlock);
        blockChain.CUT_OFF_AGE = 1;
        BlockHandler blockHandler = new BlockHandler(blockChain);
        blockHandler.processTx(tx.get(0));
        blockHandler.processTx(tx.get(1));
        Block block1 = new Block(genesisBlock.getHash(),keyPair.get(1).getPublic());
        block1.addTransaction(tx.get(0));
        block1.finalize();
        Block block2 = new Block(block1.getHash(),keyPair.get(1).getPublic());
        Transaction txWithCoinBaseTxFromParentBlock = new Transaction();
        txWithCoinBaseTxFromParentBlock.addInput(block1.getCoinbase().getHash(),0);
        txWithCoinBaseTxFromParentBlock.addOutput(1,keyPair.get(1).getPublic());
        txWithCoinBaseTxFromParentBlock.addSignature(sign(keyPair.get(1).getPrivate(),txWithCoinBaseTxFromParentBlock.getRawDataToSign(0)),0);
        txWithCoinBaseTxFromParentBlock.finalize();
        block2.addTransaction(txWithCoinBaseTxFromParentBlock);
        block2.finalize();
        blockHandler.processBlock(block1);
        BlockChain.BlockChainNode blockChainNode1 = blockChain.getHighestNode();
        List<BlockChain.BlockChainNode> chain1 = new ArrayList<BlockChain.BlockChainNode>();
        chain1.add(blockChainNode1);
        blockHandler.processBlock(block2);
        BlockChain.BlockChainNode blockChainNode2 = blockChain.getHighestNode();
        blockChain.clear();
        chain1.add(blockChainNode2);
        assertTrue("the function is inValid",blockChain.getHighestNode().getBlcok().equals(block2));
        assertTrue("the function is inValid",blockChain.getHighestNode().getHeight()==4);
        //System.out.println(b);
        assertTrue("the function is inValid",blockChain.getChain().equals(chain1));
    }

    //createUsersKeyPair(public key and private key)
    public static ArrayList<KeyPair> createUsersKeyPair(int usersNumber) throws NoSuchAlgorithmException {
        ArrayList<KeyPair> users = new ArrayList<KeyPair>();
        for (int i = 0; i < usersNumber; i++) {
            users.add(KeyPairGenerator.getInstance("RSA").generateKeyPair());
        }
        return new ArrayList<KeyPair>(users);
    }

    private static byte[] sign(PrivateKey privateKey, byte[] dataToSign) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = null;
        try {
            signature = Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            signature.initSign(privateKey);// Initializes this Signature instance for signing, using the private key of the identity whose signature is going to be generated.
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        try {
            signature.update(dataToSign);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return signature.sign();
    }
}
