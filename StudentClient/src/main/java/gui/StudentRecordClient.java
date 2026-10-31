package gui;

import domain.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRecordClient implements ActionListener{
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static JFrame frame;
    private static JTextField nameField;
    private static JTextField idField;
    private static JTextField scoreField;
    private static JTextArea recordsTextArea;
    private static JButton addButton;
    private static JButton retrieveButton;
    private static JButton exitButton;
    private static Socket socket;

    public StudentRecordClient(){
        connectToServer();
        createAndShowGUI();
    }
    private void connectToServer() {
        try {
            System.out.println("Attempting to connect to server...");
            socket = new Socket("127.0.0.1", 6666);
            System.out.println("Client connected to local server");

            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Could not connect to the server: " + ioe.getMessage());
            System.exit(0);
        }
    }
    private void createAndShowGUI() {
        frame = new JFrame("Student Record Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Score:"));
        scoreField = new JTextField();
        inputPanel.add(scoreField);
        frame.add(inputPanel, BorderLayout.NORTH);

        addButton = new JButton("Add Record");
        addButton.addActionListener(this);
        retrieveButton = new JButton("Retrieve Records");
        retrieveButton.addActionListener(this);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(exitButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        recordsTextArea = new JTextArea(10,10);
        recordsTextArea.setEditable(false);
        frame.add(new JScrollPane(recordsTextArea), BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    private static void addStudentRecord() {
        try {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();

            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter the name, ID and score");
                return;
            }

            double score = Double.parseDouble(scoreField.getText().trim());

            Student student = new Student(name, id, score);
            out.writeObject(student);
            out.flush();

            nameField.setText("");
            idField.setText("");
            scoreField.setText("");
            nameField.requestFocus();
        }
        catch (NumberFormatException nfe)
        {
            JOptionPane.showMessageDialog(frame, "The score must be a number");
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(frame, "IO Exception: " + ioe.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    private static void retrieveStudentRecords() {
        try {
            out.writeObject("retrieve");
            out.flush();

            ArrayList<Student> studentList = (ArrayList<Student>)in.readObject();
            displayStudentRecords(studentList);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(frame, "IO Exception: " + ioe.getMessage());
        }
        catch (ClassNotFoundException cnfe)
        {
            JOptionPane.showMessageDialog(frame, "Class not found: " + cnfe.getMessage());
        }
    }
    private static void displayStudentRecords(List<Student> studentList) {
        recordsTextArea.setText("");
        for (Student student : studentList) {
            recordsTextArea.append(student.toString() + "\n");
        }
    }
    private static void closeConnection() {
        try {
            out.writeObject("exit");
            out.flush();

            String response = (String)in.readObject();
            System.out.println("From SERVER>> " + response);

            out.close();
            in.close();
            socket.close();
        }
        catch (IOException ioe)
        {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("Class not found: " + cnfe.getMessage());
        }
        System.exit(0);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == addButton) {
            addStudentRecord();
        }
        else if (e.getSource() == retrieveButton) {
            retrieveStudentRecords();
        }
        else if (e.getSource() == exitButton) {
            closeConnection();
        }
    }
    public static void main(String[] args) {
        new StudentRecordClient();
    }
}
