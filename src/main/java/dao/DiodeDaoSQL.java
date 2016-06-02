package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import beans.Diode;

public class DiodeDaoSQL implements DiodeDao {

    private DaoFactory daoFactory;

    DiodeDaoSQL(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
    @Override
    public void add(Diode d) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("INSERT INTO diodes (name, Cd, Vd) VALUES(?, ?, ?);");
            preparedStatement.setString(1, d.getName());
            preparedStatement.setString(2, d.getCd());
            preparedStatement.setString(3, d.getVd());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Diode d) {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("UPDATE diodes SET name = ?, Cd = ?, Vd = ? WHERE id = ?;");
            preparedStatement.setString(1, d.getName());
            preparedStatement.setString(2, d.getCd());
            preparedStatement.setString(3, d.getVd());
            preparedStatement.setString(4, String.valueOf(d.getId()));
            
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
    		preparedStatement = connexion.prepareStatement("DELETE FROM diodes WHERE id = ?;");
    		preparedStatement.setString(1, String.valueOf(id));
        
        	preparedStatement.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    
    @Override
    public List<Diode> list() {
        List<Diode> diodes = new ArrayList<Diode>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT * FROM diodes;");
            
            while (resultat.next()) {
               	Diode d = new Diode(
                		Integer.parseInt(resultat.getString("id")), 
                		resultat.getString("name"),
                		resultat.getString("Cd"),
                		resultat.getString("Vd")
                );

                diodes.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diodes;
    }

}