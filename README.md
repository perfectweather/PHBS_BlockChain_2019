# HOMEWORK1
Name: Zhou Yutian<br>
ID: 1801212996

## Homework Solution:<br>
1.  Use UTXOPool(UTXOPool uPool) to construct object TxHandle, which has a feature "private UTXOPool utxoPool".<br>
2.  Create five method to verify the five requirements when verify the transation.<br>

 Method  | Function  | Result
 ---- | ----- | ------  
 inCuUTXOpool  | verify (1) all outputs claimed by {@code tx} are in <br>the current UTXO pool| True: satify (1)<br>False: not satify(1) 
 单元格内容  | 单元格内容 | 单元格内容 
  UTXOPool utxoPool
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each in put of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
         //IMPLEMENT THIS
        if (inCuUTXOpool(tx) == true && signIsTrue(tx) == true && notMulti(tx) == true && nonNegative(tx) == true && sumOfOutput(tx) == true){
            return true;
        }
        return false;
    }



## Test



### Create Data<br>
### Test Meathod<br>
Test1：<br>
Used for test the requirement (1) in isValid: (1) not all outputs claimed by are in the current UTXO pool.<br>
Assume that not all outputs not in the current UTXO pool since corresponding index in UTXO Pool is different.<br><br>
Result: <br>
Except the tx I change
txHandler.isValidTx(invalidTx[0]) is False
txHandler.isValidTx(inCuUTXOpool[0]) is False
txHandler.isValidTx(notMulti[0]) is True
txHandler.isValidTx(nonNegative[0]) is True






