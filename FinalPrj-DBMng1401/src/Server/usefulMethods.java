package Server;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * this class has some useful methods
 * @author ashkan_mogharab
 */
public class usefulMethods {
    /**
     * this method sends a message to its destination
     *
     * @param out    an output stream
     * @param string a message
     */
    public void send_message(OutputStream out, String string) {
        try {
            out.write(string.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * this method reads a message from its source
     *
     * @param in an input stream
     * @return message
     */
    public String read_message(InputStream in) {
        String message = null;
        try {
            byte[] buffer = new byte[2048];
            int read = in.read(buffer);
            new String(buffer, 0, read);
            message = new String(buffer, 0, read);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return message;
    }
    /**
     * this method checks that client wants to continue attempting or not
     * @return a string
     */
    public String continue_or_not() {
        String sel = "-1";
        while ((!(sel.equals("1"))) && (!(sel.equals("2")))) {
            Scanner input = new Scanner(System.in);
            sel = input.nextLine();
        }
        return sel;
    }
    /**
     * run a few statement that are duplicate in main code
     *
     * @param out    an output stream
     * @param in     an input stream
     * @param input  a scanner for use
     * @return a string or void
     */
    public String run_few_statement2(OutputStream out, InputStream in, Scanner input) {
        String select = "";
        while (true) {
            System.out.println(read_message(in) );
            select = input.nextLine();
            send_message(out, select);
            if (read_message(in).equals("true")) {
                select = "-1";
                break;
            } else {
                System.out.println(read_message(in));
                select =continue_or_not();
                send_message(out, select);
                if (select.equals("2")) {
                    break;
                }
            }
        }
        return select;
    }
    public void run_few_statement1(OutputStream out, InputStream in, Scanner input) {
        System.out.println(read_message(in));
        String select = input.nextLine();
        send_message(out, select);
    }
    /**
     * this method hashes a string to an array of bytes
     *
     * @param input an string
     * @return hash of the string
     */
    public byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param hash input of toHexString method with array of bytes form
     * @return hex string of password
     */
    public String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
