/*
 * Andrew Mbugua
 * BBIT
 * 222336
 * 12th November 2026
 */

package com.mbugua.sufeeds;
import javax.swing.*;
import java .awt.*;
import java. sql.*;

public class RegisterFrame extends JFrame {

    // The things the user sees and uses
    JTextField txtName = new JTextField();
    JTextField txtEmail = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JButton btnRegister = new JButton("Register");

    public RegisterFrame() {
        // Window settings
        setTitle("SU Feeds - Register");
        setSize(400, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));   // 5 rows, 2 columns

        // Add components in order: left, right, left, right...add(new JtnLabel("Full name:"));add(txtName);
        JButton btnBack=new JButton("Back to login");
        add(new JLabel ("Full name:"));
        add(txtName);
        add(new JLabel("Email:"));
        add(txtEmail);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(btnBack);
        add(btnRegister);

        // Tiny copyright text (required)
        JLabel copyright = new JLabel("© 2026 Andrew Mbugua");
        copyright.setFont(new Font("Softcode", Font.PLAIN, 9));
        add(copyright);

        // When the button is clicked, run registerStudent()
        btnRegister.addActionListener(e -> registerStudent());
        btnBack.addActionListener(e -> {new LoginFrame().setVisible(true);
        dispose();
        });
    }

    private void registerStudent() {
        // 1. Read what the user typed
        String name = txtName.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        // 2. Check nothing is empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;   // stop here
        }

        // 3. The SQL command. Each ? is filled in below.
        String sql = "INSERT INTO tbl_student (full_name, email, password) VALUES (?, ?, ?)";

        try {
            Connection conn = database.getConnection();   // connect
            PreparedStatement ps = conn.prepareStatement(sql); // prepare the SQL
            ps.setString(1, name);       // first ?  = name
            ps.setString(2, email);      // second ? = email
            ps.setString(3, password);   // third ?  = password
            ps.executeUpdate();          // run it
            conn.close();                // disconnect

            JOptionPane.showMessageDialog(this, "Account created!");
            new LoginFrame().setVisible(true); // opens the login frame
            dispose(); //closes this register window
        } catch (SQLException ex) {
            // Something went wrong: show a warning instead of crashing
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new RegisterFrame().setVisible(true);
    }
}


