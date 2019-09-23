# HOMEWORK1
Name: Zhou Yutian<br>
ID: 1801212996<br>

## Homework Solution:<br>

1.  Use UTXOPool(UTXOPool uPool) to construct object TxHandle, which has a feature "private UTXOPool utxoPool".<br>
2.  isValid: Create five method to verify the five requirements separately when verifying the transation.<br>
And the method isValidTx will return True if five methods all return True.<br>
This design will help to find which part goes wrong when doing the test.<br>

 Method Name | Function  | Result
 ---- | ----- | ------  
 inCuUTXOpool  | Verify (1) all outputs claimed by tx are in the current UTXO pool| True: satify (1)<br>False: not satify(1) 
 signIsTrue  | Verify (2) the signatures on each in put of tx are valid| True: satify (2)<br>False: not satify(2) 
 notMulti  | Verify (3) no UTXO is claimed multiple times by tx| True: satify (3)<br>False: not satify(3) 
 nonNegative  | Verify (4) all of txs output values are non-negative| True: satify (4)<br>False: not satify(4) 
 sumOfOutput  | Verify (5) the sum of txs input values is greater than or equal to th sum of its output value| True: satify (5)<br>False: not satify(5) 
 isValidTx  | Verify (1) - (5) | True: five method above all return True<br>False: else

3.  handleTxs: Since transactions may depend on each other, use "do - while" to judge whether transaction is vaild untill the number of "not valid transation" do not change after judge all "not valid transation".<br>
Also, I wirte a method updateUTXOPool to update UTXOPool. After finding every valid transaction, this method will be used once. 


## Test

### Create Data<br>

I write createUTXOPoolAndTx class to create data for test. I firstly generate data to create valid trasactions and then change some transaction to make it has error to prepare data for tests.<br>

1. First, I write some methods to create transaction.<br>
  •sign: To get signature<br>
  •createUsersKeyPair: To get public key and private key for users<br>
  •getOutput: To generate outputs. (Since Transaction.Output is not static, Cannot generate output randomly. Need to use this function to create a fictitious old transaction(those outputs are actually not in one transaction,but here we assume they are in one transaction in this method)<br>
  •createNewUTXOPool: To generate UTXOPool according to outputs<br>
  •newTx: To generate new transactions<br>

2. Second
2. Secon
     * fictitious transaction(those outputs are not in one transaction to get outputs,but here we assume they are in one transaction)
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


    

    //test1：(1).1, not all outputs claimed by {@code tx} are in the current UTXO pool.
    // Assume that outputs not in the current UTXO pool since corresponding index in UTXO Pool is different.
    public static createUTXOPoolAndTx test1Generator

    /**generate txs without errors by using method createUTXOPoolAndTx.createNewUTXOPool to create UTXO Pool
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

    

### Test Meathod<br>

before
miaoshu

Test1：<br>
Used for test the requirement (1) in isValid: (1) not all outputs claimed by are in the current UTXO pool.<br>
Assume that not all outputs not in the current UTXO pool since corresponding index in UTXO Pool is different.<br><br>
Result: <br>
Except the tx I change
txHandler.isValidTx(invalidTx[0]) is False
txHandler.isValidTx(inCuUTXOpool[0]) is False
txHandler.isValidTx(notMulti[0]) is True
txHandler.isValidTx(nonNegative[0]) is True






