//import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;

public class TxHandler {

    private UTXOPool utxoPool;
    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        this.utxoPool = new UTXOPool(utxoPool);
    }

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

    /**
     * @param tx
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     */
    public boolean inCuUTXOpool(Transaction tx){
        for(Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (!this.utxoPool.contains(utxo))
                return false;
        }
        return true;
    }

    /**
     * @param tx
     * @return true if:
     * (2) the signatures on each in put of {@code tx} are valid,
     */
    public boolean signIsTrue(Transaction tx){
        int index = 0;
        for(Transaction.Input input : tx.getInputs()) {
            index = tx.getInputs().indexOf(input);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            //byte[] signature = input.signature;
            //byte[] txHash = tx.getRawDataToSign(index);
            Transaction.Output output = this.utxoPool.getTxOutput(utxo);
            boolean verifyResult = Crypto.verifySignature(output.address, tx.getRawDataToSign(index), input.signature);
            if (!verifyResult){
                return false;
            }
        }
        return true;
    }

    /**
     * @param tx
     * @return true if:
     * (3) no UTXO is claimed multiple times by {@code tx},
     */
    public boolean notMulti(Transaction tx){
        UTXOPool tempUTXOPool = new UTXOPool();
        for(Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (tempUTXOPool.contains(utxo))
                return false;
            tempUTXOPool.addUTXO(utxo,utxoPool.getTxOutput(utxo));
        }
        return true;
    }

    /**
     * @param tx
     * @return true if:
     * (4) all of {@code tx}s output values are non-negative, and
     */
    public static boolean nonNegative(Transaction tx){
        for (Transaction.Output output : tx.getOutputs()) {
            if (output.value < 0.0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param tx
     * @return true if:
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     * values; and false otherwise.
     */
    public boolean sumOfOutput(Transaction tx){
        double sumOfIutputVal = 0.0;
        double sumOfOutputVal = 0.0;
        for(Transaction.Input input : tx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output oldOutput = this.utxoPool.getTxOutput(utxo);
            sumOfIutputVal += oldOutput.value;
        }
        for (Transaction.Output output : tx.getOutputs()) {
            sumOfOutputVal += output.value;
        }
        if (sumOfIutputVal < sumOfOutputVal){
            return false;
        }
        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        ArrayList<Transaction> unJudgeTx = new ArrayList<Transaction>(Arrays.asList(possibleTxs));
        ArrayList<Transaction> validTx = new ArrayList<Transaction>();
        ArrayList<Transaction> newUnJudgeTx = new ArrayList<Transaction>();
        int unJudgeTxCount = 0;
        do {
            unJudgeTxCount = unJudgeTx.size();
            //System.out.println("unJudgeTxCount = " + unJudgeTxCount);
            for (Transaction tx : unJudgeTx) {
                if(isValidTx(tx)) {
                    validTx.add(tx);
                    updateUTXOPool(tx);
                }
                else{
                    newUnJudgeTx.add(tx);
                }

            }
            unJudgeTx = new ArrayList<Transaction>(newUnJudgeTx);
            newUnJudgeTx.clear();
        } while (unJudgeTxCount != unJudgeTx.size());
        return validTx.toArray(new Transaction[validTx.size()]);
    }

    private void updateUTXOPool(Transaction validTx){
        for(Transaction.Input input : validTx.getInputs()) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            this.utxoPool.removeUTXO(utxo);
        }

        byte[] txHash = validTx.getHash();
        int index = 0;
        for (Transaction.Output output : validTx.getOutputs()) {
            index = validTx.getOutputs().indexOf(output);
            UTXO utxo = new UTXO(txHash, index);
            this.utxoPool.addUTXO(utxo,output);
        }

        

    }

}
