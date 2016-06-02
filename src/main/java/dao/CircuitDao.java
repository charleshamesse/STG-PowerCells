package dao;

import java.util.List;
import beans.Circuit;

public interface CircuitDao {
    List<Circuit> list();
    Circuit get(int id);
    void add(Circuit circuit);
    void update(Circuit circuit);
    void delete(int id);
}