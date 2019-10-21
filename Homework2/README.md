# HOMEWORK2
Name: Zhou Yutian<br>
ID: 1801212996<br>

## Homework Solution:<br>
1.  Create a subclass BlockChinaNode in BlockChina, which include block, height of block and the utxoPool of the block.<br>
2.  The attribution of BlockChina is CUT_OFF_AGE, highestNode, chain and transactionPool. Chain is a list consists of BlockChinaNode.<br>
3.  As for addBlock, the first step is to judge whether the block can be accepted. This part include three functions:<br>

Function | Purpose  
 ---- | -----  
  hasPreBlockHashAndNotGenesis | to verify block is not another genesis block and it has a parent block 
 heightRight  | to verify block should be at height > (maxHeight - CUT_OFF_AGE)
txsValid | to verify all transactions is valid

The second step is if the above three functions return true, then we need to update BlockChain: update transactionPool(remove txs in block from transactionPool)
, highestNode and china. To update chain, we need to create a BlockChinaNode for new block.<br>

And new block cannot use the UTXOPool of parent block directly, we need to add coinbase(change into utxo and output) of parent block.<br>
Also, highestNode need to be updated if adding block on the highest block.

4. Create a clear() function used to delete the blockNode whose block height >= highestNode.height-CUT_OFF_AGE to save the memory.

## Homework Test:<br>
I design tests to test function in BlockChina to keep BlockChinaHanlder can be executed correctly.<br>


I test function  addBlock（including hasPreBlockHashAndNotGenesis 
 heightRight and txsValid,）addTransaction, getMaxHeightBlock, getMaxHeightUTXOPool and clear respectively

<escape>  
<table>    
  <tr><th>Purpose</th><th>Detail</th><th>testName</th></tr>   
  <tr><td rowspan="8">test addBlock </td><td>adding a new block on the highest block (genesis block), and the highestNode should be unpdated</td><td>test4</td></tr>  
  <tr><td>adding a new block on genesis block(not the highest block), and the highestNode should not be unpdated</td><td>test5</td></tr>   
  <tr><td>test hasPreBlockHashAndNotGenesis by adding another genesis block</td><td>test6</td></tr> 
  <tr><td>test hasPreBlockHashAndNotGenesis by adding a block without pre block</td><td>test7</td></tr> 
  <tr><td>test heightRight by adding a new block on the block whose height <= than maxheight - cut off age) </td><td>test8</td></tr> 
  <tr><td>test txsValid by adding a new block whose transaction are not all valid</td><td>test9</td></tr> 
  <tr><td>test a coinbase transaction of a block is available to be spent in the next block mined on top of it by adding a block whose transaction use the output of coinbase in the pre block.</td><td>test10</td></tr> 
  <tr><td>adding a new block with transaction has been in other blocks(transaction are not in transactionPool)</td><td>test11</td></tr> 
  <tr><td>test addTransaction </td><td>add two transaction to transactionPool and test wheather tests are in transactionPool </td><td>test1</td></tr> 
  <tr><td>test getMaxHeightBlock </td><td>when blockchain only has genesisBlock return genesisblock</td><td>test2</td></tr> 
  <tr><td>test getMaxHeightUTXOPool </td><td>when blockchain only has genesisBlock return an empty UTXOPool</td><td>test3</td></tr> 
  <tr><td>test clear </td><td>when the CUT_OFF_AGE = 1 and the highest block height is 4 the blocks with height 2 should be delete </td><td>test12</td></tr> 
</table>   
</escape>  

