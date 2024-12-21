import dao.CurrencyDao;
import dao.ExchangeRateDao;
import entity.Currency;
import entity.ExchangeRate;
import utils.ConnectionManager;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class SQliteRunner {

    public static void main(String[] args) {
        var exchangeRate = ExchangeRateDao.getInstance().findAll();
        System.out.println(exchangeRate);


    }
}
