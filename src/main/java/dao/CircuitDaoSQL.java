package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import beans.Circuit;

public class CircuitDaoSQL implements CircuitDao {

    private DaoFactory daoFactory;

    CircuitDaoSQL(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
    @Override
    public void add(Circuit c) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("INSERT INTO circuits (shortName, name, variables) VALUES(?, ?, ?);");
            preparedStatement.setString(1, c.getShortName());
            preparedStatement.setString(2, c.getName());
            preparedStatement.setString(3, c.getVariablesAsString());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Circuit c) {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("UPDATE circuits SET shortName = ?, name = ?, variables = ? WHERE id = ?;");
            preparedStatement.setString(1, c.getShortName());
            preparedStatement.setString(2, c.getName());
            preparedStatement.setString(3, c.getVariablesAsString());
            preparedStatement.setString(4, String.valueOf(c.getId()));
            
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
    		preparedStatement = connexion.prepareStatement("DELETE FROM circuits WHERE id = ?;");
    		preparedStatement.setString(1, String.valueOf(id));
        
        	preparedStatement.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    
    @Override
    public List<Circuit> list() {
        List<Circuit> circuits = new ArrayList<Circuit>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT * FROM circuits;");
            
            while (resultat.next()) {
                Circuit circuit = new Circuit(
                		Integer.parseInt(resultat.getString("id")), 
                		resultat.getString("shortName"),
                		resultat.getString("name"),
                		resultat.getString("variables")
                );

                circuits.add(circuit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return circuits;
    }
    
    @Override
    public Circuit get(int id) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultat = null;
        Circuit circuit = new Circuit(0, "", "", "");

        try {
            connexion = daoFactory.getConnection();

    		preparedStatement = connexion.prepareStatement("SELECT * FROM circuits WHERE id = ?;");
    		preparedStatement.setString(1, String.valueOf(id));
        	resultat = preparedStatement.executeQuery();
            
            if (resultat != null && resultat.next()) {
                circuit = new Circuit(
                		Integer.parseInt(resultat.getString("id")), 
                		resultat.getString("shortName"),
                		resultat.getString("name"),
                		resultat.getString("variables")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return circuit;
    }

}