package dao;

import connection.DBConnection;
import domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class StudentDAO {

    private Connection con;
    private Statement stmt;
    private PreparedStatement pstmt;

    public StudentDAO()
    {
        try {
            this.con = DBConnection.derbyConnection();
            createTable();
        }
        catch(Exception exception) {
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE StudentRecord ("
                + "studentName VARCHAR(50) NOT NULL, "
                + "studentId VARCHAR(20) NOT NULL, "
                + "studentScore DOUBLE NOT NULL, "
                + "PRIMARY KEY (studentId))";
        try {
            stmt = this.con.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("StudentRecord table created");
        }
        catch(SQLException sqlException) {
            if (!"X0Y32".equals(sqlException.getSQLState())) {
                JOptionPane.showMessageDialog(null, "SQL Error: " + sqlException.getMessage());
            }
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            }
            catch(Exception exception){
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }
    }

    public void save(Student student) {
        int ok;
        String sql = "INSERT INTO StudentRecord values (?, ?, ?)";
        try {
            pstmt = this.con.prepareStatement(sql);
            pstmt.setString(1, student.getStudentName());
            pstmt.setString(2, student.getStudentId());
            pstmt.setDouble(3, student.getStudentScore());
            ok = pstmt.executeUpdate();
            System.out.println(ok + " record inserted");
        }
        catch(SQLException sqlException) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + sqlException.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
            }
            catch(Exception exception){
                JOptionPane.showMessageDialog(null, exception.getMessage());
            }
        }
    }

    public ArrayList<Student> getAll()
    {
        ArrayList<Student> studentList = new ArrayList<>();
        ResultSet rs = null;
        try{
            String getAllSql = "select * from StudentRecord";
            pstmt = this.con.prepareStatement(getAllSql);
            rs = pstmt.executeQuery();

            if (rs != null){
                while ( rs.next() )
                {
                    System.out.println("DB table Record: "
                    + rs.getString(1) + " " + rs.getString(2) + " " + rs.getDouble(3));
                    studentList.add(new Student(rs.getString(1), rs.getString(2), rs.getDouble(3)));
                }
            }
        }
        catch(Exception exception){
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
        finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
            }
            catch(Exception exception){
            }
        }
        return studentList;
    }
    public void closeConnection() {
        try {
            if (con != null)
                con.close();
        }
        catch(Exception exception){
            JOptionPane.showMessageDialog(null, exception.getMessage());
        }
    }
}
