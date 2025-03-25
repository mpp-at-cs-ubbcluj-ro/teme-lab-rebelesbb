import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry("Finding cars by manufacturer: {}", manufacturerN);
        Connection con = dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Masini WHERE manufacturer = ?")) {
            preStmt.setString(1, manufacturerN);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String manufacturer = result.getString("manufacturer");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB: " + e);
        }
        logger.traceExit(cars);
        return cars;

    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {

        logger.traceEntry("Finding cars between years: {} - {}", min, max);
        Connection con = dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM Masini WHERE year BETWEEN ? AND ?")) {
            preStmt.setInt(1, min);
            preStmt.setInt(2, max);
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("id");
                    String manufacturer = result.getString("manufacturer");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB: " + e);
        }
        logger.traceExit(cars);
        return cars;
    }

    @Override
    public void add(Car elem) {
        //to do
        logger.traceEntry("saving task {}", elem);
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = conn.prepareStatement("insert into Masini(manufacturer, model, year) values (?, ?, ?)")){
            preparedStatement.setString(1, elem.getManufacturer());
            preparedStatement.setString(2, elem.getModel());
            preparedStatement.setInt(3, elem.getYear());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);
        }catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Integer integer, Car elem) {
        logger.traceEntry("Updating car with id {}", integer);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE Masini SET manufacturer=?, model=?, year=? WHERE id=?")) {
            preStmt.setString(1, elem.getManufacturer());
            preStmt.setString(2, elem.getModel());
            preStmt.setInt(3, elem.getYear());
            preStmt.setInt(4, integer);
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} rows", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB: " + e);
        }
        logger.traceExit();

    }

    @Override
    public Iterable<Car> findAll() {
        logger.traceEntry();
        Connection conn = dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();
        try(PreparedStatement preparedStatement = conn.prepareStatement("select * from Masini");
        ResultSet resultSet = preparedStatement.executeQuery()){
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String manufacturer = resultSet.getString("manufacturer");
                String model = resultSet.getString("model");
                int year = resultSet.getInt("year");
                Car car = new Car(manufacturer, model, year);
                car.setId(id);
                cars.add(car);
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
        return cars;
    }
}
