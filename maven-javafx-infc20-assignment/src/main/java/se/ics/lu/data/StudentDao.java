package se.ics.lu.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.ics.lu.models.Student;

public class StudentDao {
     private ConnectionHandler connectionHandler;

     public StudentDao() throws IOException {
         this.connectionHandler = new ConnectionHandler();
     }

     public List<Student> getAllStudents(){
        String callProcedure = "{CALL uspGetAllStudents}";
        List<Student> students = new ArrayList<>();

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()){
                students.add(mapToStudent(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Error while fetching all students", e);
        }
        return students;
     }

     public Student getStudentByNumber(String studentPersonalNumber){
        String callProcedure = "{CALL uspGetStudentByStudentPersonalNo}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){
                statement.setString(1, studentPersonalNumber);

                try (ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return mapToStudent(resultSet);
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e){
            throw new DaoException("Error fetching student with personal number: " + studentPersonalNumber, e);
        }
     }

     public void save(Student student){
        String callProcedure = "{CALL uspInsertStudent(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){

                statement.setString(1, student.getStudentPersonalNumber());
                statement.setString(2, student.getName());
                statement.setString(3, student.getEmail());

                statement.executeUpdate();
            } catch (SQLException e){
                if (e.getErrorCode() == 2627) {
                    throw new DaoException("A student with this personal number already exists", e);
                } else {
                    throw new DaoException("Error saving student: " + student.getStudentPersonalNumber(), e);
                }
            }
     }

     public void update(Student student) {
        String callProcedure = "{CALL uspUpdateStudent(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){

                statement.setString(1, student.getStudentPersonalNumber());
                statement.setString(2, student.getName());
                statement.setString(3, student.getEmail());

                statement.executeUpdate();
            } catch (SQLException e){
                throw new DaoException("Error updating student: " + student.getStudentPersonalNumber(), e);
            }
     }

     public void deleteByNo(String studentPersonalNumber){
        String callProcedure = "{CALL uspDeleteStudent}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){

                statement.setString(1, studentPersonalNumber);

                statement.executeUpdate();
            } catch (SQLException e){
                throw new DaoException("Error deleting student with personal number: " + studentPersonalNumber, e);
            }
     }

     private Student mapToStudent(ResultSet resultSet) throws SQLException{
        return new Student(
            resultSet.getString("StudentPersonalNo"),
            resultSet.getString("StudentName"),
            resultSet.getString("StudentEmail"));
     }
}
