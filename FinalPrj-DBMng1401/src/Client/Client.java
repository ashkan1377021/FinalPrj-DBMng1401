package Client;

import Server.usefulMethods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket client = new Socket("127.0.0.1", 7600)) {
            usefulMethods usefulmethods = new usefulMethods();
            OutputStream out = client.getOutputStream();
            InputStream in = client.getInputStream();
            Scanner input = new Scanner(System.in);
            String select;
            System.out.println("connecting ....");
            ClientAuthenticationService clientAuthenticationService = new ClientAuthenticationService(client);
            if(clientAuthenticationService.isQuit()){
                client.close();
                System.exit(0);
            }
            while (true) {
                System.out.println("commands:" + '\n' + "1:go to services" + '\n' + "2:log out");
                do {
                    select = input.nextLine();
                } while (!select.equals("1") && !select.equals("2"));
                usefulmethods.send_message(out, select);
                if (select.equals("1")) {
                    ClientServices clientservices = new ClientServices(client);
                    if (clientservices.getEnable() == 0) {
                        System.out.println(usefulmethods.read_message(in));
                        clientAuthenticationService =new ClientAuthenticationService(client);
                        if(clientAuthenticationService.isQuit()){
                            client.close();
                            break;
                        }
                    }
                } else {
                 clientAuthenticationService  = new ClientAuthenticationService(client);
                    if(clientAuthenticationService.isQuit()){
                        client.close();
                       break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
