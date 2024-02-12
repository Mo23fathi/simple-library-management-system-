package library.management.system;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
 
import javax.swing.*;
import net.proteanit.sql.DbUtils;
 
public class main {
     
    public static class ex{
        public static int days=0;
            }
 
    public static void main(String[] args) {
         
        login();
        //create();
    }
    public static void login() {
    // Create a JFrame for the login window
    JFrame f = new JFrame("Login");

    // Create labels for the username and password
    JLabel l1, l2;
    l1 = new JLabel("Username");
    l1.setBounds(30, 15, 100, 30);

    l2 = new JLabel("Password");
    l2.setBounds(30, 50, 100, 30);

    // Create text fields for username and password
    JTextField F_user = new JTextField();
    F_user.setBounds(110, 15, 200, 30);

    JPasswordField F_pass = new JPasswordField();
    F_pass.setBounds(110, 50, 200, 30);

    // Create a button for login
    JButton login_but = new JButton("Login");
    login_but.setBounds(130, 90, 80, 25);

    // Add action listener to the login button
    login_but.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Retrieve username and password entered by the user
            String username = F_user.getText();
            String password = new String(F_pass.getPassword());

            // Check if username or password is empty
            if (username.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter username");
            } else if (password.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter password");
            } else {
                // Connect to the database
                Connection connection = connect();

                try {
                    // Create a Statement with a scrollable result set
                    Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.executeUpdate("USE LIBRARY"); // Use the database with the name "Library"

                    // Execute a query to retrieve user information based on username and password
                    String st = "SELECT * FROM USERS WHERE USERNAME='" + username + "' AND PASSWORD='" + password + "'";
                    ResultSet rs = stmt.executeQuery(st);

                    // Check if the result set is empty (no matching user)
                    if (rs.next() == false) {
                        System.out.print("No user");
                        JOptionPane.showMessageDialog(null, "Wrong Username/Password!");
                    } else {
                        f.dispose();
                        rs.beforeFirst();

                        // Iterate through the result set
                        while (rs.next()) {
                            String admin = rs.getString("ADMIN"); // Check if the user is an admin
                            String UID = rs.getString("UID"); // Get user ID of the user

                            // Redirect to the admin menu or user menu based on the user's role
                            if (admin.equals("1")) {
                                admin_menu(); // Redirect to admin menu
                            } else {
                                user_menu(UID); // Redirect to user menu for that user ID
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    });

    // Add components to the JFrame
    f.add(F_pass);
    f.add(login_but);
    f.add(F_user);
    f.add(l1);
    f.add(l2);

    // Set JFrame properties
    f.setSize(400, 180);
    f.setLayout(null);
    f.setVisible(true);
    f.setLocationRelativeTo(null);
}

  public static Connection connect() {
    try {
        // Load the MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Loaded driver");

        // Establish a connection to the MySQL database
        // Replace "library" with the name of your database, "root" with the MySQL username,
        // and "Moh@23623@" with the password for the MySQL user
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Moh@23623@");
        System.out.println("Connected to MySQL");

        // Return the established connection
        return con;
    } catch (Exception ex) {
        // Handle any exceptions by printing the stack trace
        ex.printStackTrace();
    }

    // Return null if there is an exception or an error in connecting to the database
    return null;
}

    public static void create() {
    try {
        // Establish a connection to the database
        Connection connection = connect();
        
        // Retrieve the list of catalogs (databases) available in the connected database server
        ResultSet resultSet = connection.getMetaData().getCatalogs();

        // Iterate through each catalog in the ResultSet
        while (resultSet.next()) {
            // Get the name of the database at the current position in the ResultSet
            String databaseName = resultSet.getString(1);

            // Check if the database with the name "library" already exists
            if (databaseName.equals("library")) {
                // Drop the existing "library" database to reset the entire database
                Statement stmt = connection.createStatement();
                String sql = "DROP DATABASE library";
                stmt.executeUpdate(sql);
            }
        }

        // Create a new Statement for executing SQL queries
        Statement stmt = connection.createStatement();

        // Create a new database named "LIBRARY"
        String createDatabaseSql = "CREATE DATABASE LIBRARY";
        stmt.executeUpdate(createDatabaseSql);

        // Switch to using the "LIBRARY" database
        stmt.executeUpdate("USE LIBRARY");

        // Create a table named "USERS" with columns: UID, USERNAME, PASSWORD, ADMIN
        String createUserTableSql = "CREATE TABLE USERS(UID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, USERNAME VARCHAR(50) NOT NULL , PASSWORD VARCHAR(50) NOT NULL , ADMIN BOOLEAN DEFAULT FALSE )";
        stmt.executeUpdate(createUserTableSql);

        // Insert an initial user (admin) into the "USERS" table
        stmt.executeUpdate("INSERT INTO USERS(USERNAME, PASSWORD, ADMIN) VALUES('admin','admin',TRUE)");

        // Create a table named "BOOKS" with columns: BID, BNAME, GENRE, PRICE
        String createBooksTableSql = "CREATE TABLE BOOKS(BID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, BNAME VARCHAR(50) NOT NULL , GENRE VARCHAR(50)NOT NULL , PRICE INT NOT NULL )";
        stmt.executeUpdate(createBooksTableSql);

        // Create a table named "ISSUED" with columns: IID, UID, BID, ISSUED_DATE, RETURN_DATE, PERIOD, FINE
        String createIssuedTableSql = "CREATE TABLE ISSUED(IID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, UID INT NOT NULL , BID INT NOT NULL, ISSUED_DATE VARCHAR(20) NOT NULL , RETURN_DATE VARCHAR(20), PERIOD INT NOT NULL , FINE INT,FOREIGN KEY (UID) REFERENCES Users(UID),FOREIGN KEY (BID) REFERENCES Books(BID))  ";
        stmt.executeUpdate(createIssuedTableSql);

        // Insert initial book records into the "BOOKS" table
        //stmt.executeUpdate("INSERT INTO BOOKS(BNAME, GENRE, PRICE) VALUES ('War and Peace', 'Mystery', 200),  ('The Guest Book', 'Fiction', 300), ('The Perfect Murder','Mystery', 150), ('Accidental Presidents', 'Biography', 250), ('The Wicked King','Fiction', 350)");
          stmt.executeUpdate("INSERT INTO BOOKS (BNAME, GENRE, PRICE) VALUES ('The Great Gatsby', 'Fiction', 20), ('To Kill a Mockingbird', 'Classic', 15), ('1984', 'Dystopian', 18), ('Pride and Prejudice', 'Romance', 25), ('The Catcher in the Rye', 'Coming of Age', 22), ('The Hobbit', 'Fantasy', 30), ('The Lord of the Rings', 'Fantasy', 40), ('Harry Potter and the Sorcerer\\'s Stone', 'Fantasy', 35), ('The Hunger Games', 'Dystopian', 25), ('The Da Vinci Code', 'Mystery', 28), ('The Alchemist', 'Adventure', 21), ('Brave New World', 'Dystopian', 23), ('The Odyssey', 'Epic', 28), ('Frankenstein', 'Gothic', 17), ('Moby-Dick', 'Adventure', 32), ('Jane Eyre', 'Gothic', 19), ('Wuthering Heights', 'Gothic', 24), ('The Road', 'Post-Apocalyptic', 26), ('The Kite Runner', 'Drama', 27), ('The Girl on the Train', 'Mystery', 29), ('The Picture of Dorian Gray', 'Gothic', 18), ('Dracula', 'Gothic', 20), ('Alice\\'s Adventures in Wonderland', 'Fantasy', 16), ('One Hundred Years of Solitude', 'Magical Realism', 31), ('The Shining', 'Horror', 34), ('The Chronicles of Narnia', 'Fantasy', 38), ('A Game of Thrones', 'Fantasy', 42), ('The Road Not Taken', 'Poetry', 14), ('The Old Man and the Sea', 'Adventure', 23), ('Sapiens: A Brief History of Humankind', 'Non-Fiction', 27), ('The Bell Jar', 'Drama', 19), ('The Color Purple', 'Drama', 21), ('The Grapes of Wrath', 'Classic', 26), ('The Outsiders', 'Coming of Age', 15), ('The Sun Also Rises', 'Modernist', 22), ('Great Expectations', 'Classic', 24), ('Fahrenheit 451', 'Dystopian', 30), ('Animal Farm', 'Allegory', 16), ('The Road Less Traveled', 'Self-Help', 18), ('The Silent Patient', 'Thriller', 28), ('The Fault in Our Stars', 'Young Adult', 23), ('The Great Expectations', 'Classic', 28), ('The Handmaid\\'s Tale', 'Dystopian', 32), ('The Road to Wigan Pier', 'Political', 19), ('The Secret Garden', 'Children\\'s Literature', 15), ('The Girl with the Dragon Tattoo', 'Mystery', 26), ('The Art of War', 'Classics/Military', 20), ('The Three Musketeers', 'Historical Fiction', 30), ('The Jungle Book', 'Adventure', 18), ('The Hound of the Baskervilles', 'Mystery', 25)");  
          
        // Insert initial users and admins "USERS" table
          stmt.executeUpdate("INSERT INTO USERS (USERNAME, PASSWORD, ADMIN) VALUES ('ahmed', 'ahmed', FALSE),('john_doe', 'johns_password', FALSE), ('alice_smith', 'alices_password', FALSE), ('bob_jones', 'bobs_password', FALSE), ('emma_davis', 'emmas_password', FALSE), ('michael_brown', 'michaels_password', FALSE), ('olivia_white', 'olivias_password', FALSE), ('samuel_taylor', 'samuels_password', FALSE), ('sophia_clark', 'sophias_password', FALSE), ('william_martin', 'williams_password', FALSE), ('emily_jackson', 'emilys_password', FALSE), ('ryan_hill', 'ryans_password', FALSE), ('ava_wilson', 'avas_password', FALSE), ('james_thomas', 'james_password', FALSE), ('lily_anderson', 'lilys_password', FALSE), ('daniel_miller', 'daniels_password', FALSE), ('admin_jane', 'admin_janes_password', TRUE), ('admin_mike', 'admin_mikes_password', TRUE), ('admin_anna', 'admin_annas_password', TRUE)");
        
        // Insert initial some ISSUED books "ISSUED" table
          stmt.executeUpdate("INSERT INTO ISSUED (UID, BID, ISSUED_DATE, RETURN_DATE, PERIOD, FINE) VALUES (1, 1, '01-01-2023', '15-01-2023', 14, 0), (2, 3, '01-02-2023', '15-02-2023', 12, 0), (3, 5, '01-03-2023', '15-03-2023', 16, 0), (4, 7, '01-04-2023', '15-04-2023', 10, 0), (5, 9, '01-05-2023', '15-05-2023', 18, 10), (6, 11, '01-06-2023', '15-06-2023', 14, 0), (7, 13, '01-07-2023', '15-07-2023', 20, 20), (8, 15, '01-08-2023', '15-08-2023', 13, 0), (9, 17, '01-09-2023', '15-09-2023', 15, 30), (10, 19, '01-10-2023', '15-10-2023', 11, 0)");
        // Close the ResultSet
        resultSet.close();
    } catch (Exception ex) {
        // Handle any exceptions by printing the stack trace
        ex.printStackTrace();
    }
}
    public static void user_menu(String UID) {
    // Create a JFrame for user functions
    JFrame f = new JFrame("User Functions");

    // Create a button to view available books
    JButton view_but = new JButton("View Books");
    view_but.setBounds(20, 20, 120, 25);
    view_but.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Create a new JFrame to display available books
            JFrame f = new JFrame("Books Available");

            // Connect to the database
            Connection connection = connect();
            String sql = "select * from BOOKS"; // Retrieve data from the database

            try {
                Statement stmt = connection.createStatement(); // Create a statement to connect to the database
                stmt.executeUpdate("USE LIBRARY"); // Use the 'LIBRARY' database
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                // Create a JTable to display the data
                JTable book_list = new JTable();
                book_list.setModel(DbUtils.resultSetToTableModel(rs));

                // Enable a scroll bar for the table
                JScrollPane scrollPane = new JScrollPane(book_list);

                // Add the scroll bar to the JFrame
                f.add(scrollPane);
                f.setSize(800, 400); // Set dimensions of the frame
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            } catch (SQLException e1) {
                // Handle SQL exception by displaying an error message
                JOptionPane.showMessageDialog(null, e1);
            }
        }
    });

    // Create a button to view user's issued books
    JButton my_book = new JButton("My Books");
    my_book.setBounds(150, 20, 120, 25);
    my_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Create a new JFrame to display user's issued books
            JFrame f = new JFrame("My Books");

            // Parse the user ID to an integer
            int UID_int = Integer.parseInt(UID);

            // Connect to the database
            Connection connection = connect();

            // Retrieve data for user's issued books
            String sql = "SELECT DISTINCT issued.*, books.bname, books.genre, books.price " +
              "FROM issued, books " +
              "WHERE issued.uid=" + UID_int + " AND books.bid IN " +
              "(SELECT bid FROM issued WHERE issued.uid=" + UID_int + ") " +
              "GROUP BY issued.iid, books.bname, books.genre, books.price";

            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("USE LIBRARY"); // Use the 'LIBRARY' database
                stmt = connection.createStatement();

                // Create an array list to store books
                ArrayList books_list = new ArrayList();

                ResultSet rs = stmt.executeQuery(sql);
                // Create a JTable to display the data
                JTable book_list = new JTable();
                book_list.setModel(DbUtils.resultSetToTableModel(rs));

                // Enable a scroll bar for the table
                JScrollPane scrollPane = new JScrollPane(book_list);

                // Add the scroll bar to the JFrame
                f.add(scrollPane);
                f.setSize(800, 400); // Set dimensions of the frame
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            } catch (SQLException e1) {
                // Handle SQL exception by displaying an error message
                JOptionPane.showMessageDialog(null, e1);
            }
        }
    });

    // Add buttons to the JFrame
    f.add(my_book);
    f.add(view_but);

    // Set JFrame properties
    f.setSize(300, 100); // Set dimensions of the frame
    f.setLayout(null); // Use no layout managers
    f.setVisible(true); // Make the frame visible
    f.setLocationRelativeTo(null); // Center the frame on the screen
}

    public static void admin_menu() {
     
     
    JFrame f=new JFrame("Admin Functions"); //Give dialog box name as admin functions
    //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //
     
     
    JButton create_but=new JButton("Create/Reset");//creating instance of JButton to create or reset database
    create_but.setBounds(450,60,120,25);//x axis, y axis, width, height 
    create_but.addActionListener(new ActionListener() { //Perform action
        public void actionPerformed(ActionEvent e){
             
            create(); //Call create function
            JOptionPane.showMessageDialog(null,"Database Created/Reset!"); //Open a dialog box and display the message
             
        }
    });
     
     
    JButton view_but=new JButton("View Books");//creating instance of JButton to view books
    view_but.setBounds(20,20,120,25);//x axis, y axis, width, height 
    view_but.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
             
            JFrame f = new JFrame("Books Available"); 
            //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             
             
            Connection connection = connect(); //connect to database
            String sql="select * from BOOKS"; //select all books 
            try {
                Statement stmt = connection.createStatement();
                 stmt.executeUpdate("USE LIBRARY"); //use database
                stmt=connection.createStatement();
                ResultSet rs=stmt.executeQuery(sql);
                JTable book_list= new JTable(); //view data in table format
                book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                //mention scroll bar
                JScrollPane scrollPane = new JScrollPane(book_list); 
 
                f.add(scrollPane); //add scrollpane
                f.setSize(800, 400); //set size for frame
                f.setVisible(true);
                f.setLocationRelativeTo(null);
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                 JOptionPane.showMessageDialog(null, e1);
            }               
             
    }
    }
    );
     
    JButton users_but=new JButton("View Users");//creating instance of JButton to view users
    users_but.setBounds(150,20,120,25);//x axis, y axis, width, height 
    users_but.addActionListener(new ActionListener() { //Perform action on click button
        public void actionPerformed(ActionEvent e){
                 
                JFrame f = new JFrame("Users List");
                //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 
                 
                Connection connection = connect();
                String sql="select * from users"; //retrieve all users
                try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY"); //use database
                    stmt=connection.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    JTable book_list= new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                    //mention scroll bar
                    JScrollPane scrollPane = new JScrollPane(book_list);
 
                    f.add(scrollPane); //add scrollpane
                    f.setSize(800, 400); //set size for frame
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                     JOptionPane.showMessageDialog(null, e1);
                }       
                 
                 
    }
        }
    );  
     
    JButton issued_but=new JButton("View Issued Books");//creating instance of JButton to view the issued books
    issued_but.setBounds(280,20,160,25);//x axis, y axis, width, height 
    issued_but.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                 
                JFrame f = new JFrame("Users List");
                //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 
                 
                Connection connection = connect();
                String sql="select * from issued";
                try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY");
                    stmt=connection.createStatement();
                    ResultSet rs=stmt.executeQuery(sql);
                    JTable book_list= new JTable();
                    book_list.setModel(DbUtils.resultSetToTableModel(rs)); 
                     
                    JScrollPane scrollPane = new JScrollPane(book_list);
 
                    f.add(scrollPane);
                    f.setSize(800, 400);
                    f.setVisible(true);
                    f.setLocationRelativeTo(null);
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                     JOptionPane.showMessageDialog(null, e1);
                }       
                             
    }
        }
    );
     
     
    JButton add_user=new JButton("Add User"); //creating instance of JButton to add users
    add_user.setBounds(20,60,120,25); //set dimensions for button
     
    add_user.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                 
                JFrame g = new JFrame("Enter User Details"); //Frame to enter user details
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Create label 
                JLabel l1,l2;  
                l1=new JLabel("Username");  //label 1 for username
                l1.setBounds(30,15, 100,30); 
                 
                 
                l2=new JLabel("Password");  //label 2 for password
                l2.setBounds(30,50, 100,30); 
                 
                //set text field for username 
                JTextField F_user = new JTextField();
                F_user.setBounds(110, 15, 200, 30);
                 
                //set text field for password
                JPasswordField F_pass=new JPasswordField();
                F_pass.setBounds(110, 50, 200, 30);
                //set radio button for admin
                JRadioButton a1 = new JRadioButton("Admin");
                a1.setBounds(55, 80, 200,30);
                //set radio button for user
                JRadioButton a2 = new JRadioButton("User");
                a2.setBounds(130, 80, 200,30);
                //add radio buttons
                ButtonGroup bg=new ButtonGroup();    
                bg.add(a1);bg.add(a2);  
                 
                                 
                JButton create_but=new JButton("Create");//creating instance of JButton for Create 
                create_but.setBounds(130,130,80,25);//x axis, y axis, width, height 
                create_but.addActionListener(new ActionListener() {
                     
                    public void actionPerformed(ActionEvent e){
                     
                    String username = F_user.getText();
                    String password = F_pass.getText();
                    Boolean admin = false;
                     
                    if(a1.isSelected()) {
                        admin=true;
                    }
                     
                    Connection connection = connect();
                     
                    try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY");
                     stmt.executeUpdate("INSERT INTO USERS(USERNAME,PASSWORD,ADMIN) VALUES ('"+username+"','"+password+"',"+admin+")");
                     JOptionPane.showMessageDialog(null,"User added!");
                     g.dispose();
                      
                    }
                     
                    catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                    }
                     
                });
                     
                 
                    g.add(create_but);
                    g.add(a2);
                    g.add(a1);
                    g.add(l1);
                    g.add(l2);
                    g.add(F_user);
                    g.add(F_pass);
                    g.setSize(350,200);//400 width and 500 height  
                    g.setLayout(null);//using no layout managers  
                    g.setVisible(true);//making the frame visible 
                    g.setLocationRelativeTo(null);
                 
                 
    }
    });
         
     
    JButton add_book=new JButton("Add Book"); //creating instance of JButton for adding books
    add_book.setBounds(150,60,120,25); 
     
    add_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                //set frame wot enter book details
                JFrame g = new JFrame("Enter Book Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // set labels
                JLabel l1,l2,l3;  
                l1=new JLabel("Book Name");  //lebel 1 for book name
                l1.setBounds(30,15, 100,30); 
                 
                 
                l2=new JLabel("Genre");  //label 2 for genre
                l2.setBounds(30,53, 100,30); 
                 
                l3=new JLabel("Price");  //label 2 for price
                l3.setBounds(30,90, 100,30); 
                 
                //set text field for book name
                JTextField F_bname = new JTextField();
                F_bname.setBounds(110, 15, 200, 30);
                 
                //set text field for genre 
                JTextField F_genre=new JTextField();
                F_genre.setBounds(110, 53, 200, 30);
                //set text field for price
                JTextField F_price=new JTextField();
                F_price.setBounds(110, 90, 200, 30);
                         
                 
                JButton create_but=new JButton("Submit");//creating instance of JButton to submit details  
                create_but.setBounds(130,130,80,25);//x axis, y axis, width, height 
                create_but.addActionListener(new ActionListener() {
                     
                    public void actionPerformed(ActionEvent e){
                    // assign the book name, genre, price
                    String bname = F_bname.getText();
                    String genre = F_genre.getText();
                    String price = F_price.getText();
                    //convert price of integer to int
                    int price_int = Integer.parseInt(price);
                     
                    Connection connection = connect();
                     
                    try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY");
                     stmt.executeUpdate("INSERT INTO BOOKS(BNAME,GENRE,PRICE) VALUES ('"+bname+"','"+genre+"',"+price_int+")");
                     JOptionPane.showMessageDialog(null,"Book added!");
                     g.dispose();
                      
                    }
                     
                    catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                    }
                     
                });
                                 
                    g.add(l3);
                    g.add(create_but);
                    g.add(l1);
                    g.add(l2);
                    g.add(F_bname);
                    g.add(F_genre);
                    g.add(F_price);
                    g.setSize(350,200);//400 width and 500 height  
                    g.setLayout(null);//using no layout managers  
                    g.setVisible(true);//making the frame visible 
                    g.setLocationRelativeTo(null);
                             
    }
    });
     
     
    JButton issue_book=new JButton("Issue Book"); //creating instance of JButton to issue books
    issue_book.setBounds(450,20,120,25); 
     
    issue_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                //enter details
                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //create labels
                JLabel l1,l2,l3,l4;  
                l1=new JLabel("Book ID(BID)");  // Label 1 for Book ID
                l1.setBounds(30,15, 100,30); 
                 
                 
                l2=new JLabel("User ID(UID)");  //Label 2 for user ID
                l2.setBounds(30,53, 100,30); 
                 
                l3=new JLabel("Period(days)");  //Label 3 for period
                l3.setBounds(30,90, 100,30); 
                 
                l4=new JLabel("Issued Date(DD-MM-YYYY)");  //Label 4 for issue date
                l4.setBounds(30,127, 150,30); 
                 
                JTextField F_bid = new JTextField();
                F_bid.setBounds(110, 15, 200, 30);
                 
                 
                JTextField F_uid=new JTextField();
                F_uid.setBounds(110, 53, 200, 30);
                 
                JTextField F_period=new JTextField();
                F_period.setBounds(110, 90, 200, 30);
                 
                JTextField F_issue=new JTextField();
                F_issue.setBounds(180, 130, 130, 30);   
 
                 
                JButton create_but=new JButton("Submit");//creating instance of JButton  
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height 
                create_but.addActionListener(new ActionListener() {
                     
                    public void actionPerformed(ActionEvent e){
                     
                    String uid = F_uid.getText();
                    String bid = F_bid.getText();
                    String period = F_period.getText();
                    String issued_date = F_issue.getText();
 
                    int period_int = Integer.parseInt(period);
                     
                    Connection connection = connect();
                     
                    try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY");
                     stmt.executeUpdate("INSERT INTO ISSUED(UID,BID,ISSUED_DATE,PERIOD) VALUES ('"+uid+"','"+bid+"','"+issued_date+"',"+period_int+")");
                     JOptionPane.showMessageDialog(null,"Book Issued!");
                     g.dispose();
                      
                    }
                     
                    catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                    }
                     
                });
                     
                 
                    g.add(l3);
                    g.add(l4);
                    g.add(create_but);
                    g.add(l1);
                    g.add(l2);
                    g.add(F_uid);
                    g.add(F_bid);
                    g.add(F_period);
                    g.add(F_issue);
                    g.setSize(350,250);//400 width and 500 height  
                    g.setLayout(null);//using no layout managers  
                    g.setVisible(true);//making the frame visible 
                    g.setLocationRelativeTo(null);
                 
                 
    }
    });
     
     
    JButton return_book=new JButton("Return Book"); //creating instance of JButton to return books
    return_book.setBounds(280,60,160,25); 
     
    return_book.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
                 
                JFrame g = new JFrame("Enter Details");
                //g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //set labels 
                JLabel l1,l2,l3,l4;  
                l1=new JLabel("Issue ID(IID)");  //Label 1 for Issue ID
                l1.setBounds(30,15, 100,30); 
                
                 
                l4=new JLabel("Return Date(DD-MM-YYYY)");  
                l4.setBounds(30,50, 150,30); 
                 
                JTextField F_iid = new JTextField();
                F_iid.setBounds(110, 15, 200, 30);
                 
                 
                JTextField F_return=new JTextField();
                F_return.setBounds(180, 50, 130, 30);
             
 
                JButton create_but=new JButton("Return");//creating instance of JButton to mention return date and calculcate fine
                create_but.setBounds(130,170,80,25);//x axis, y axis, width, height 
                create_but.addActionListener(new ActionListener() {
                     
                    public void actionPerformed(ActionEvent e){                 
                     
                    String iid = F_iid.getText();
                    String return_date = F_return.getText();
                     
                    Connection connection = connect();
                     
                    try {
                    Statement stmt = connection.createStatement();
                     stmt.executeUpdate("USE LIBRARY");
                     //Intialize date1 with NULL value
                     String date1=null;
                     String date2=return_date; //Intialize date2 with return date
                     
                     //select issue date
                     ResultSet rs = stmt.executeQuery("SELECT ISSUED_DATE FROM ISSUED WHERE IID="+iid);
                     while (rs.next()) {
                         date1 = rs.getString(1);
                          
                       }
                      
                     try {
                            Date date_1=new SimpleDateFormat("dd-MM-yyyy").parse(date1);
                            Date date_2=new SimpleDateFormat("dd-MM-yyyy").parse(date2);
                            //subtract the dates and store in diff
                            long diff = date_2.getTime() - date_1.getTime();
                            //Convert diff from milliseconds to days
                            ex.days=(int)(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                             
                             
                        } catch (ParseException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                      
                     
                     //update return date
                     stmt.executeUpdate("UPDATE ISSUED SET RETURN_DATE='"+return_date+"' WHERE IID="+iid);
                     g.dispose();
                      
 
                     Connection connection1 = connect();
                     Statement stmt1 = connection1.createStatement();
                     stmt1.executeUpdate("USE LIBRARY");                
                    ResultSet rs1 = stmt1.executeQuery("SELECT PERIOD FROM ISSUED WHERE IID="+iid); //set period
                    String diff=null; 
                    while (rs1.next()) {
                         diff = rs1.getString(1);
                          
                       }
                    int diff_int = Integer.parseInt(diff);
                    if (ex.days > diff_int) { // If number of days are more than the period then calculate fine
                         
                        //System.out.println(ex.days);
                        int fine = (ex.days-diff_int)*10; //fine for every day after the period is Rs 10.
                        //update fine in the system
                        stmt1.executeUpdate("UPDATE ISSUED SET FINE="+fine+" WHERE IID="+iid);  
                        String fine_str = ("Fine: Rs. "+fine);
                        JOptionPane.showMessageDialog(null,fine_str);
                         
                    }
 
                     JOptionPane.showMessageDialog(null,"Book Returned!");
                      
                    }
                             
                     
                    catch (SQLException e1) {
                        // TODO Auto-generated catch block
                         JOptionPane.showMessageDialog(null, e1);
                    }   
                     
                    }
                     
                }); 
                    g.add(l4);
                    g.add(create_but);
                    g.add(l1);
                    g.add(F_iid);
                    g.add(F_return);
                    g.setSize(350,250);//400 width and 500 height  
                    g.setLayout(null);//using no layout managers  
                    g.setVisible(true);//making the frame visible 
                    g.setLocationRelativeTo(null);              
    }
    });
    JButton update_user = new JButton("Update User");
    update_user.setBounds(20, 100, 120, 25);
    update_user.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JFrame g = new JFrame("Update User Details");

            JLabel l1, l2, l3;
            l1 = new JLabel("User ID(UID)");
            l1.setBounds(30, 15, 100, 30);

            l2 = new JLabel("New Username");
            l2.setBounds(30, 50, 100, 30);

            l3 = new JLabel("New Password");
            l3.setBounds(30, 85, 100, 30);

            JTextField F_uid = new JTextField();
            F_uid.setBounds(140, 15, 200, 30);

            JTextField F_new_user = new JTextField();
            F_new_user.setBounds(140, 50, 200, 30);

            JPasswordField F_new_pass = new JPasswordField();
            F_new_pass.setBounds(140, 85, 200, 30);

            JButton update_but = new JButton("Update");
            update_but.setBounds(130, 130, 80, 25);
            update_but.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    String uid = F_uid.getText();
                    String newUsername = F_new_user.getText();
                    String newPassword = F_new_pass.getText();

                    Connection connection = connect();

                    try {
                        Statement stmt = connection.createStatement();
                        stmt.executeUpdate("USE LIBRARY");
                        stmt.executeUpdate("UPDATE USERS SET USERNAME='" + newUsername + "', PASSWORD='" + newPassword
                                + "' WHERE UID=" + uid);
                        JOptionPane.showMessageDialog(null, "User information updated!");
                        g.dispose();

                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, e1);
                    }

                }
            });

            g.add(update_but);
            g.add(l1);
            g.add(l2);
            g.add(l3);
            g.add(F_uid);
            g.add(F_new_user);
            g.add(F_new_pass);
            g.setSize(400, 200);
            g.setLayout(null);
            g.setVisible(true);
            g.setLocationRelativeTo(null);
        }
    });

   JButton update_book = new JButton("Update Book");
update_book.setBounds(150, 100, 120, 25);
update_book.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        JFrame g = new JFrame("Update Book Details");

        JLabel l1, l2, l3, l4;
        l1 = new JLabel("Book ID(BID)");
        l1.setBounds(30, 15, 100, 30);

        l2 = new JLabel("New Book Name");
        l2.setBounds(30, 50, 100, 30);

        l3 = new JLabel("New Genre");
        l3.setBounds(30, 85, 100, 30);

        l4 = new JLabel("New Price");
        l4.setBounds(30, 120, 100, 30);

        JTextField F_bid = new JTextField();
        F_bid.setBounds(140, 15, 200, 30);

        JTextField F_new_bname = new JTextField();
        F_new_bname.setBounds(140, 50, 200, 30);

        JTextField F_new_genre = new JTextField();
        F_new_genre.setBounds(140, 85, 200, 30);

        JTextField F_new_price = new JTextField();
        F_new_price.setBounds(140, 120, 200, 30);

        JButton update_but = new JButton("Update");
        update_but.setBounds(130, 165, 80, 25);
        update_but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String bid = F_bid.getText();
                String newBName = F_new_bname.getText();
                String newGenre = F_new_genre.getText();
                String newPrice = F_new_price.getText();

                Connection connection = connect();

                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate("USE LIBRARY");
                    stmt.executeUpdate("UPDATE BOOKS SET BNAME='" + newBName + "', GENRE='" + newGenre
                            + "', PRICE=" + newPrice + " WHERE BID=" + bid);
                    JOptionPane.showMessageDialog(null, "Book information updated!");
                    g.dispose();

                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null, e1);
                }

            }
        });

        g.add(update_but);
        g.add(l1);
        g.add(l2);
        g.add(l3);
        g.add(l4);
        g.add(F_bid);
        g.add(F_new_bname);
        g.add(F_new_genre);
        g.add(F_new_price);
        g.setSize(400, 220);
        g.setLayout(null);
        g.setVisible(true);
        g.setLocationRelativeTo(null);
    }
});

    
    f.add(update_user);
    f.add(update_book); 
    f.add(create_but);
    f.add(return_book);
    f.add(issue_book);
    f.add(add_book);
    f.add(issued_but);
    f.add(users_but);
    f.add(view_but);
    f.add(add_user);
    f.setSize(600,200);//400 width and 500 height  
    f.setLayout(null);//using no layout managers  
    f.setVisible(true);//making the frame visible 
    f.setLocationRelativeTo(null);
     
    }
}
