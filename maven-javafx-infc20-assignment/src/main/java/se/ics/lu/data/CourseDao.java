package se.ics.lu.data;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.ics.lu.models.Course;

public class CourseDao {

    private ConnectionHandler connectionHandler;

    public CourseDao() throws IOException {
        this.connectionHandler = new ConnectionHandler();
    }

    public List<Course> getAllCourses(){
        String callProcedure = "{CALL uspGetAllCourses}";
        List<Course> courses = new ArrayList();

        try(Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure);
            ResultSet resultSet = statement.executeQuery()){
            while(resultSet.next()){
                courses.add(mapToCourse(resultSet));
            }
        } catch (SQLException e){
            throw new DaoException("Error while fetching all courses", e);
        }
        return courses;
    }

    public Course getByCourseCode(String courseCode){
        String callProcedure = "{CALL uspGetCourseByCourseCode(?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){
                statement.setString(1, courseCode);

                try (ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return mapToCourse(resultSet);
                    } else {
                        return null;
                    }
                }
        } catch (SQLException e){
            throw new DaoException("Error fetching course with course code: " + courseCode, e);
        }
    }

    public void save(Course course) {
        String callProcedure = "{CALL uspInsertCourse(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){

            statement.setString(1, course.getCourseCode());
            statement.setString(2, course.getName());
            statement.setDouble(3, course.getCredits());

            statement.executeUpdate();
        } catch (SQLException e){
            if(e.getErrorCode() == 2627){
                throw new DaoException("Course with course code: " + course.getCourseCode() + " already exists", e);
            } else if(e.getErrorCode() == 547){
                throw new DaoException("Please enter a valid course code", e);
            } else {
            throw new DaoException("Error while saving course", e);
            }
        }
    }

    public void update(Course course){
        String callProcedure = "{CALL uspUpdateCourse(?, ?, ?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){
                statement.setString(1, course.getCourseCode());
                statement.setString(2, course.getName());
                statement.setDouble(3, course.getCredits());

                statement.executeUpdate();
        } catch (SQLException e){
            throw new DaoException("Error while creating course", e);
        }
    }

    public void deleteByCourseCode(String courseCode){
        String callProcedure = "{CALL uspDeleteCourse(?)}";

        try (Connection connection = connectionHandler.getConnection();
            CallableStatement statement = connection.prepareCall(callProcedure)){
                statement.setString(1, courseCode);

                statement.executeUpdate();
        } catch (SQLException e){
            if(e.getMessage().contains("Course has students")){
                throw new DaoException("You may not delete a course that has students enrolled", e);
            } else {
                throw new DaoException("Error while deleting course: " + courseCode, e);
            }
        }
    }

    private Course mapToCourse(ResultSet resultSet) throws SQLException {
        return new Course(
            resultSet.getString("CourseCode"),
            resultSet.getString("CourseName"),
            resultSet.getDouble("CourseCredits"));
    }

}
