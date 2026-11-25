/*
  Andrew Mbugua
  BBIT
  222336
  14th November 2026
 */
package com.mbugua.sufeeds;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ClassesFrame extends JFrame {
    JTextField txtClass=new JTextField();
    DefaultListModel<String> listModel = new DefaultListModel<>();   // holds the class names
    JList<String> listClasses = new JList<>(listModel);              // shows them on screen
    JButton btnAdd = new JButton("Add");
    JButton btnUpdate = new JButton("Update selected");
    JButton btnDelete = new JButton("Delete selected");
    JButton btnTopics=new JButton("Topics");
    JButton btnLogout = new JButton("Logout");

    public ClassesFrame(){
        setTitle("SU feeds - My Classes("+LoginFrame.LoggedInName+")");
        setSize(450,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // top: Label + text box + Add Button
        JPanel top= new JPanel(new GridLayout(1,3,10,10));
        top.add(new JLabel("Class name:"));
        top.add(txtClass);
        top.add(btnAdd);
        add(top, BorderLayout.NORTH);

        // Middle : the list of classes
        add(new JScrollPane(listClasses), BorderLayout.CENTER);

        JPanel buttons =new JPanel(new GridLayout(1,4,10,10));
        buttons.add(btnTopics);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnLogout);

        JLabel copyright=new JLabel("2026 Andrew Mbugua");
        copyright.setFont(new Font("softcode",Font.PLAIN,10));

        JPanel bottom=new JPanel(new GridLayout(2,1,5,5));
        bottom.add(buttons);
        bottom.add(copyright);
        add(bottom, BorderLayout.SOUTH);

        // Button clicks
        btnAdd.addActionListener(e -> addClass());
        btnUpdate.addActionListener(e -> updateClass());
        btnDelete.addActionListener(e -> deleteClass());
        btnLogout.addActionListener(e -> {new LoginFrame().setVisible(true);dispose();
        });
        btnTopics.addActionListener(e -> {
            new TopicsFrame().setVisible(true);
            dispose();
        });
        LoadClasses();  // shows the saved classes as soon as the window opens

    }
       // READ: get this student's classes from the databases
     private void LoadClasses() {
         listModel.clear(); // empty the list first
         String sql = "Select class_name FROM tbl_class WHERE student_id=?";

         try {
             Connection conn = database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ps.setInt(1, LoginFrame.LoggedInStudentID);
             ResultSet rs = ps.executeQuery();

             while (rs.next()) { // repeat for every row found
                 listModel.addElement(rs.getString("class_name"));

             }
             conn.close();

         } catch (SQLException ex) {
             JOptionPane.showMessageDialog(this, "Error:" + ex.getMessage());
         }
     }
     // CREATE :save a new class
    private void addClass(){
        String className= txtClass.getText();

        if (className. isEmpty()){
            JOptionPane.showMessageDialog(this, "Please type a class name.");
            return;
        }
        String sql= "INSERT INTO tbl_class(student_id, class_name) VALUES (?,?)";

        try{
            Connection conn=database.getConnection();
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setInt(1, LoginFrame.LoggedInStudentID);
            ps.setString(2, className);
            ps.executeUpdate();
            conn.close();
            txtClass.setText("");  //clears the box
            LoadClasses(); // refreshes the list
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(this,"Error"+ex.getMessage());

        }
    }
     // UPDATE :rename the selected class to what is typed in the box
    private void updateClass(){
        String oldName=listClasses.getSelectedValue();
        String newName= txtClass.getText();
        if (oldName==null){
            JOptionPane.showMessageDialog(this,"Please select a class from the list first.");
            return;
        }
        if (newName.isEmpty()){
            JOptionPane.showMessageDialog(this,"type the new class name in the box.");
            return;
        }
        String sql= "UPDATE tbl_class SET class_name=? WHERE student_id=? AND class_name=?";

        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setInt(2, LoginFrame.LoggedInStudentID);
            ps.setString(3, oldName);
            ps.executeUpdate();
            conn.close();

            txtClass.setText("");
            LoadClasses();

        }catch (SQLException ex){
            JOptionPane.showMessageDialog(this,"Error:"+ ex.getMessage());
        }
    }
     // DELETE : remove the selected class
    private void deleteClass() {
        String selected=listClasses.getSelectedValue();
        if(selected==null){
            JOptionPane.showMessageDialog(this,"Please select a class from the list first.");
            return;
        }
        // Message before deleting
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete \"" + selected + "\" and all its topics?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;   // user clicked No
        }
        String sql= "DELETE FROM tbl_class WHERE student_id =? AND class_name=?";

        try{
            Connection conn=database.getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);
            ps.setInt(1, LoginFrame.LoggedInStudentID);
            ps.setString(2,selected);
            ps.executeUpdate();
            conn.close();

            LoadClasses();
        }catch (SQLException ex) {
           JOptionPane.showMessageDialog(this,"Error:"+ex.getMessage());
        }
    }
}
