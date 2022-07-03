package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        try (ServerSocket welcomingSocket = new ServerSocket(7600)) {
            System.out.println("Server started");
            int count = 0;
            while (count != 20) {
                count++;
                Socket connectionSocket = welcomingSocket.accept();
                pool.execute(new ClientHandler(connectionSocket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    Socket connectionSocket;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try {
            String select;
            InputStream in = connectionSocket.getInputStream();
            OutputStream out = connectionSocket.getOutputStream();
            usefulMethods usefulmethods = new usefulMethods();
            String ID = "";
           do {
                if (ID.equals("")) {
                    AuthenticationService  authenticationService = new AuthenticationService(connectionSocket);
                    ID = authenticationService.getID();
                    if(authenticationService.isQuit()){
                        connectionSocket.close();
                        break;
                    }
                } else {
                    select = usefulmethods.read_message(in);
                    if (select.equals("1")) {
                        Services services = new Services(ID, connectionSocket);
                        if (services.getEnable() == 0) {
                            usefulmethods.send_message(out, "delete account successfully done:(");
                            AuthenticationService  authenticationService = new AuthenticationService(connectionSocket);
                            ID = authenticationService.getID();
                            if(authenticationService.isQuit()){
                                connectionSocket.close();
                                break;
                            }
                        }
                    } else {
                        try {
                            Class.forName("com.mysql.cj.jdbc.Driver");
                        } catch (ClassNotFoundException cnfe) {
                            System.out.println("Error loading driver");
                        }
                        try {
                            Connection connection = DriverManager.getConnection(
                                "jdbc:mysql://localhost"
                                ,
                                "ashkan1377", "aBc.123456");
                            Statement statement = connection.createStatement();
                            statement.execute("use messenger");
                            String st = "update  log set enable = 0 where logged = ? and type = 'login' and enable ='1'";
                            PreparedStatement ps = connection.prepareStatement(st);
                            ps.setString(1, ID);
                            ps.execute();
                            AuthenticationService authenticationService = new AuthenticationService(connectionSocket);
                            ID = authenticationService.getID();
                            if (authenticationService.isQuit()) {
                                connectionSocket.close();
                                break;
                            }
                        }
                        catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


