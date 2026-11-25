/*
* Andrew Mbugua
* BBIT
* 222336
* 19th November 2026
*/
package com.mbugua.sufeeds;
 import javax.swing.*;
 import java.awt.*;
 import java.sql.*;
 import java.util.ArrayList;

public class FeedbackFrame extends JFrame {

    JComboBox<String> cmbTopics = new JComboBox<>();
    ArrayList<Integer> topicIds = new ArrayList<>();   // topic numbers, same order as the drop-down
    JTextField txtFeedback = new JTextField();
    DefaultListModel<String> listModel = new DefaultListModel<>();
    JList<String> listFeedback = new JList<>(listModel);
    JButton btnPost = new JButton("Post");
    JButton btnBack = new JButton("Back");

    public FeedbackFrame() {
        setTitle("SU Feeds - Feedback");
        setSize(500, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridLayout(2, 2, 10, 10));
        top.add(new JLabel("Topic:"));
        top.add(cmbTopics);
        top.add(new JLabel("Your feedback:"));
        top.add(txtFeedback);

        JPanel bottom = new JPanel(new GridLayout(2, 2, 10, 10));
        bottom.add(btnPost);
        bottom.add(btnBack);
        JLabel copyright = new JLabel("© 2026 Andrew Mbugua");   // tiny copyright text
        copyright.setFont(new Font("Softcode", Font.PLAIN, 9));
        copyright.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        bottom.add(copyright);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(listFeedback), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadTopics();
        loadFeedback();
        cmbTopics.addActionListener(e -> loadFeedback());   // new topic chosen: reload feedback
        btnPost.addActionListener(e -> postFeedback());
        btnBack.addActionListener(e -> {
            new TopicsFrame().setVisible(true);
            dispose();
        });
    }

    // Number of the topic chosen in the drop-down (-1 if none)
    private int getTopicId() {
        int i = cmbTopics.getSelectedIndex();
        if (i == -1) {
            return -1;
        }
        return topicIds.get(i);
    }

    // READ: fill the drop-down with the topics of ALL students
    private void loadTopics() {
        String sql = "SELECT t.topic_id, c.class_name, t.topic_name "
                + "FROM tbl_topic t JOIN tbl_class c ON t.class_no = c.class_no";
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                topicIds.add(rs.getInt("topic_id"));
                cmbTopics.addItem(rs.getString("class_name") + ": " + rs.getString("topic_name"));
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // READ: show every student's feedback on the chosen topic
    private void loadFeedback() {
        listModel.clear();
        if (getTopicId() == -1) {
            return;
        }
        String sql = "SELECT s.full_name, f.feedback_comment "
                + "FROM tbl_feedback f JOIN tbl_student s ON f.student_id = s.student_id "
                + "WHERE f.topic_id = ?";
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, getTopicId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listModel.addElement(rs.getString("full_name") + ": " + rs.getString("feedback_comment"));
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // CREATE: save a new feedback comment
    private void postFeedback() {
        String text = txtFeedback.getText();
        if (getTopicId() == -1 || text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Choose a topic and type your feedback.");
            return;
        }
        String sql = "INSERT INTO tbl_feedback (topic_id, student_id, feedback_comment) VALUES (?, ?, ?)";
        try {
            Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, getTopicId());
            ps.setInt(2, LoginFrame.LoggedInStudentID);
            ps.setString(3, text);
            ps.executeUpdate();
            conn.close();
            txtFeedback.setText("");
            loadFeedback();   // refresh the list
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}


