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


I test function  addBlock(Block block)（including hasPreBlockHashAndNotGenesis 
 heightRight and txsValid）addTransaction(Transaction tx) getMaxHeightBlock()，getMaxHeightUTXOPool() and clear(）respectively
 Purpose | Detail 
 ---- | -----  
  hasPreBlockHashAndNotGenesis | to verify block is not another genesis block and it has a parent block 
 heightRight  | to verify block should be at height > (maxHeight - CUT_OFF_AGE)
| to verify all transactions is valid


