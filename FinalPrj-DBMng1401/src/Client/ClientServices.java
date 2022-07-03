package Client;

import Server.usefulMethods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * this class has Methods that make a connection to server and give services from it
 *
 * @author ashkan_mogharab
 */
public class ClientServices {
    // a scanner
    private final Scanner input = new Scanner(System.in);
    // a socket
    Socket client;
    //an object of usefulMethods
    private usefulMethods usefulmethods;
    // an input stream
    private InputStream in;
    // an output stream
    private OutputStream out;
    // select of user
    private String select;
    // shows that user deleted account or not
    private int enable;

    /**
     * creates a new client services
     *
     * @param client a socket
     */
    public ClientServices(Socket client) {
        this.client = client;
        this.enable = 1;
        act();
    }

    /**
     * this method handles the works that should be done in ClientServices
     */
    private void act() {
        usefulmethods = new usefulMethods();
        try {
            out = client.getOutputStream();
            in = client.getInputStream();
            while (true) {
                System.out.println(usefulmethods.read_message(in) + "");
                do {
                    select = input.nextLine();
                } while (!select.equals("1") && !select.equals("2") && !select.equals("3") && !select.equals("4") && !select.equals("5") && !select.equals("6") && !select.equals("7") && !select.equals("8")&&!select.equals("9")&&!select.equals("10") &&!select.equals("11")&&!select.equals("12") &&!select.equals("13"));
                usefulmethods.send_message(out, select);
                if (select.equals("1") || select.equals("2") || select.equals("3")) {
                    block_unblock_requestFriendship();
                } else if (select.equals("4") || select.equals("6") ||select.equals("9")) {
                    requests_to_me_or_friends_messages();
                } else if (select.equals("5")) {
                    accept_or_ignore_requests();
                }
                else if (select.equals("7")) {
                    remove_friend();
                }
                else if (select.equals("8")) {
                    send_message();
                }
                else if (select.equals("10")) {
                    usefulmethods.run_few_statement1(out,in,input);
                    System.out.println(usefulmethods.read_message(in));
                }
                else if (select.equals("11")) {
                   search();
                }
                else if (select.equals("12")) {
                    enable = 0;
                    break;
                } else
                    break;

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * this method handles process of block or unblock a user
     */
    private void block_unblock_requestFriendship() {
        String sel = usefulmethods.run_few_statement2(out, in, input);
        if (!sel.equals("2")) {
            System.out.println(usefulmethods.read_message(in));
        }
    }

    /**
     * this method handles process of show requests made to user or its friends or its messages
     */
    private void requests_to_me_or_friends_messages() {
        int count = Integer.parseInt(usefulmethods.read_message(in));
        for (int i = 0; i < count; i++)
            System.out.println(usefulmethods.read_message(in));
    }
    /**
     * this method handles process of accept or ignore requests made to user
     */
    private void accept_or_ignore_requests() {
        String sel = usefulmethods.run_few_statement2(out, in, input);
        if (!sel.equals("2")) {
            System.out.println(usefulmethods.read_message(in));
            String se;
            do {
                se = input.nextLine();
            } while (!se.equals("1") && !se.equals("2"));
            usefulmethods.send_message(out, se);
            System.out.println(usefulmethods.read_message(in));
        }
    }
    /**
     * this method handles process of remove a friend of user
     */
    private void remove_friend() {
        String sel = usefulmethods.run_few_statement2(out, in, input);
        if (!sel.equals("2")) {
            System.out.println(usefulmethods.read_message(in));
        }
    }
    /**
     * this method handles process of send message to a friend
     */
    private void send_message() {
        String sel = usefulmethods.run_few_statement2(out, in, input);
        if (!sel.equals("2")) {
            System.out.println(usefulmethods.read_message(in));
                String text = input.nextLine();
            usefulmethods.send_message(out, text);
            System.out.println(usefulmethods.read_message(in));
        }
    }
    /**
     * this method handles process of search ides  near to user's word
     */
    private void search() {
        usefulmethods.run_few_statement1(out,in,input);
        int count = Integer.parseInt(usefulmethods.read_message(in));
        for (int i = 0; i < count; i++)
            System.out.println(usefulmethods.read_message(in));
    }
    /**
     * getter
     *
     * @return enable
     */
    public int getEnable() {
        return enable;
    }
}


