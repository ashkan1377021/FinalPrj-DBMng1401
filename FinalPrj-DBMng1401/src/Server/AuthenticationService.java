package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * this class can sign up persons  and also can allow to previous users to sign in
 *
 * @author ashkan_mogharab
 */
public class AuthenticationService {
    //a socket for connecting  to user
    private final Socket connectionSocket;
    // an output stream
    private OutputStream out;
    // an input stream
    private InputStream in;
    //an object of usefulMethods
    private final usefulMethods usefulmethods = new usefulMethods();
    //ID of user who signs in
    private String ID;
    // signs user Quited or not
    private boolean Quit;

    /**
     * creates a new  authentication service
     *
     * @param connectionSocket a socket for connecting  to user
     */
    public AuthenticationService(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.ID = "";
        this.Quit = false;
        act();
    }

    /**
     * this method handles the works that should be done in authentication service
     */
    private void act() {
        label:
        while (true) {
            if (!ID.equals("")) {
                break;
            }
            String msg = "Welcome " + "\n" + "1:sign up" + '\n' + "2:Sign in" + '\n' + "3:recover password" + '\n' + "4:Quit";
            try {
                out = connectionSocket.getOutputStream();
                in = connectionSocket.getInputStream();
                usefulmethods.send_message(out, msg);
                msg = usefulmethods.read_message(in);
                switch (msg) {
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
                        usefulmethods.send_message(out, "Goodbye.coming back soon");
                        Quit = true;
                        break label;
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
            String firstname;
            String lastname;
            String phone;
            String email;
            String ID;
            String password;
            String answer;
            int flg = 0;
            int sign = 0;
            while (sign != 1) {
                String msg = "first name :";
                usefulmethods.send_message(out, msg);
                firstname = usefulmethods.read_message(in);
                usefulmethods.send_message(out, "last name :");
                lastname = usefulmethods.read_message(in);
                while (true) {
                    int flag = 0;
                    usefulmethods.send_message(out, "phone :");
                    phone = usefulmethods.read_message(in);
                    if (phone.charAt(0) == '0' && phone.charAt(1) == '9' && phone.length() == 11) {
                        flag = 1;
                        if (input_is_valid(phone, 1)) {
                            usefulmethods.send_message(out, "true");
                            break;
                        }
                    }
                    usefulmethods.send_message(out, "false");
                    if (flag == 0)
                        usefulmethods.send_message(out, ("This phone is invalid" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    else
                        usefulmethods.send_message(out, ("This phone is duplicate" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    if (usefulmethods.read_message(in).equals("2")) {
                        flg = 1;
                        break;
                    }
                }
                if (flg == 1) {
                    break;
                }
                while (true) {
                    usefulmethods.send_message(out, "email :");
                    email = usefulmethods.read_message(in) + "@gmail.com";
                    if (input_is_valid(email, 2)) {
                        usefulmethods.send_message(out, "true");
                        break;
                    }
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, ("This email is duplicate" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    if (usefulmethods.read_message(in).equals("2")) {
                        flg = 1;
                        break;
                    }
                }
                if (flg == 1) {
                    break;
                }
                while (true) {
                    int flag = 0;
                    usefulmethods.send_message(out, "ID :");
                    ID = usefulmethods.read_message(in);
                    if (ID.toLowerCase().endsWith("deleted")) {
                        flag = 1;
                    } else {
                        if (input_is_valid(ID, 3)) {
                            usefulmethods.send_message(out, "true");
                            break;
                        }
                    }
                    usefulmethods.send_message(out, "false");
                    if (flag == 0) {
                        usefulmethods.send_message(out, ("This ID is duplicate" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    } else {
                        usefulmethods.send_message(out, ("ID can not ends with deleted" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    }
                    if (usefulmethods.read_message(in).equals("2")) {
                        flg = 1;
                        break;
                    }
                }
                if (flg == 1) {
                    break;
                }
                while (true) {
                    usefulmethods.send_message(out, "password :");
                    password = usefulmethods.read_message(in);
                    if (check_password(password)) {
                        usefulmethods.send_message(out, "true");
                        password = usefulmethods.toHexString(usefulmethods.getSHA(password));
                        break;
                    }
                    usefulmethods.send_message(out, "false");
                    usefulmethods.send_message(out, ("password should be complex of letters and numbers" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                    if (usefulmethods.read_message(in).equals("2")) {
                        flg = 1;
                        break;
                    }
                }
                if (flg == 1) {
                    break;
                }
                usefulmethods.send_message(out, "what is your favorite color? ");
                answer = usefulmethods.toHexString(usefulmethods.getSHA(usefulmethods.read_message(in)));
                String st = "INSERT INTO user(ID, firstname, lastname, phone, email, password, answer) VALUES(?, ?, ? ,?, ?, ?,?)";
                PreparedStatement ps = connection.prepareStatement(st);
                ps.setString(1, ID);
                ps.setString(2, firstname);
                ps.setString(3, lastname);
                ps.setString(4, phone);
                ps.setString(5, email);
                ps.setString(6, password);
                ps.setString(7, answer);
                ps.execute();
                usefulmethods.send_message(out, ("Hi " + ID + ". your signup successfully done"));
                sign = 1;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    }

    /**
     * this method does sign in process
     */
    private void signIn() {
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
            String ID;
            String password;
            int flag = 0;
            while (flag == 0) {
                usefulmethods.send_message(out, "ID: ");
                ID = usefulmethods.read_message(in);
                if (!input_is_valid(ID, 3) && !ID.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    String st = "select * from  accesslimitation where  accesslimited = ? ";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ResultSet rs = ps.executeQuery();
                    int flg = 0;
                    int flg2 = 0;
                    LocalDateTime endTime = LocalDateTime.now();
                    if (!rs.next()) {
                        flg = 1;
                    } else {
                        endTime = LocalDateTime.parse(rs.getString("end_time"));
                        if (endTime.isBefore(LocalDateTime.now())) {
                            flg = 1;
                            st = "delete  from accesslimitation where  accesslimited = ? ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, ID);
                            ps.execute();
                        }
                    }
                    st = "select * from  log where logged = ? and type = 'login' and enable ='1'";
                    ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                     rs = ps.executeQuery();
                     if(!rs.next()){
                         flg2 = 1;
                     }
                    if (flg == 1 && flg2 == 1) {
                        usefulmethods.send_message(out, "true");
                        int count = 0;
                        do {
                            usefulmethods.send_message(out, "password :");
                            password = usefulmethods.toHexString(usefulmethods.getSHA(usefulmethods.read_message(in)));
                            if (check_sth_of_an_id(ID, password, 1)) {
                                usefulmethods.send_message(out, "true");
                                usefulmethods.send_message(out, "Hi " + ID + ". welcome to your account");
                                this.ID = ID;
                                st = "insert into `messenger`.`log` (`logged`, `arrival_time`,`type`) VALUES (?, ?,?);";
                                ps = connection.prepareStatement(st);
                                ps.setString(1, ID);
                                ps.setString(2, LocalDateTime.now().toString());
                                ps.setString(3, "login");
                                ps.execute();
                                break;
                            } else {
                                count++;
                                usefulmethods.send_message(out, "false");
                                st = "insert into `messenger`.`log` (`logged`, `arrival_time`,`type`) VALUES (?, ?,?);";
                                ps = connection.prepareStatement(st);
                                ps.setString(1, ID);
                                ps.setString(2, LocalDateTime.now().toString());
                                ps.setString(3, "wrong password");
                                ps.execute();
                                usefulmethods.send_message(out, "incorrect password!" + "\n" + "1:continue attempting" + "\n" + "2: back");
                                if (usefulmethods.read_message(in).equals("2")) {
                                    break;
                                } else {
                                    if (count == 3) {
                                        st = "insert into `messenger`.`accesslimitation` (`accesslimited`, `end_time`) VALUES (?, ?);";
                                        String temp;
                                        ps = connection.prepareStatement(st);
                                        ps.setString(1, ID);
                                        ps.setString(2, temp = LocalDateTime.now().plusDays(1).toString());
                                        ps.execute();
                                        usefulmethods.send_message(out, "too many attempts!" + " " + "access to your account limited until " + temp);
                                        break;
                                    }
                                }
                            }
                        } while (true);
                    } else {
                        if (flg == 0) {
                            usefulmethods.send_message(out, "access to your account is limited until " + endTime);
                        }
                        else{
                            usefulmethods.send_message(out, "you can not login more than once at a time");
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
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    }

    /**
     * this method does recover password process
     */
    private void recover_password() {
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
            String ID;
            String password;
            int flag = 0;
            while (flag == 0) {
                usefulmethods.send_message(out, "ID: ");
                ID = usefulmethods.read_message(in);
                if (!input_is_valid(ID, 3) && !ID.toLowerCase().endsWith("deleted")) {
                    flag = 1;
                    usefulmethods.send_message(out, "true");
                }
                if (flag == 1) {
                    String st = "select * from recoverpasswordlimitation where recoverpasswordlimited = ? ";
                    PreparedStatement ps = connection.prepareStatement(st);
                    ps.setString(1, ID);
                    ResultSet rs = ps.executeQuery();
                    int flg = 0;
                    LocalDateTime endTime = LocalDateTime.now();
                    if (!rs.next()) {
                        flg = 1;
                    } else {
                        endTime = LocalDateTime.parse(rs.getString("end_time"));
                        if (endTime.isBefore(LocalDateTime.now())) {
                            flg = 1;
                            st = "delete  from recoverpasswordlimitation where recoverpasswordlimited = ? ";
                            ps = connection.prepareStatement(st);
                            ps.setString(1, ID);
                            ps.execute();
                        }
                    }
                    if (flg == 1) {
                        usefulmethods.send_message(out, "true");
                        int count = 0;
                        do {
                            usefulmethods.send_message(out, "security Question : what is your favorite color ?");
                            String answer = usefulmethods.toHexString(usefulmethods.getSHA(usefulmethods.read_message(in)));
                            if (check_sth_of_an_id(ID, answer, 2)) {
                                usefulmethods.send_message(out, "true");
                                while (true) {
                                    usefulmethods.send_message(out, "new password :");
                                    password = usefulmethods.read_message(in);
                                    if (check_password(password)) {
                                        usefulmethods.send_message(out, "true");
                                        password = usefulmethods.toHexString(usefulmethods.getSHA(password));
                                        st = "UPDATE `messenger`.`user` SET `password` = ? WHERE (`ID` = ?);";
                                        ps = connection.prepareStatement(st);
                                        ps.setString(1, password);
                                        ps.setString(2, ID);
                                        ps.execute();
                                        st = "insert into `messenger`.`log` (`logged`, `arrival_time`,`type`) VALUES (?, ?,?);";
                                        ps = connection.prepareStatement(st);
                                        ps.setString(1, ID);
                                        ps.setString(2, LocalDateTime.now().toString());
                                        ps.setString(3, "change password");
                                        ps.execute();
                                        usefulmethods.send_message(out, "your password successfully changed");
                                        break;
                                    }
                                    usefulmethods.send_message(out, "false");
                                    usefulmethods.send_message(out, ("password should be complex of letters and numbers" + "\n" + "1:continue attempting" + "\n" + "2: back"));
                                    if (usefulmethods.read_message(in).equals("2")) {
                                        break;
                                    }
                                }
                                break;
                            } else {
                                count++;
                                usefulmethods.send_message(out, "false");
                                usefulmethods.send_message(out, "incorrect answer!" + "\n" + "1:continue attempting" + "\n" + "2: back");
                                if (usefulmethods.read_message(in).equals("2")) {
                                    break;
                                } else {
                                    if (count == 5) {
                                        st = "insert into`messenger`.`recoverpasswordlimitation` (`recoverpasswordlimited`, `end_time`) VALUES (?, ?);";
                                        ps = connection.prepareStatement(st);
                                        String temp;
                                        ps.setString(1, ID);
                                        ps.setString(2, temp = LocalDateTime.now().plusDays(1).toString());
                                        ps.execute();
                                        usefulmethods.send_message(out, "too many attempts!" + " " + "recover password for your account limited until " + temp);
                                        break;
                                    }
                                }
                            }
                        } while (true);
                    } else {
                        usefulmethods.send_message(out, "recovery password is limited until " + endTime);
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
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    }

    /**
     * this method checks  phone or email or ID  which person inserted is duplicate or not
     *
     * @param input which is checked that is duplicate or not
     * @return true if it is not  duplicate otherwise false
     */
    private boolean input_is_valid(String input, int flag) {
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
            String st;
            if (flag == 1) {
                st = "select * from user where phone =?";
                PreparedStatement ps = connection.prepareStatement(st);
                ps.setString(1, input);
                rs = ps.executeQuery();
            } else if (flag == 2) {
                st = "select * from user where email =?";
                PreparedStatement ps = connection.prepareStatement(st);
                ps.setString(1, input);
                rs = ps.executeQuery();
            } else {
                st = "select * from user where ID =?";
                PreparedStatement ps = connection.prepareStatement(st);
                ps.setString(1, input);
                rs = ps.executeQuery();
            }
            return !rs.next();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    /**
     * this method checks that password is complex of letters and numbers or not
     *
     * @param password which is checked that is in correct form or not
     * @return true if it is in correct form otherwise false
     */
    private boolean check_password(String password) {
        int count1 = 0;
        int count2 = 0;
        int i = 0;
        while (i < password.length()) {
            if (!Character.isAlphabetic(password.charAt(i)) && !Character.isDigit(password.charAt(i))) {
                return false;
            } else if (Character.isAlphabetic(password.charAt(i))) {
                count1++;
            } else if (Character.isDigit(password.charAt(i))) {
                count2++;
            }
            i++;
        }
        return count1 >= 1 && count2 >= 1;
    }

    /**
     * this method checks that a password or an answer of security Question is for an id or not
     *
     * @param ID    of user
     * @param input is evaluated that is for the id or not
     * @return true or false
     */
    private boolean check_sth_of_an_id(String ID, String input, int flag) {
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
            String st = "";
            if (flag == 1) {
                st = "select * from user where ID =? and password =?";
            } else {
                st = "select * from user where ID =? and answer =?";
            }
            PreparedStatement ps = connection.prepareStatement(st);
            ps.setString(1, ID);
            ps.setString(2, input);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    /**
     * get ID of user
     *
     * @return ID of user
     */
    public String getID() {
        return ID;
    }

    /**
     * getter
     * @return Quit
     */
    public boolean isQuit() {
        return Quit;
    }
}

