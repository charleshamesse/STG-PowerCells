package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import beans.Transistor;

public class TransistorDaoSQL implements TransistorDao {

    private DaoFactory daoFactory;

    TransistorDaoSQL(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    
    @Override
    public void add(Transistor t) {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("INSERT INTO transistors (name, Vgsth, Gm, Cgd, Cgs, Cds, ROn, Ls) VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, t.getName());
            preparedStatement.setString(2, t.getVgsth());
            preparedStatement.setString(3, t.getGm());
            preparedStatement.setString(4, t.getCgd());
            preparedStatement.setString(5, t.getCgs());
            preparedStatement.setString(6, t.getCds());
            preparedStatement.setString(7, t.getROn());
            preparedStatement.setString(8, t.getLs());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Transistor t) {
    	Connection connexion = null;
        PreparedStatement preparedStatement = null;
        try {
            connexion = daoFactory.getConnection();
            preparedStatement = connexion.prepareStatement("UPDATE transistors SET name = ?, Vgsth = ?, Gm = ?, Cgd = ?, Cgs = ?, Cds = ?, ROn = ?, Ls = ? WHERE id = ?;");
            preparedStatement.setString(1, t.getName());
            preparedStatement.setString(2, t.getVgsth());
            preparedStatement.setString(3, t.getGm());
            preparedStatement.setString(4, t.getCgd());
            preparedStatement.setString(5, t.getCgs());
            preparedStatement.setString(6, t.getCds());
            preparedStatement.setString(7, t.getROn());
            preparedStatement.setString(8, t.getLs());
            preparedStatement.setString(9, String.valueOf(t.getId()));
            
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
    		preparedStatement = connexion.prepareStatement("DELETE FROM transistors WHERE id = ?;");
    		preparedStatement.setString(1, String.valueOf(id));
        
        	preparedStatement.executeUpdate();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    
    @Override
    public List<Transistor> list() {
        List<Transistor> transistors = new ArrayList<Transistor>();
        Connection connexion = null;
        Statement statement = null;
        ResultSet resultat = null;

        try {
            connexion = daoFactory.getConnection();
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT * FROM transistors;");
            
            while (resultat.next()) {
                Transistor transistor = new Transistor(
                		Integer.parseInt(resultat.getString("id")), 
                		resultat.getString("name"),
                		resultat.getString("Vgsth"),
                		resultat.getString("Gm"),
                		resultat.getString("Cgd"),
                		resultat.getString("Cgs"),
                		resultat.getString("Cds"),
                		resultat.getString("ROn"),
                		resultat.getString("Ls")
                );

                transistors.add(transistor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transistors;
    }

}