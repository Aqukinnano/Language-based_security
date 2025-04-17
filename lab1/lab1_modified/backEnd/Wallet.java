package backEnd;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class Wallet {
    /**
     * The RandomAccessFile of the wallet file
     */  
    private RandomAccessFile file;

    private FileChannel channel;

    /**
     * Creates a Wallet object
     *
     * A Wallet object interfaces with the wallet RandomAccessFile
     */
    public Wallet () throws Exception {
	this.file = new RandomAccessFile(new File("backEnd/wallet.txt"), "rw");
    this.channel = file.getChannel();
    }

    /**
     * Gets the wallet balance. 
     *
     * @return                   The content of the wallet file as an integer
     */
    public int getBalance() throws IOException {
        try (FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) { //shared lock
	        this.file.seek(0);
	        return Integer.parseInt(this.file.readLine());
        }    
    }

    /**
     * Sets a new balance in the wallet
     *
     * @param  newBalance          new balance to write in the wallet
     */
    private void setBalance(int newBalance) throws Exception {
	this.file.setLength(0);
	String str = Integer.valueOf(newBalance).toString()+'\n'; 
	this.file.writeBytes(str); 
    }


        /**
     * Safely withdraws money from the wallet (atomic check-and-withdraw)
     *
     * @param  valueToWithdraw     amount to withdraw
     * @return                     true if withdraw was successful, false if insufficient funds
     */
    public boolean safeWithdraw(int valueToWithdraw) throws Exception {
        // Get exclusive lock on the wallet file
        try (FileLock lock = channel.lock()) {
            int currentBalance = getBalance();
            if (currentBalance < valueToWithdraw) {
                return false;
            }

            System.out.println("\n[TOCTOU window] press Enter to continue...");
            System.in.read();  // system pause
            
            setBalance(currentBalance - valueToWithdraw);
            return true;
        }
    }

    /**
     * Closes the RandomAccessFile in this.file
     */
    public void close() throws Exception {
        this.channel.close();
	    this.file.close();
    }
}
