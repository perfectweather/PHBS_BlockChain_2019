import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TxHandlerTest {

    /**before is used to generate txs without error and test valid txs(do not depend on each) can be judged to valid(isValid == True)
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    @Before
    public void before() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        TxHandler txHandler0 = new TxHandler(create0.utxoPool);
        for (Transaction tx: create0.newTx ) {
            assertTrue("this tx is invalid", txHandler0.isValidTx(tx));
        }
        assertEquals(txHandler0.handleTxs(create0.newTx),create0.newTx);
    }

    /**
     * •test1()~test10() use createUTXOPoolAndTx.test1Generator ~ createUTXOPoolAndTx.test10Generator separately to get data<br>
     * •test1() ~ test8() test isValid() method<br>
     * •test 9() and test10() test handleTxs() method: some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet while test1() ~ test8() test transactions which are independent and all the inputs are outputs from tx that already be confirmed.<br>
     * •test1() will test the situation that the input tx[] is empty.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    //test1：(1).1, not all outputs claimed by {@code tx} are in the current UTXO pool.
    // Assume that outputs not in the current UTXO pool since corresponding index in UTXO Pool is different.
    @org.junit.Test
    public void test1() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test1Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        //Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test1
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertFalse(txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test2：(1).2, not all outputs claimed by {@code tx} are in the current UTXO pool.
    // Assume that outputs not in the current UTXO pool since corresponding preTxHash in UTXO Pool is different.
    @org.junit.Test
    public void test2() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test2Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        //Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test2
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertFalse(txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }
    //test3 (1).3 not all outputs claimed by {@code tx} are in the current UTXO pool.
    // double spending in different transaction can lend to the result that do not satisfies (1). PreTxHash/ Index or both of them are not in the current UTXP pool.
    @org.junit.Test
    public void test3() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test3Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        //Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test3
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertFalse(txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test4 (2).1 the signatures on each in put of {@code tx} are invalid
    //the signature is not accordance with message
    @org.junit.Test
    public void test4() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test4Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.inCuUTXOpool(invalidTx[0]));
        assertFalse(txHandler.signIsTrue(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.sumOfOutput(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test5 (2).2 the signatures on each input of {@code tx} are invalid
    //the signature is not accordance with address
    @org.junit.Test
    public void test5() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test5Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.inCuUTXOpool(invalidTx[0]));
        assertFalse(txHandler.signIsTrue(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.sumOfOutput(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test6 (3) UTXO is claimed multiple times by {@code tx}
    //double spending in the same transaction.
    @org.junit.Test
    public void test6() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test6Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.signIsTrue(invalidTx[0]));
        assertFalse(txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.sumOfOutput(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test7 (4) Output values are negative
    @org.junit.Test
    public void test7() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test7Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];        //Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test1
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.signIsTrue(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertFalse(txHandler.nonNegative(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.sumOfOutput(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test8 (5) the sum of {@code tx}s input values is not greater than or equal to the sum of its output values; and false otherwise.
    @org.junit.Test
    public void test8() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test8Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] invalidTx = new Transaction[1];        //Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test1
        invalidTx[0] = newTx[newTx.length-1];
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result1 = txHandler.handleTxs(validTx);
        assertEquals(result1,validTx);
        assertFalse(txHandler.isValidTx(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.inCuUTXOpool(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.signIsTrue(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.notMulti(invalidTx[0]));
        assertTrue("this tx is invalid",txHandler.nonNegative(invalidTx[0]));
        assertFalse(txHandler.sumOfOutput(invalidTx[0]));
        Transaction[] result = txHandler.handleTxs(invalidTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);


    }

    //test9 test1-test8 test the situation that transactions are independent and all the inputs are outputs from tx that already be confirmed.
    // This test will test some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet.
    // ALL txs are valid
    @org.junit.Test
    public void test9() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test9Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] validTx = new Transaction[newTx.length-1];
        for(int i = 0; i< newTx.length-1; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        assertFalse(txHandler.isValidTx(newTx[newTx.length-1]));
        assertFalse("this tx is invalid",txHandler.inCuUTXOpool(newTx[newTx.length-1]));
        Transaction[] result = txHandler.handleTxs(newTx);
        assertEquals(result, newTx);

    }

    //test10 test1-test8 test the situation that transactions are independent and all the inputs are outputs from tx that already be confirmed.
    // This test will test some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet.
    // Assume txA  depend on txB and txC, txB is valid but txC is not valid(Assume txB and txC have double spending, then txA is not valid)
    @org.junit.Test
    public void test10() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        createUTXOPoolAndTx create = createUTXOPoolAndTx.test10Generator(create0.inAndOutNumberOfNewTx, create0.newTx,create0.utxoPool,create0.users);
        TxHandler txHandler = new TxHandler(create.utxoPool);
        Transaction[] newTx = create.newTx;
        Transaction[] validTx = new Transaction[newTx.length-2];
        for(int i = 0; i< newTx.length-2; i++){
            validTx[i] = newTx[i];
        }
        for (Transaction tx: validTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        assertFalse(txHandler.isValidTx(newTx[newTx.length-1]));
        assertFalse(txHandler.isValidTx(newTx[newTx.length-2]));
        Transaction[] result1 = txHandler.handleTxs(newTx);
        assertEquals(result1, validTx);
        assertFalse(txHandler.isValidTx(newTx[newTx.length-1]));
        assertFalse(txHandler.isValidTx(newTx[newTx.length-2]));
        assertFalse("this tx is invalid",txHandler.inCuUTXOpool(newTx[newTx.length-1]));
        Transaction[] result = txHandler.handleTxs(newTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

    //test11 test1-test8 test the situation that transactions are independent and all the inputs are outputs from tx that already be confirmed.
    //test 9 and test10 test some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet.
    // This test will test the situation that the input tx[] is empty
    @org.junit.Test
    public void test11() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        createUTXOPoolAndTx create0 = createUTXOPoolAndTx.start();
        TxHandler txHandler = new TxHandler(create0.utxoPool);
        Transaction[] newTx = new Transaction[0];
        for (Transaction tx: newTx ) {
            assertTrue("this tx is invalid", txHandler.isValidTx(tx));
        }
        Transaction[] result = txHandler.handleTxs(newTx);
        Transaction[] testTx = new Transaction[0];
        assertEquals(result, testTx);

    }

}

