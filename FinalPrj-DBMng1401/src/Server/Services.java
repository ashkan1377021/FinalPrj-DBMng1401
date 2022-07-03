package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * this class has methods that each provide services
 *
 * @author ashkan_mogharab
 */
public class Services {
    //a socket for connecting  to user
    private final Socket connectionSocket;
    //an object of usefulMethods
    private final usefulMethods usefulmethods = new usefulMethods();
    //ID of the user who wants to use from services
    private final String ID;
    // an output stream
    private OutputStream out;
    // an input stream
    private InputStream in;
    // select of  user
    private String select;
    // shows that user deleted account or not
    private int enable;

    /**
     * creates a new services
     *
     * @param ID               of user
     * @param connectionSocket a socket for connecting  to user
     **/
    public Services(String ID, Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.ID = ID;
        this.enable = 1;
        act();
    }

    /**
     * this method handles the works that should be done Services
     */
    private void act() {
        try {
            out = connectionSocket.getOutputStream();
            in = connectionSocket.getInputStream();
            while (true) {
                usefulmethods.send_message(out,
                        "Welcome to services" + "\n" + "1:block" + '\n' + "2:unblock" + '\n' +
                        "3:request friendship" + '\n' + "4:requests to me" + '\n' +
                        "5:accept or ignore requests" + '\n' + "6:show friends"+ '\n' +
                        "7:remove friend"+ '\n' + "8:send message" + '\n' + "9:show received messages" +
                        '\n' + "10:like message" +  '\n' + "11:search ID of users" +'\n' + "12:delete account" +
                        '\n' + "13:back");
                select = usefulmethods.read_message(in);
                if (select.equals("1") || select.equals("2")) {
                    block_unblock();
                } else if (select.equals("3")) {
                    request_friendship();
                } else if (select.equals("4")) {
                    requests_to_me();
                } else if (select.equals("5")) {
                    accept_or_ignore_requests();
                } else if (select.equals("6")) {
                    show_friends();
                }
                else if (select.equals("7")) {
                   remove_friend();
                }
                else if (select.equals("8")) {
                    send_message();
                }
                else if (select.equals("9")) {
                   show_received_messages();
                }
                else if (select.equals("10")) {
                   like_message();
                }
                else if (select.equals("11")) {
                   search();
                }
                else if (select.equals("12")) {
                    delete_account();
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
    private void block_unblock() {
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
            String blocked_unblocked;
            int flag = 0;
            while (flag == 0) {
                if (select.equals("1")) {
                    usefulmethods.send_message(out, "ID of user you want to block: ");
                } else {
                    usefulmethods.send_message(out, "ID of user you want to unblock: ");
                }
                blocked_unblocked = usefulmethods.read_message(in);
                if (input_exists(blocked_unblocked) && !blocked_unblocked.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    String st = "select * from block where blocker = ? and blocked = ? ";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ps.setString(2, blocked_unblocked);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        if (select.equals("1")) {
                            usefulmethods.send_message(out, "you blocked this user previously");
                        } else {
                            st = "delete from block where blocker = ? and blocked = ? ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, ID);
                            ps.setString(2, blocked_unblocked);
                            ps.execute();
                            usefulmethods.send_message(out, "unblocking successfully done");
                        }
                    } else {
                        if (select.equals("1")) {
                            if (ID.equals(blocked_unblocked)) {
                                usefulmethods.send_message(out, "you can not block yourself!");
                            } else {
                                st = "insert into`messenger`.`block` (`blocker`, `blocked`) VALUES (?, ?);";
                                ps = connection.prepareStatement(st);
                                ps.setString(1, ID);
                                ps.setString(2, blocked_unblocked);
                                ps.execute();
                                usefulmethods.send_message(out, "blocking successfully done");
                            }
                        } else {
                            usefulmethods.send_message(out, "this user is not blocked");
                        }
                    }
                } else {
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, "There is no user with this username" + "\n" + "1:continue attempting" + "\n" + "2: back");
                    if (usefulmethods.read_message(in).equals("2")) {
                        break;
                    }
                }


            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * this method handles process of request friendship to a user
     */
    private void request_friendship() {
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
            String requested;
            int flag = 0;
            while (flag == 0) {
                usefulmethods.send_message(out, "ID of user you want to request: ");
                requested = usefulmethods.read_message(in);
                if (input_exists(requested) && !requested.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    String st = "select * from friendship where accepted = ? and acceptor = ? ";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ps.setString(2, requested);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        usefulmethods.send_message(out, "you are a friend of this user.don't need to request friendship");
                    } else {
                        if (ID.equals(requested)) {
                            usefulmethods.send_message(out, "you can not request friendship to yourself!");
                        } else {
                            st = "select * from block where blocker = ? and blocked = ? ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, requested);
                            ps.setString(2, ID);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                usefulmethods.send_message(out, "you are blocked and can not request friendship to this user");
                            } else {
                                st = "select * from request where requestor = ? and requested = ? ";
                                ps = connection.prepareStatement(st);
                                ps.setString(1, ID);
                                ps.setString(2, requested);
                                rs = ps.executeQuery();
                                if (rs.next()) {
                                    usefulmethods.send_message(out, "you requested friendship to this user previously!");
                                } else {
                                    st = "insert into`messenger`.`request` (`requestor`, `requested`) VALUES (?, ?);";
                                    ps = connection.prepareStatement(st);
                                    ps.setString(1, ID);
                                    ps.setString(2, requested);
                                    ps.execute();
                                    usefulmethods.send_message(out, "request friendship successfully done");
                                }
                            }
                        }
                    }
                } else {
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, "There is no user with this username" + "\n" + "1:continue attempting" + "\n" + "2: back");
                    if (usefulmethods.read_message(in).equals("2")) {
                        break;
                    }
                }


            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * this method handles process of show requests made to user
     */
    private void requests_to_me() {
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
            String st = "select requestor from request where  requested = ? ";
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            ArrayList<String> requestors = new ArrayList<>();
            while (rs.next()) {
                requestors.add(rs.getString("requestor"));
            }
            usefulmethods.send_message(out, "" + requestors.size());
            for (String requestor : requestors) {
                usefulmethods.send_message(out, requestor);
                Thread.sleep(1);
            }
        } catch (SQLException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of accept or ignore requests made to user
     */
     private void accept_or_ignore_requests() {
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
             int flag = 0;
             String judged;
             String choice;
             while (flag == 0) {
                 usefulmethods.send_message(out, "ID of user you want to accept or ignore its request: ");
                  judged = usefulmethods.read_message(in);
                 if (input_exists(judged) && !judged.toLowerCase().endsWith("deleted")) {
                     flag = 1;
                     usefulmethods.send_message(out, "true");
                 }
                 if (flag == 1) {
                     usefulmethods.send_message(out, "your choice?" +'\n'+ "1:accept" +'\n' + "2:ignore");
                     choice = usefulmethods.read_message(in);
                     String st = "select requestor from request where requested = ? and requestor = ?";
                     PreparedStatement ps = connection.prepareStatement(st);
                     ps.setString(1, ID);
                     ps.setString(2, judged);
                     ResultSet rs = ps.executeQuery();
                     if (!rs.next()) {
                         usefulmethods.send_message(out, "this user didn't request to you any way");
                     } else {
                             st = "delete from request where requested = ? and requestor =? ";
                             ps = connection.prepareStatement(st);
                             ps.setString(1, ID);
                             ps.setString(2,judged);
                             ps.execute();
                       if(choice.equals("1")){
                           st = "insert into friendship values (?,?);";
                           ps = connection.prepareStatement(st);
                           ps.setString(1, judged);
                           ps.setString(2,ID);
                           ps.execute();
                           usefulmethods.send_message(out, "accept this request successfully done");
                       }
                       else{
                           usefulmethods.send_message(out, "ignore this request successfully done");
                       }
                     }
                 } else {
                     usefulmethods.send_message(out, "false");
                     usefulmethods.send_message(out, "There is no user with this username" + "\n" + "1:continue attempting" + "\n" + "2: back");
                     if (usefulmethods.read_message(in).equals("2")) {
                         break;
                     }
                 }
             }
         }
         catch (SQLException e1) {
             e1.printStackTrace();
         }
     }
    /**
     * this method handles process of show friends of user
     */
    private void show_friends() {
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
            String st = "select accepted from friendship where acceptor= ?";
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            ArrayList<String> friends = new ArrayList<>();
            while(rs.next()){
                friends.add(rs.getString("accepted"));
            }
            usefulmethods.send_message(out,"" +friends.size());
            for(String friend : friends){
                usefulmethods.send_message(out,friend);
                Thread.sleep(1);
            }
        }
        catch (SQLException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of remove a friend of user
     */
    private void remove_friend() {
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
            int flag = 0 ;
            String removed;
            while (flag == 0) {
                usefulmethods.send_message(out, "ID of friend you want to remove: ");
                removed= usefulmethods.read_message(in);
                if (input_exists(removed) && !removed.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    String st = "select accepted from friendship where accepted = ? and acceptor = ?";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, removed);
                    ps.setString(2, ID);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        usefulmethods.send_message(out, "this user isn't your friend");
                    } else {
                        st = "delete from friendship where accepted =? and  acceptor =? ";
                        ps = connection.prepareStatement(st);
                        ps.setString(1, removed);
                        ps.setString(2,ID);
                        ps.execute();
                            usefulmethods.send_message(out, "this user removed from your friends successfully");
                    }
                } else {
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, "There is no user with this username" + "\n" + "1:continue attempting" + "\n" + "2: back");
                    if (usefulmethods.read_message(in).equals("2")) {
                        break;
                    }
                }
            }
    }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of send message to a friend
     */
    private void send_message() {
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
            String receiver;
            String text;
            int flag = 0;
            while (flag == 0) {
                usefulmethods.send_message(out, "ID of friend you want to send message: ");
                receiver = usefulmethods.read_message(in);
                if (input_exists(receiver) && !receiver.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    usefulmethods.send_message(out, "text:");
                    text = usefulmethods.read_message(in);
                    String st = "select * from friendship where accepted = ? and acceptor = ? ";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ps.setString(2, receiver);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        usefulmethods.send_message(out, "you are not a friend of this user and can not send message");
                    } else {
                        if (ID.equals(receiver)) {
                            usefulmethods.send_message(out, "you can not send message to yourself!");
                        } else {
                            st = "select * from block where blocker = ? and blocked = ? ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, receiver);
                            ps.setString(2, ID);
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                usefulmethods.send_message(out, "you are blocked and can not send message to this user");
                            } else {
                                    st = "INSERT INTO `messenger`.`message` (`text`, `sender`, `receiver`, `send_time`, `status`) VALUES (?,?,?,?,?);";
                                    ps = connection.prepareStatement(st);
                                    ps.setString(1, text);
                                    ps.setString(2, ID);
                                    ps.setString(3, receiver);
                                    ps.setString(4, LocalDateTime.now().toString());
                                    ps.setString(5, "0");
                                    ps.execute();
                                    usefulmethods.send_message(out, "send message successfully done");
                            }
                        }
                    }
                } else {
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, "There is no user with this username" + "\n" + "1:continue attempting" + "\n" + "2: back");
                    if (usefulmethods.read_message(in).equals("2")) {
                        break;
                    }
                }


            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of show received messages  of user
     */
    private void show_received_messages() {
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
            String st = "select * from message where  receiver = ? order by send_time desc";
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
        ArrayList<String>messages = new ArrayList<>();
            while(rs.next()){
                int flag = 1;
                if( rs.getString("status").equals("0")){
                    String st2 = "update message set status = 1 where receiver = ? and ID = ?;";
                    PreparedStatement ps2= connection.prepareStatement(st2);
                    ps2.setString(1,ID);
                    ps2.setString(2,rs.getString("ID"));
                    ps2.execute();
                    flag = 0;
                }
                if(flag == 0){
                   messages.add( "ID: " + rs.getString("ID") +'\n' +rs.getString("send_time") +'\n' +  rs.getString("text")  + "  (unread)");
                }
                else{
                    messages.add("ID: " + rs.getString("ID") +'\n' +rs.getString("send_time") +'\n' + rs.getString("text"));
                }
            }
            usefulmethods.send_message(out,"" +messages.size());
            for(String message : messages){
                usefulmethods.send_message(out,message);
                Thread.sleep(1);
            }
        }
        catch (SQLException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of like a message by user
     */
    private void like_message() {
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
            String message_ID;
                usefulmethods.send_message(out, "ID of message you want to like: ");
                message_ID= usefulmethods.read_message(in);

                    String st = "select * from message where receiver = ? and ID = ?";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ps.setString(2, message_ID);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        usefulmethods.send_message(out, "you can only like received message!");
                    }
                    else{
                        st = "select * from liked where user_ID = ? and message_ID = ? ";
                        ps = connection.prepareStatement(st);
                        ps.setString(1, ID);
                        ps.setString(2, message_ID);
                        rs = ps.executeQuery();
                        if(rs.next()){
                            usefulmethods.send_message(out, "you liked this message previously");
                        }
                        else{
                            st = "insert into liked values (?,?) ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, ID);
                            ps.setString(2, message_ID);
                            ps.execute();
                            usefulmethods.send_message(out, "like this message successfully done");
                        }
                    }
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of search ides  near to user's word
     */
    private void search() {
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
            usefulmethods.send_message(out, "word: ");
            String word = usefulmethods.read_message(in);
            ResultSet rs = statement.executeQuery("select ID from user");
            ArrayList<String> nears = new ArrayList<>();
            while(rs.next()){
                String st = rs.getString("ID");
                if(st.startsWith(word) && !st.endsWith("deleted") ){
                    nears.add(st);
                }
            }
            usefulmethods.send_message(out,"" +nears.size());
            for(String near : nears){
                usefulmethods.send_message(out,near);
                Thread.sleep(1);
            }
        }
        catch (SQLException | InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * this method handles process of delete account of user
     */
    private void delete_account() {
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
            String st = "delete from block where  blocker = ? or blocked = ?";
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ps.setString(2, ID);
            ps.execute();
            st = "delete from friendship where  accepted = ? or acceptor = ?";
            ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ps.setString(2, ID);
            ps.execute();
            st = "delete from recoverpasswordlimitation where  recoverpasswordlimited= ? ";
            ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ps.execute();
            st = "delete from request where  requestor = ? or requested = ?";
            ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ps.setString(2, ID);
            ps.execute();
            st = "UPDATE `messenger`.`user` SET `phone` = ? WHERE (`ID` = ?)";
            ps = connection.prepareStatement(st);
            ps.setString(1,  " deleted");
            ps.setString(2, ID);
            ps.execute();
            st = "UPDATE `messenger`.`user` SET `email` = ? WHERE (`ID` = ?)";
            ps = connection.prepareStatement(st);
            ps.setString(1,  " deleted");
            ps.setString(2, ID);
            ps.execute();
            st = "UPDATE `messenger`.`user` SET `ID` = ? WHERE (`ID` = ?)";
            ps = connection.prepareStatement(st);
            ps.setString(1, ID + " deleted");
            ps.setString(2, ID);
            ps.execute();
            st = "update  log set enable = 0 where logged = ? and type = 'login' and enable ='1'";
            ps = connection.prepareStatement(st);
            ps.setString(1, ID + " deleted") ;
            ps.execute();
            enable = 0;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * this method checks  ID  which person inserted exists or not
     *
     * @param input which is checked that  exists or not
     * @return true if it exists otherwise false
     */
    private boolean input_exists(String input) {
        ResultSet rs;
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
            String st = "select * from user where ID =?";
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, input);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return false;
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