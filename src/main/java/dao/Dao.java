package dao;

import java.util.List;
import java.util.Optional;

public interface Dao<E> {
    List<E> findAll();
    Optional<E> findById(int id);
    E save(E e);
    void update(E e);
    boolean delete(int id);
}
