package dao;

import entity.Currency;
import exception.CurrencyAlreadyExistException;
import exception.CurrencyNotFoundException;
import exception.DaoException;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<Currency> {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String DELETE_SQL = """
            DELETE FROM currencies
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE currencies
            SET code = ?,
                full_name = ?,
                sign = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   code,
                   full_name,
                   sign
            FROM currencies
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    private CurrencyDao() {
    }

    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<Currency> currencies = new ArrayList<>();

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Currency> findById(int id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Currency> findById(int id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Currency currency = null;

            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);

            var resultSet = preparedStatement.executeQuery();
            Currency currency = null;

            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }

            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new CurrencyNotFoundException(e);
        }
    }

    @Override
    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();
            Integer id = null;

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
            return new Currency(id, currency.getCode(), currency.getName(), currency.getSign());

        } catch (SQLException e) {
            throw new CurrencyAlreadyExistException(e);
        }
    }

    @Override
    public void update(Currency currency) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.setInt(4, currency.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public boolean delete(int id) {
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign"));
    }
}
