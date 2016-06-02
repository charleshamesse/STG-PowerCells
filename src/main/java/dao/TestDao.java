package dao;

import java.util.List;

import beans.Test;
import beans.Transistor;

public interface TestDao {
    List<Test> list();
    void add(Test test);
    void delete(int id);
}