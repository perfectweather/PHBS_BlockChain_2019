import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
//import static java.nio.file.TempFileHelper.random;
//import org.junit.Assert;


public class createUTXOPoolAndTx {
    /**createUTXOPoolAndTx class is used to generate data to test, and those data will be tested in TxHandlerTest
     */
    public HashMap<Integer, int[]> inAndOutNumberOfNewTx;
    public UTXOPool utxoPool;
    public Transaction[] newTx;
    public ArrayList<KeyPair> users;

    public createUTXOPoolAndTx() {
        inAndOutNumberOfNewTx = new HashMap<Integer, int[]>();
        utxoPool = new UTXOPool();
        newTx = new Transaction[0];
        users = new ArrayList<KeyPair>();
    }

    public createUTXOPoolAndTx(UTXOPool utxoPool, Transaction[] newTx, HashMap<Integer, int[]> inAndOutNumberOfNewTx, ArrayList<KeyPair> users) {
        this.utxoPool = new UTXOPool(utxoPool);
        this.newTx = newTx;
        this.inAndOutNumberOfNewTx = new HashMap<Integer, int[]>(inAndOutNumberOfNewTx);
        this.users = new ArrayList<KeyPair>(users);

    }
    /**generate valid txs by using method createUTXOPoolAndTx.createNewUTXOPool to create UTXO Pool
     * and method createUTXOPoolAndTx.newTx to create txs.
     */
    public static createUTXOPoolAndTx start() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //totalOutputNumber: the size of all outputs of txs that are already confirmed.
        int totalOutputNumber = 30;
        //usersNumber:Users in these txs
        int usersNumber = 10;
        //create keys for users
        ArrayList<KeyPair> users = createUTXOPoolAndTx.createUsersKeyPair(usersNumber);
        //the maxvalue of an output
        int maxValue = 10;
        //the number of txs that are already confirmed
        int oldTxNumber = 5;
        //the number of new txs
        int newTxNumber = 6;
        //the first dimension is number of input of the new tx, the second dimension is the number of output of the new tx
        // the size of the first dimension is the number of new txs(newTxNumber), it should be larger than 3.
        //number is int[] in inAndOutNumberOfNewTx
        int[][] number = {{2, 3}, {2, 3}, {5, 7}, {6, 2}, {5, 4},{3,4}};
        //use utxo to find the owner of this input(output of old tx)
        HashMap<UTXO, Integer> UTXOFindOwners = new HashMap<UTXO, Integer>();
        //use output to find the owner of this output
        HashMap<Transaction.Output, Integer> findOwners = new HashMap<Transaction.Output, Integer>();
        //use utxo to find the hash of old tx
        HashMap<UTXO, byte[]> findOldTx = new HashMap<UTXO, byte[]>();
        //use utxo to find the hash of new tx
        HashMap<UTXO, Integer> findNewTx = new HashMap<UTXO, Integer>();
        //inAndOutNumberOfNewTx:the integer is the index of new tx, and the int[0] is number of input of the new tx,int[1] is number of output of the new tx
        //the sum of all input of new tx can not be larger than totalOutputNumber
        HashMap<Integer, int[]> inAndOutNumberOfNewTx = new HashMap<Integer, int[]>();
        for (int i = 0; i < newTxNumber; i++) {
            inAndOutNumberOfNewTx.put(i, number[i]);
        }
        UTXOPool utxoPool = createUTXOPoolAndTx.createNewUTXOPool(totalOutputNumber, users, maxValue, oldTxNumber, UTXOFindOwners, findOwners, findOldTx);
        TxHandler txHandler = new TxHandler(utxoPool);
        Transaction[] newTx = createUTXOPoolAndTx.newTx(inAndOutNumberOfNewTx, utxoPool, findNewTx, users, UTXOFindOwners);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    /**
     * Since Transaction.Output is not static, cannot generate output randomly.
     * Need to use this function to create a fictitious old transaction
     * (those outputs are actually not in one transaction,but here we assume they are in one transaction in this method)
     */
    private static Transaction getOutput(int totalOutputNumber, ArrayList<KeyPair> users, int maxValue, HashMap<Transaction.Output, Integer> findOwners) {
        Transaction tx = new Transaction();
        for (int i = 0; i < totalOutputNumber; i++) {
            Random random = new Random();
            int u = (int) (Math.random() * users.size());
            PublicKey address = users.get(u).getPublic();
            //int j = random.nextInt(oldTxNumber);
            //int u = random.nextInt(users);
            double v = Math.random() * maxValue + 0.1;
            tx.addOutput(v, address);
            findOwners.put(tx.getOutput(i), u);
        }
        tx.finalize();
        return tx;
    }

    // get UTXOpool
    public static UTXOPool createNewUTXOPool(int totalOutputNumber, ArrayList<KeyPair> users, int maxValue, int oldTxNumber, HashMap<UTXO, Integer> UTXOFindOwners, HashMap<Transaction.Output, Integer> findOwners, HashMap<UTXO, byte[]> findOldTx) {
        if (oldTxNumber > 10) {
            System.out.println("oldTxNumber cannot be greater than 10");
            assert false;
        }
        UTXOPool utxoPool = new UTXOPool();
        byte[][] txHash = new byte[oldTxNumber][256];
        for (int i = 0; i < oldTxNumber; i++) {
            java.util.Arrays.fill(txHash[i], (byte) i);
        }
        Transaction getOutput = getOutput(totalOutputNumber, users, maxValue, findOwners);
        for (int i = 0; i < totalOutputNumber; i++) {
            Random random = new Random();
            int j = (int) (Math.random() * oldTxNumber);
            UTXO utxo = new UTXO(txHash[j], i);
            findOldTx.put(utxo, txHash[j]);
            int u = findOwners.get(getOutput.getOutput(i));
            UTXOFindOwners.put(utxo, u);
            utxoPool.addUTXO(utxo, getOutput.getOutput(i));
        }
        return utxoPool;
    }

    //get newTx
    public static Transaction[] newTx(HashMap<Integer, int[]> inAndOutNumberOfNewTx, UTXOPool utxoPool, HashMap<UTXO, Integer> findNewTx, ArrayList<KeyPair> users, HashMap<UTXO, Integer> UTXOFindOwners) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ArrayList<Transaction> newTx = new ArrayList<Transaction>();
        ArrayList<UTXO> UTXOs = utxoPool.getAllUTXO();
        for (int i = 0; i < inAndOutNumberOfNewTx.size(); i++) {
            double sumValueOfInput = 0.0;
            double v = 0.0;
            Transaction tempNewTx = new Transaction();
            //int key = i + 1;
            int[] number = inAndOutNumberOfNewTx.get(i);
            ArrayList UTXOOfNewTxi = new ArrayList<UTXO>();
            for (int j = 0; j < number[0]; j++) {
                Random random = new Random();
                int k = (int) (Math.random() * UTXOs.size());
                UTXO tempUtxo = UTXOs.get(k);
                findNewTx.put(tempUtxo, i);
                tempNewTx.addInput(tempUtxo.getTxHash(), tempUtxo.getIndex());
                UTXOOfNewTxi.add(tempUtxo);
                //if(j < number[0]-1) {
                sumValueOfInput += utxoPool.getTxOutput(tempUtxo).value;
                UTXOs.remove(k);
            }
            for (int j = 0; j < number[1]; j++) {
                Random random = new Random();
                int u = (int) (Math.random() * users.size());
                PublicKey address = users.get(u).getPublic();
                //int j = random.nextInt(oldTxNumber);
                //int u = random.nextInt(users);
                //double v = sumValueOfInput / number[1];
                v = sumValueOfInput / (2 * number[1]);
                tempNewTx.addOutput(v, address);
            }
            for (int j = 0; j < number[0]; j++) {
                byte[] rawData = tempNewTx.getRawDataToSign(j);
                PrivateKey privateKey = users.get(UTXOFindOwners.get(UTXOOfNewTxi.get(j))).getPrivate();
                tempNewTx.addSignature(sign(privateKey, rawData), j);
            }
            tempNewTx.finalize();
            newTx.add(tempNewTx);
        }
        return newTx.toArray(new Transaction[newTx.size()]);
    }

    //createUsersKeyPair(public key and private key)
    public static ArrayList<KeyPair> createUsersKeyPair(int usersNumber) throws NoSuchAlgorithmException {
        ArrayList<KeyPair> users = new ArrayList<KeyPair>();
        for (int i = 0; i < usersNumber; i++) {
            users.add(KeyPairGenerator.getInstance("RSA").generateKeyPair());
        }
        return new ArrayList<KeyPair>(users);
    }

    //get signature
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
    public static createUTXOPoolAndTx test1Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        newTx[txNumber].removeInput(number[0] - 1);
        newTx[txNumber].addInput(input.prevTxHash, input.outputIndex + utxoPool.getAllUTXO().size());
        byte[] sign = input.signature;
        newTx[txNumber].addSignature(sign, number[0] - 1);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test2：(1).2, not all outputs claimed by {@code tx} are in the current UTXO pool.
    // Assume that outputs not in the current UTXO pool since corresponding preTxHash in UTXO Pool is different.
    public static createUTXOPoolAndTx test2Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        newTx[txNumber].removeInput(number[0] - 1);
        byte[] txHash = new byte[250];
        java.util.Arrays.fill(txHash, (byte) 1);
        newTx[txNumber].addInput(txHash, input.outputIndex);
        byte[] sign = input.signature;
        newTx[txNumber].addSignature(sign, number[0] - 1);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test3 (1).3 not all outputs claimed by {@code tx} are in the current UTXO pool.
    // double spending in different transaction can lend to the result that do not satisfies (1). PreTxHash/ Index or both of them are not in the current UTXP pool.
    public static createUTXOPoolAndTx test3Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int txNumber = inAndOutNumberOfNewTx.size() - 2;
        int txNumber2 = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        int[] number2 = inAndOutNumberOfNewTx.get(txNumber2);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        newTx[txNumber2].addInput(input.prevTxHash, input.outputIndex);
        newTx[txNumber2].addSignature(input.signature, number2[0]);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test4 (2).1 the signatures on each in put of {@code tx} are invalid
    //the signature is not accordance with message
    public static createUTXOPoolAndTx test4Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        Transaction.Output output = newTx[txNumber].getOutput(number[1] - 1);
        newTx[txNumber].addOutput(0, output.address);
        byte[] signature = input.signature;
        newTx[txNumber].addSignature(signature, number[0] - 1);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test5 (2).2 the signatures on each in put of {@code tx} are invalid
    //the signature is not accordance with address
    public static createUTXOPoolAndTx test5Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        Transaction.Output output = newTx[txNumber].getOutput(number[1] - 1);
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Transaction tx = new Transaction();
        tx.addOutput(output.value, keyPair.getPublic());
        byte[] signature = input.signature;
        newTx[txNumber].addSignature(signature, number[0] - 1);
        utxoPool.removeUTXO(utxo);
        utxoPool.addUTXO(utxo, tx.getOutput(0));
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test6 (3) UTXO is claimed multiple times by {@code tx}
    //double spending in the same transaction.
    public static createUTXOPoolAndTx test6Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Input input = newTx[txNumber].getInput(number[0] - 1);
        newTx[txNumber].addInput(input.prevTxHash, input.outputIndex);
        byte[] signature = input.signature;
        newTx[txNumber].addSignature(signature, number[0]);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test7 (4)output values are negative
    public static createUTXOPoolAndTx test7Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        Transaction.Output output = newTx[txNumber].getOutput(number[1] - 1);
        newTx[txNumber].addOutput(-1, output.address);
        PrivateKey privateKey = null;
        for (int i = 0; i < number[0]; i++) {
            Transaction.Input input = newTx[txNumber].getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output1 = utxoPool.getTxOutput(utxo);
            for (KeyPair keyPair : users) {
                if (keyPair.getPublic().equals(output1.address)) {
                    privateKey = keyPair.getPrivate();
                    break;
                }

            }
            byte[] rawData = newTx[txNumber].getRawDataToSign(i);
            byte[] signature = sign(privateKey, rawData);
            newTx[txNumber].addSignature(signature, i);

        }
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test8 (5) the sum of {@code tx}s input values is not greater than or equal to the sum of its output values; and false otherwise.
    public static createUTXOPoolAndTx test8Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int txNumber = inAndOutNumberOfNewTx.size() - 1;
        int[] number = inAndOutNumberOfNewTx.get(txNumber);
        int sumOfIutputVal = 0;
        for (Transaction.Input input : newTx[txNumber].getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output oldOutput = utxoPool.getTxOutput(utxo);
            sumOfIutputVal += oldOutput.value;
        }
        Transaction.Output output = newTx[txNumber].getOutput(number[1] - 1);
        newTx[txNumber].addOutput(sumOfIutputVal + 1, output.address);
        PrivateKey privateKey = null;
        for (int i = 0; i < number[0]; i++) {
            Transaction.Input input = newTx[txNumber].getInput(i);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output output1 = utxoPool.getTxOutput(utxo);
            for (KeyPair keyPair : users) {
                if (keyPair.getPublic().equals(output1.address)) {
                    privateKey = keyPair.getPrivate();
                    break;
                }

            }
            byte[] rawData = newTx[txNumber].getRawDataToSign(i);
            byte[] signature = sign(privateKey, rawData);
            newTx[txNumber].addSignature(signature, i);

        }
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test9 test1-test8 test the situation that transactions are independent and all the inputs are outputs from tx that already be confirmed.
    // This test will test some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet.
    // ALL txs are valid
    public static createUTXOPoolAndTx test9Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int txNumber1 = inAndOutNumberOfNewTx.size() - 1;
        int txNumber2 = inAndOutNumberOfNewTx.size() - 2;
        int txNumber3 = inAndOutNumberOfNewTx.size() - 3;
        int[] number1 = inAndOutNumberOfNewTx.get(txNumber1);
        int[] number2 = inAndOutNumberOfNewTx.get(txNumber2);
        Transaction.Output output2 = newTx[txNumber2].getOutput(number2[1] - 1);
        int[] number3 = inAndOutNumberOfNewTx.get(txNumber3);
        Transaction.Output output3 = newTx[txNumber3].getOutput(number3[1] - 1);
        newTx[txNumber1].addInput(newTx[txNumber2].getHash(), number2[1] - 1);
        newTx[txNumber1].addInput(newTx[txNumber3].getHash(), number3[1] - 1);
        PrivateKey privateKey = null;
        for (KeyPair keyPair : users) {
            if (keyPair.getPublic().equals(output2.address)) {
                privateKey = keyPair.getPrivate();
                break;
            }
        }
        byte[] rawData2 = newTx[txNumber1].getRawDataToSign(number1[0]);
        byte[] signature2 = sign(privateKey, rawData2);
        newTx[txNumber1].addSignature(signature2, number1[0]);
        for (KeyPair keyPair : users) {
            if (keyPair.getPublic().equals(output3.address)) {
                privateKey = keyPair.getPrivate();
                break;
            }
        }
        byte[] rawData3 = newTx[txNumber1].getRawDataToSign(number1[0] + 1);
        byte[] signature3 = sign(privateKey, rawData3);
        newTx[txNumber1].addSignature(signature3, number1[0] + 1);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }

    //test10 test1-test8 test the situation that transactions are independent and all the inputs are outputs from tx that already be confirmed.
    // This test will test some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet.
    // Assume txA  depend on txB and txC, txB is valid but txC is not valid(Assume txB and txC have double spending, then txA is not valid)
    public static createUTXOPoolAndTx test10Generator(HashMap<Integer, int[]> inAndOutNumberOfNewTx, Transaction[] newTx, UTXOPool utxoPool, ArrayList<KeyPair> users) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int txNumber1 = inAndOutNumberOfNewTx.size() - 1;
        int txNumber2 = inAndOutNumberOfNewTx.size() - 2;
        int txNumber3 = inAndOutNumberOfNewTx.size() - 3;
        int[] number1 = inAndOutNumberOfNewTx.get(txNumber1);
        int[] number2 = inAndOutNumberOfNewTx.get(txNumber2);
        Transaction.Output output2 = newTx[txNumber2].getOutput(number2[1] - 1);
        int[] number3 = inAndOutNumberOfNewTx.get(txNumber3);
        Transaction.Output output3 = newTx[txNumber3].getOutput(number3[1] - 1);
        newTx[txNumber1].addInput(newTx[txNumber2].getHash(), number2[1] - 1);
        newTx[txNumber1].addInput(newTx[txNumber3].getHash(), number3[1] - 1);
        Transaction.Input input = newTx[txNumber3].getInput(number3[0] - 1);
        newTx[txNumber2].addInput(input.prevTxHash, input.outputIndex);
        newTx[txNumber2].addSignature(input.signature, number2[0]);
        PrivateKey privateKey = null;
        for (KeyPair keyPair : users) {
            if (keyPair.getPublic().equals(output2.address)) {
                privateKey = keyPair.getPrivate();
                break;
            }
        }
        byte[] rawData2 = newTx[txNumber1].getRawDataToSign(number1[0]);
        byte[] signature2 = sign(privateKey, rawData2);
        newTx[txNumber1].addSignature(signature2, number1[0]);
        for (KeyPair keyPair : users) {
            if (keyPair.getPublic().equals(output3.address)) {
                privateKey = keyPair.getPrivate();
                break;
            }
        }
        byte[] rawData3 = newTx[txNumber1].getRawDataToSign(number1[0] + 1);
        byte[] signature3 = sign(privateKey, rawData3);
        newTx[txNumber1].addSignature(signature3, number1[0] + 1);
        createUTXOPoolAndTx create = new createUTXOPoolAndTx(utxoPool, newTx, inAndOutNumberOfNewTx, users);
        return create;
    }
}


