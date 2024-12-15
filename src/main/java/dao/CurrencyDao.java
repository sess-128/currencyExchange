package dao;

import dto.CurrencyFilter;
import entity.Currency;
import exception.DaoException;
import utils.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

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

    private CurrencyDao() {
    }

    public List<Currency> findAll(CurrencyFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();

        if (filter.code() != null) {
            whereSql.add("code = ?");
            parameters.add(filter.code());
        }
        if (filter.fullName() != null) {
            whereSql.add("full_name = ?");
            parameters.add(filter.fullName());
        }

        parameters.add(filter.limit());
        parameters.add(filter.offset());
        var where = whereSql.stream().collect(joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));

        var sql = FIND_ALL_SQL + where;

        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            System.out.println(preparedStatement);
            var resultSet = preparedStatement.executeQuery();

            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }

            return currencies;

        } catch (SQLException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
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
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
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

    @Override
    public Currency save(Currency currency) {
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt("id"));
            }

            return currency;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Currency currency) {
        try (var connection = ConnectionManager.get(); var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
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
        return new Currency(resultSet.getInt("id"), resultSet.getString("code"), resultSet.getString("full_name"), resultSet.getString("sign"));
    }
}
