package Client;

import Server.usefulMethods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * this class can sign up / sign in a client
 *
 * @author ashkan_mogharab
 */
public class ClientAuthenticationService {
    // a socket
    Socket client;
    //an object of usefulMethods
    static usefulMethods usefulmethods;
    // an input stream
    private InputStream in;
    // an output stream
    private OutputStream out;
    // a scanner
    private Scanner input;
    // select of client
    private String select;
    // a flag for exit from while loop of act
    private int end;
    // signs user Quited or not
    private boolean Quit;

    /**
     * creates a new  client authentication service
     *
     * @param client a socket
     */
    public ClientAuthenticationService(Socket client) {
        this.client = client;
        this.end = 0;
        this.Quit = false ;
        act();
    }

    /**
     * this method handles the works that should be done in ClientAuthentication Service
     */
    private void act() {
        while (end!= 1 && !Quit) {
            try {
                usefulmethods = new usefulMethods();
                out = client.getOutputStream();
                in = client.getInputStream();
                input = new Scanner(System.in);
                System.out.println(usefulmethods.read_message(in));
                do {
                    select = input.nextLine();
                } while (!select.equals("1") && !select.equals("2") && !select.equals("3") && !select.equals("4"));
                usefulmethods.send_message(out, select);
                switch (select) {
                    case "1":
                        signUp();
                        break;
                    case "2":
                        signIn();
                        break;
                    case "3":
                        recover_password();
                        break;
                    default:
                        System.out.println(usefulmethods.read_message(in));
                        Quit = true;
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * this method does sign up process
     */
    private void signUp() {
        int sign = 0;
        while (sign == 0) {
            usefulmethods.run_few_statement1(out, in, input);
            usefulmethods.run_few_statement1(out, in, input);
            String flg1 = usefulmethods.run_few_statement2(out, in, input);
            if (flg1.equals("2")) {
                break;
            }
            String flg2 = usefulmethods.run_few_statement2(out, in, input);
            if (flg2.equals("2")) {
                break;
            }
            String flg3 = usefulmethods.run_few_statement2(out, in, input);
            if (flg3.equals("2")) {
                break;
            }
            String flg4 = usefulmethods.run_few_statement2(out, in, input);
            if (flg4.equals("2")) {
                break;
            }
            usefulmethods.run_few_statement1(out, in, input);
            System.out.println(usefulmethods.read_message(in));
            sign = 1;
        }
    }

    /**
     * this method does sign in process
     */
    private void signIn() {
        String flg1 = usefulmethods.run_few_statement2(out, in, input);
        if (!flg1.equals("2")) {
            String temp = usefulmethods.read_message(in);
            if (temp.equals("true")) {
                int count = 0;
                int flg = 0;
                while (true) {
                    System.out.println(usefulmethods.read_message(in));
                    select = input.nextLine();
                    usefulmethods.send_message(out, select);
                    if (usefulmethods.read_message(in).equals("true")) {
                        break;
                    } else {
                        count++;
                        System.out.println(usefulmethods.read_message(in));
                        select = usefulmethods.continue_or_not();
                        usefulmethods.send_message(out, select);
                        if (select.equals("2")) {
                            flg = 1;
                            break;
                        } else if (count == 3) {
                            System.out.println(usefulmethods.read_message(in));
                            flg = 1;
                            break;
                        }
                    }
                }
                if (flg == 0) {
                    System.out.println(usefulmethods.read_message(in));
                    end = 1;
                }
            } else {
                System.out.println(temp);
            }
        }
    }

    /**
     * this method does recover password process
     */
    private void recover_password() {
        String flg1 = usefulmethods.run_few_statement2(out, in, input);
        if (!flg1.equals("2")) {
            String temp = usefulmethods.read_message(in);
            if (temp.equals("true")) {
                int count = 0;
                while (true) {
                    System.out.println(usefulmethods.read_message(in));
                    select = input.nextLine();
                    usefulmethods.send_message(out, select);
                    if (usefulmethods.read_message(in).equals("true")) {
                        if (!usefulmethods.run_few_statement2(out, in, input).equals("2")) {
                            System.out.println(usefulmethods.read_message(in));
                        }
                        break;
                    } else {
                        count++;
                        System.out.println(usefulmethods.read_message(in));
                        select = usefulmethods.continue_or_not();
                        usefulmethods.send_message(out, select);
                        if (select.equals("2")) {
                            break;
                        } else if (count == 5) {
                            System.out.println(usefulmethods.read_message(in));
                            break;
                        }
                    }
                }
            } else {
                System.out.println(temp);
            }
        }
    }

    /**
     * getter
     * @return Quit
     */
    public boolean isQuit() {
        return Quit;
    }
}
