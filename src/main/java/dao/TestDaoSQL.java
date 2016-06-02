package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import beans.Test;
import beans.Transistor;

public class TestDaoSQL implements TestDao {

    private DaoFactory daoFactory;

    TestDaoSQL(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void add(Test test) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("INSERT INTO tests(type, component1, component2, voltage, current, dutycycle, losses, frequency, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, test.getType());
            preparedStatement.setString(2, test.getComponent1());
            preparedStatement.setString(3, test.getComponent2());
            preparedStatement.setString(4, test.getVoltage());
            preparedStatement.setString(5, test.getCurrent());
            preparedStatement.setString(6, test.getDutyCycle());
            preparedStatement.setString(7, test.getLosses());
            preparedStatement.setString(8, test.getFrequency());
            preparedStatement.setString(9, test.getDate());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void delete(int id) {
    	Connection connexion = null;
    	PreparedStatement preparedStatement = null;
    	try {
    		connexion = daoFactory.getConnection();
    		preparedStatement = connexion.prepareStatement("DELETE FROM tests WHERE id = ?;");
    		preparedStatement.setString(1, String.valueOf(id));
        
        	preparedStatement.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }

    @Override
    public List<Test> list() {
        List<Test> tests = new ArrayList<Test>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT * FROM tests;");

            while (resultat.next()) {
                Test test = new Test(
                		Integer.parseInt(resultat.getString("id")),
                		resultat.getString("type"),
                		resultat.getString("component1"),
                		resultat.getString("component2"),
                		resultat.getString("voltage"),
                		resultat.getString("current"),
                		resultat.getString("dutycycle"),
                		resultat.getString("losses"),
                		resultat.getString("frequency"),
                		resultat.getString("date")
                );

                tests.add(test);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

}