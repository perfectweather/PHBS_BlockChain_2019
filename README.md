# HOMEWORK1
Name: Zhou Yutian<br>
ID: 1801212996

## Homework Solution:<br>
1.  Use UTXOPool(UTXOPool uPool) to construct object TxHandle, which has a feature "private UTXOPool utxoPool".<br>
2.  Create five method to verify the five requirements when verify the transation.<br>

 Method Name | Function  | Result
 ---- | ----- | ------  
 inCuUTXOpool  | Verify (1) all outputs claimed by tx are in the current UTXO pool| True: satify (1)<br>False: not satify(1) 
 signIsTrue  | Verify (2) the signatures on each in put of tx are valid| True: satify (2)<br>False: not satify(2) 
 notMulti  | Verify (3) no UTXO is claimed multiple times by tx| True: satify (3)<br>False: not satify(3) 
 nonNegative  | Verify (4) all of txs output values are non-negative| True: satify (4)<br>False: not satify(4) 
 sumOfOutput  | Verify (5) the sum of txs input values is greater than or equal to the<br> sum of its output value| True: satify (5)<br>False: not satify(5) 
 isValidTx  | Verify (1) - (5) | True: five method above all return True<br>False: else


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






