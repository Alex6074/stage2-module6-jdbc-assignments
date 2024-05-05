package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User findUserById(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(findUserByIdSQL)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                int age = resultSet.getInt("age");

                return new User(id, firstName, lastName, age);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User findUserByName(String userName) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(findUserByNameSQL)) {
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return (User) resultSet.getObject(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<User> findAllUser() {
        List<User> result = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(findAllUserSQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(new User(resultSet.getLong("id"), resultSet.getString("firstname"), resultSet.getString("lastname"), resultSet.getInt("age")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(updateUserSQL)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setInt(3, user.getAge());
            statement.setLong(4, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteUser)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}