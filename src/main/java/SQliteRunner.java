import dao.CurrencyDao;
import entity.Currency;
import utils.ConnectionManager;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class SQliteRunner {

    public static void main(String[] args) {
        var sql = """
                INSERT INTO currencies (code, full_name, sign
                ) VALUES ('RUB', 'Russian Ruble', '₽')
                """;

//        Optional<Currency> currency = CurrencyDao.getInstance().findById(3);
//        System.out.println(currency);

//        Currency currency = new Currency(5,"KZT", "Tenge", "₸");
//        CurrencyDao.getInstance().update(currency);


        CurrencyDao.getInstance().delete(4);
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
