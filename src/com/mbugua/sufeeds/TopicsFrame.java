/*
 * Andrew Mbugua
 * BBIT
 * 222336
 * 17th November 2026
 */
package com.mbugua.sufeeds;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class TopicsFrame extends JFrame {

    JComboBox<String> cmbClasses = new JComboBox<>();
    ArrayList<Integer> classIds = new ArrayList<>();   // class numbers, same order as the drop-down
    JTextField txtTopic = new JTextField();
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> listTopics = new JList<>(listModel);
    JButton btnAdd = new JButton("Add");
    JButton btnDelete = new JButton("Delete selected");
    JButton btnFeedback=new JButton("Feedback");
    JButton btnBack = new JButton("Back");

    public TopicsFrame() {
        setTitle("SU Feeds - My Topics");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridLayout(2, 2, 10, 10));
        top.add(new JLabel("Class:"));
        top.add(cmbClasses);
        top.add(new JLabel("Topic:"));
        top.add(txtTopic);

        JPanel bottom = new JPanel(new GridLayout(2, 3, 10, 10));
        bottom.add(btnAdd);
        bottom.add(btnDelete);
        bottom.add(btnBack);
        bottom.add(btnFeedback);
        JLabel copyright = new JLabel("© 2026 Andrew Mbugua");   // tiny copyright text
        copyright.setFont(new Font("SansSerif", Font.PLAIN, 9));
        copyright.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        bottom.add(copyright);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(listTopics), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadClasses();
        loadTopics();
        cmbClasses.addActionListener(e -> loadTopics());   // new class chosen: reload topics
        btnAdd.addActionListener(e -> addTopic());
        btnDelete.addActionListener(e -> deleteTopic());
        btnBack.addActionListener(e -> {
            new ClassesFrame().setVisible(true);
            dispose();
        });
        btnFeedback.addActionListener(e -> {
            new FeedbackFrame().setVisible(true);
            dispose();
        });
    }

    // Number of the class chosen in the drop-down (-1 if none)
    private int getClassNo() {
        int i = cmbClasses.getSelectedIndex();
        if (i == -1) {
            return -1;
        }
        return classIds.get(i);
    }

    // READ: fill the drop-down with this student's classes
    private void loadClasses() {
        String sql = "SELECT class_no, class_name FROM tbl_class WHERE student_id = ?";
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, LoginFrame.LoggedInStudentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                classIds.add(rs.getInt("class_no"));
                cmbClasses.addItem(rs.getString("class_name"));
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // READ: show the topics of the chosen class
    private void loadTopics() {
        listModel.clear();
        String sql = "SELECT topic_name FROM tbl_topic WHERE class_no= ?";
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, getClassNo());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listModel.addElement(rs.getString("topic_name"));
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // CREATE
    private void addTopic() {
        String topic = txtTopic.getText();
        if (getClassNo() == -1 || topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose a class and type a topic.");
            return;
        }
        run("INSERT INTO tbl_topic (class_no, topic_name) VALUES (?, ?)", topic);
    }

    // DELETE
    private void deleteTopic() {
        String selected = listTopics.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a topic first.");
            return;
        }
        run("DELETE FROM tbl_topic WHERE class_no = ? AND topic_name = ?", selected);
    }

    // Runs an INSERT or DELETE. Both use the same two values: class number, then topic name
    private void run(String sql, String topic) {
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, getClassNo());
            ps.setString(2, topic);
            ps.executeUpdate();
            conn.close();
            txtTopic.setText("");
            loadTopics();   // refresh the list
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}