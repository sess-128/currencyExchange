package dao;

import entity.Currency;
import entity.ExchangeRate;
import exception.CurrencyNotFoundException;
import exception.DaoException;
import exception.ExchangeRateAlreadyExistException;
import utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao implements Dao<ExchangeRate> {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final String DELETE_SQL = """
            DELETE FROM exchange_rates
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates (
                base_currency_id,
                target_currency_id,
                rate)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
                UPDATE exchange_rates
                SET rate = ?
                WHERE base_currency_id = ? AND target_currency_id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   base_currency_id,
                   target_currency_id,
                   rate
            FROM exchange_rates
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    private static final String FIND_BY_PAIR_SQL = FIND_ALL_SQL + """
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
              AND target_currency_id = (SELECT id FROM currencies WHERE code = ?);
            """;

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private ExchangeRateDao() {
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<ExchangeRate> exchangeRates = new ArrayList<>();

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(int id) {

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;

            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }

            return Optional.ofNullable(exchangeRate);

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<ExchangeRate> findByPair(String base, String target) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_PAIR_SQL)) {
            preparedStatement.setString(1, base);
            preparedStatement.setString(2, target);

            var resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;

            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }

            return Optional.ofNullable(exchangeRate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrencyId().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrencyId().getId());
            preparedStatement.setFloat(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt(1));
            }

            return exchangeRate;

        } catch (SQLException e) {
            throw new ExchangeRateAlreadyExistException(e);
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setFloat(1, exchangeRate.getRate());
            preparedStatement.setObject(2, exchangeRate.getBaseCurrencyId().getId());
            preparedStatement.setObject(3, exchangeRate.getTargetCurrencyId().getId());

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                currencyDao.findById(resultSet.getInt("base_currency_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                currencyDao.findById(resultSet.getInt("target_currency_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getLong("rate")
        );
    }
}
