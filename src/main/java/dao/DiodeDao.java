package dao;

import java.util.List;

import beans.Diode;

public interface DiodeDao {
    List<Diode> list();
    void add(Diode diode);
    void update(Diode diode);
    void delete(int id);
}