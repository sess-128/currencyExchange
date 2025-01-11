package dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    List<E> findAll();
    Optional<E> findById(K id);
    E save(E e);
    void update(E e);
}
