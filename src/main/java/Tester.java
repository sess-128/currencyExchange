import dao.CurrencyDao;

public class Tester {
    public static void main(String[] args) {

        var currencies = CurrencyDao.getInstance().findAll();
        System.out.println(currencies);
    }
}
