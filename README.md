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
Also, I wirte a method updateUTXOPool to update UTXOPool. After finding every valid transaction, this method will be used once. <br>


## Test<br>

### Create Data<br>

I write createUTXOPoolAndTx class to create data for test. I firstly generate data to create valid trasactions and UTXOPool then change some transaction to make it has error to prepare data for tests.<br>

1. First, I write some methods to create transaction.<br>
  • sign: To get signature<br> <br> 
  • createUsersKeyPair: To get public key and private key for users<br>
  • getOutput: To generate outputs. (Since Transaction.Output is not static, Cannot generate output randomly. Need to use this function to create a fictitious old transaction(those outputs are actually not in one transaction,but here we assume they are in one transaction in this method)<br>
  • createNewUTXOPool: To generate UTXOPool according to outputs<br>
  • newTx: To generate new transactions<br>

2. Second, I generate data and use function in 1 to generate valid trasactions and UTXOPool by using method start(). And the following are the explaination of variables. Those variables can be changed in method start(), and then transactions and UTXOPool can be generated<br> <br> 
  • int totalOutputNumber: The size of all outputs of txs that are already confirmed.<br>
  • int usersNumber: Users in these txs (including old txs and new txs)<br>
  • ArrayList<KeyPair> users: Keys for users(created by createUTXOPoolAndTx.createUsersKeyPair)<br>
  • int oldTxNumber: The number of txs that are already confirmed<br>
  • int maxvalue: The max value of an output<br>
  • int newTxNumber: The number of new txs<br>
  • HashMap<Integer, int[]> inAndOutNumberOfNewTx: the integer is the index of new tx, and for every int[], the int[0] is number of input of the new tx,int[1] is number of output of the new tx(the size of the first dimension is the number of new txs(newTxNumber), it should be larger than 3. The sum of all input of new tx can not be larger than totalOutputNumber)<br>

3.Third, I create method createUTXOPoolAndTx.test1Generator ~ createUTXOPoolAndTx.test10Generator to change some transaction to make it has error and prepare data for tests.<br>    

### Test Meathod<br>

I create before() and test1() to test11() to do test. <br>
  • before() is used to generate txs without error and test valid txs(do not depend on each) can be judged to valid(isValid == True). <br>
  • test1()~test10() use createUTXOPoolAndTx.test1Generator ~ createUTXOPoolAndTx.test10Generator separately to get data<br>
  • test1() ~ test8() test isValid() method<br>
  • test 9() and test10() test handleTxs() method: some valid txs that depend on each other, which means that their inputs may be output of txs which may not be confirmed yet while test1() ~ test8() test transactions which are independent and all the inputs are outputs from tx that already be confirmed.<br>
  • test1() will test the situation that the input tx[] is empty.<br>
More information is in the table(the result in the table is just for  the transaction with error, other valid tx is still valid).<br>

 Method Name | Purpose | Content  | Important Result(only the transaction with error)
 ---- | ----- | ------ | ------ 
 test1  | Test requirement (1) in isValid|Assume that outputs not in the current UTXO pool since corresponding index in UTXO Pool is different| isValidTx:False<br>inCuUTXOpool:False<br>notMulti:True<br> nonNegative:True<br>cannot return by handleTxs 
 test2  | Test requirement (1) in isValid |Assume that outputs not in the current UTXO pool since corresponding preTxHash in UTXO Pool is different| isValidTx:False<br>inCuUTXOpool:False<br>notMulti:True<br> nonNegative:True<br>cannot return by handleTxs 
 test3  | Test requirement (1) in isValid |Double spending in different transaction can lend to the result that do not satisfies (1). PreTxHash/Index or both of them are not in the current UTXP pool | isValidTx:False<br>inCuUTXOpool:False<br>notMulti:True<br> nonNegative:True<br>cannot return by handleTxs 
 test4  | Test requirement (2) in isValid |The signature is not accordance with message| isValidTx:False<br>inCuUTXOpool:True<br>signIsTrue:False<br>notMulti:True<br> nonNegative:True<br>sumOfOutput:True<br>cannot return by handleTxs 
 test5  | Test requirement (2) in isValid |The signature is not accordance with address | isValidTx:False<br>inCuUTXOpool:Truee<br>signIsTrue:False<br>notMulti:True<br> nonNegative:True<br>sumOfOutput:True<br>cannot return by handleTxs 
 test6  | Test requirement (3) in isValid |Double spending in the same transaction.| isValidTx:False<br>inCuUTXOpool:True<br>signIsTrue:True<br>notMulti:False<br> nonNegative:True<br>sumOfOutput:True<br>cannot return by handleTxs 
 test7  | Test requirement (4) in isValid |Output values are negative| isValidTx:False<br>inCuUTXOpool:True<br>signIsTrue:True<br>notMulti:True<br> nonNegative:False<br>sumOfOutput:True<br>cannot return by handleTxs  
 test8  | Test requirement (5) in isValid |The sum of input values is not greater than or equal to the sum of its output values | isValidTx:False<br>inCuUTXOpool:True<br>signIsTrue:True<br>notMulti:True<br> nonNegative:True<br>sumOfOutput:False<br>cannot return by handleTxs 
 test9  | Test handleTxs | Some txs that depend on each other and all txs are valid | return by handleTxs 
 test10  | Test handleTxs | Some txs that depend on each other. Assume txA  depend on txB and txC, txB is valid but txC is not valid(Assume txB and txC have double spending) | txA and tx C cannot return by handleTxs
 test11  | Test empty input | the input tx[] is empty | handleTxs result is tx[]

PS:Since signature is true and sum of output is smaller than sum of input need to find output in UTXO pool,the realization depend on output in UTXOPool(1), we do not test signIsTrue and nonNegative in test1 ~ test3




