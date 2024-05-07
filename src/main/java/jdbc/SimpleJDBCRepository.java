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

    private static final String createUserSQL = "INSERT INTO myusers (id, firstname, lastname, age) VALUES (?, ?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        Long id = null;
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getAge());
            ps.execute();
            ResultSet resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                id =  resultSet.getLong(1);
            }

            ps.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    public User findUserById(Long userId) {
        User user = new User();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getInt("age"));
            }

            ps.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = new User();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
                user.setFirstName(resultSet.getString(2));
                user.setLastName(resultSet.getString(3));
                user.setAge(resultSet.getInt(4));
            }

            ps.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public List<User> findAllUser() {
        List<User> result = new ArrayList<>();
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result.add(new User(resultSet.getLong("id"), resultSet.getString("firstname"), resultSet.getString("lastname"), resultSet.getInt("age")));
            }

            ps.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            if(ps.executeUpdate() > 0) {
                ps.close();
                connection.close();
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void deleteUser(Long userId) {
        try{
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SimpleJDBCRepository repository = new SimpleJDBCRepository();

        //User user = new User(4L, "dfkl", "Consdfdsnor", 23);
        //System.out.println("createUser() method output: " + repository.createUser(user));

        User user1 = repository.findUserById(8L);
        System.out.println("findUserById() method output: " + user1);

        User user3 = repository.findUserByName("Sarah");
        System.out.println("findUserByName() method output: " + user3);

//        List<User> userList = repository.findAllUser();
//        System.out.println("findAllUser() method output: ");
//        if (userList!=null) {
//            for (User user: userList) {
//                System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());
//            }
//        }

//        User user = new User(11L, "Josh", "Turner", 45);
//        System.out.println("updateUser() method output:");
//        if (repository.updateUser(user)!=null) {
//            System.out.println("User: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());
//            System.out.println("has been updated!");
//        } else {
//            System.out.println("User update failed!");
//        }

        //System.out.println("deleteUser() method execution");
        //repository.deleteUser(6L);
    }
}