package backEnd;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.io.FileReader;
import java.io.BufferedReader;

import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class Pocket {
    /**
     * The RandomAccessFile of the pocket file
     */
    private RandomAccessFile file;
    private FileChannel channel;

    /**
     * Creates a Pocket object
     * 
     * A Pocket object interfaces with the pocket RandomAccessFile.
     */
    public Pocket () throws Exception {
        this.file = new RandomAccessFile(new File("backEnd/pocket.txt"), "rw");
        this.channel = file.getChannel();
    }

    /**
     * Adds a product to the pocket. 
     *
     * @param  product           product name to add to the pocket (e.g. "car")
     */
    public void addProduct(String product) throws Exception {
        try (FileLock lock = channel.lock()) {//add filelock to the addProduct function
            this.file.seek(this.file.length());
            this.file.writeBytes(product+'\n'); 
        }
    }

    /**
     * Generates a string representation of the pocket
     *
     * @return a string representing the pocket
     */
    public String getPocket() throws Exception {
        try (FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {  // shared lock
            StringBuilder sb = new StringBuilder();
            this.file.seek(0);
            String line;
            while((line = this.file.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            return sb.toString();
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
