import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQliteRunner {

    public static void main(String[] args) {
        var sql = """
                CREATE TABLE exchange_rates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    base_currency_id INTEGER ,
                    target_currency_id INTEGER ,
                    rate REAL,
                    FOREIGN KEY (base_currency_id) REFERENCES currencies (id),
                    FOREIGN KEY (target_currency_id) REFERENCES currencies (id),
                    UNIQUE (base_currency_id, target_currency_id)
                )
                """;

        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
