package se.ics.lu.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import se.ics.lu.data.StudentDao;
import se.ics.lu.models.Course;
import se.ics.lu.models.Student;
import se.ics.lu.models.Study;

public class StudyDao {
    private ConnectionHandler connectionHandler;

     public StudyDao() throws IOException {
         this.connectionHandler = new ConnectionHandler();
     }

        public List<Study> getAllStudies(){
            String callProcedure = "{CALL uspGetAllStudies}";
            List<Study> studies = new ArrayList<>();
    
            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure);
                ResultSet resultSet = statement.executeQuery()) {
    
                while (resultSet.next()){
                    studies.add(mapToStudy(resultSet));
                }
            } catch (SQLException e) {
                throw new DaoException("Error while fetching all studies", e);
            }
            return studies;
        }

        public List<Study> getStudiesByCourse(String courseCode) {
            String callProcedure = "{CALL uspGetStudiesByCourseCode(?)}";
            List<Study> studies = new ArrayList<>();

            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, courseCode);

                    try (ResultSet resultSet = statement.executeQuery()){
                        while (resultSet.next()){
                            studies.add(mapToStudy(resultSet));
                        }
                    }
            } catch (SQLException e){
                e.printStackTrace();
                throw new DaoException("Error fetching studies by course code: " + courseCode, e);
            }

            return studies;
        }

        public Study getStudy(String studentPersonalNumber, String courseCode){
            String callProcedure = "{CALL uspGetStudy(?,?)}";

            try(Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, studentPersonalNumber);
                    statement.setString(2, courseCode);

                    try (ResultSet resultSet = statement.executeQuery()){
                        if(resultSet.next()){
                            return mapToStudy(resultSet);
                        } else {
                            return null;
                        }
                    }
                } catch (SQLException e){
                    throw new DaoException("Error fetching study with student personal number: " 
                                        + studentPersonalNumber + " and course code: " + courseCode, e);
                }
        }

        public void save(Study study){
            String callProcedure = "{CALL uspInsertStudy(?, ?)}";

            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, study.getStudent().getStudentPersonalNumber());
                    statement.setString(2, study.getCourse().getCourseCode());
                    statement.execute();
            } catch (SQLException e){
                if(e.getErrorCode() == 50000) {
                    throw new DaoException("The student already studies this course", e);
                }
                throw new DaoException("Error enrolling student", e);
            }
        }

        public void update(Study study){
            String callProcedure = "{CALL uspUpdateStudy(?, ?, ?)}";

            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, study.getStudent().getStudentPersonalNumber());
                    statement.setString(2, study.getCourse().getCourseCode());
                    statement.setString(3, study.getGrade());
                    statement.execute();
            } catch (SQLException e){
                throw new DaoException("Error while updating study", e);
            }
        }

        public void delete(String studentPersonalNumber, String courseCode){
            String callProcedure = "{CALL uspDeleteStudy(?, ?)}";

            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, studentPersonalNumber);
                    statement.setString(2, courseCode);
                    statement.execute();
            } catch (SQLException e){
                throw new DaoException("Error while deleting study", e);
            }
        }

        private Study mapToStudy(ResultSet resultSet) throws SQLException {
            try {
                StudentDao studentDao = new StudentDao();
                Student student = studentDao.getStudentByNumber(resultSet.getString("StudentPersonalNo"));
                CourseDao courseDao = new CourseDao();
                Course course = courseDao.getByCourseCode(resultSet.getString("CourseCode"));
                return new Study(
                    student, course,
                    resultSet.getString("Grade"));
            } catch (IOException e){
                throw new DaoException("Error while mapping study", e);
            }
        }
}
