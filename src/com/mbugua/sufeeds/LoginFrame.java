/*
*Andrew Mbugua
* BBIT
* 222336
* 13th November 2026
 */
package com.mbugua.sufeeds;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame  {

    // Saved after a successful login so others screens use them later
    public static int LoggedInStudentID;
    public static String  LoggedInName;

    // The things the user sees and uses
    JTextField txtEmail= new JTextField();
    JPasswordField txtPassword=new JPasswordField();
    JButton btnlogin=new JButton("Login");
    JButton btnRegister= new JButton("Create account");

    public LoginFrame() {
        // Window settings
        setTitle("SU Feeds - Login");
        setSize(400 ,220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout (4 ,2 ,10,10)); // 4 rows 2 columns

        // add components
        add (new JLabel ("Email"));
        add(txtEmail);
        add(new JLabel ("Password:"));
        add(txtPassword);
        add(btnRegister);
        add(btnlogin);

        // Tiny copyright text
        JLabel copyright=new JLabel(" 2026 Andrew Mbugua");
        copyright.setFont(new Font ("softcode",Font.PLAIN ,9));
        add(copyright);

        // Button Clicks
        btnlogin.addActionListener(e ->LoginStudent());
        btnRegister.addActionListener(e -> openRegister());


    }

    private void LoginStudent() {
        // Read what the user typed
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        // check nothing is empty
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email and password.");
            return; // stops here
        }
        // look for a student with this email AND password
        String sql = "SELECT student_id, full_name FROM tbl_student WHERE email=? AND password =?";

        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery(); // run the search

            //the rs.next () is true if a matching row was found
            if (rs.next()) {
                LoggedInStudentID = rs.getInt("Student_id");
                LoggedInName = rs.getString("Full_name");
                JOptionPane.showMessageDialog(this, "Welcome," + LoggedInName + "!");
                new ClassesFrame().setVisible(true); // opens the classes screen
                dispose(); // close the login window
            } else {
                JOptionPane.showMessageDialog(this, "Wrong email or password.");
            }
            conn.close();
        } catch (SQLException ex) {
            // Someting went wrong : show a warning instead of crashing
            JOptionPane.showMessageDialog(this, "Error" + ex.getMessage());

        }
    }
     private void openRegister(){
        new RegisterFrame().setVisible(true); // opens the register window
         dispose();  // closes the login window
     }
     public static void main (String [] args){
        new LoginFrame().setVisible(true);
     }
}
