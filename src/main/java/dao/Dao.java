package dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    List<T> findAll();
    Optional<T> findById(int id);
    T save(T t);
    void update(T t);
    boolean delete(int id);
}
