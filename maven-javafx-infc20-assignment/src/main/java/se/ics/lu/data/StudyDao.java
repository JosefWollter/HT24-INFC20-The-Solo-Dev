package se.ics.lu.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                    statement.setString(1, study.getStudentPersonalNumber());
                    statement.setString(2, study.getCourseCode());
                    statement.execute();
            } catch (SQLException e){
                if(e.getErrorCode() == 2627){
                    throw new DaoException("The student already studies this course", e);
                }
                throw new DaoException("Error while saving study", e);
            }
        }

        public void update(Study study){
            String callProcedure = "{CALL uspUpdateStudy(?, ?, ?)}";

            try (Connection connection = connectionHandler.getConnection();
                CallableStatement statement = connection.prepareCall(callProcedure)){
                    statement.setString(1, study.getStudentPersonalNumber());
                    statement.setString(2, study.getCourseCode());
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
            return new Study(
                resultSet.getString("StudentPersonalNo"),
                resultSet.getString("CourseCode"),
                resultSet.getString("Grade"));
        }

}
