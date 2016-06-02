package dao;

import java.util.List;
import beans.Transistor;

public interface TransistorDao {
    List<Transistor> list();
    void add(Transistor transistor);
    void update(Transistor transistor);
    void delete(int id);
}