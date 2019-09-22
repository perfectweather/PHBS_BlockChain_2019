# HOMEWORK1
Name: Zhou Yutian<br>
ID: 1801212996

## Homework Solution

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






