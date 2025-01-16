package utils.errorHandle;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Validation {
    private static final int SIGN_LENGTH = 1;
    private static final int CURRENCY_NANE_MAX_LENGTH = 15;
    private static Set<String> currencyCodes;

    public static boolean isValidCurrencyCode(String code) {
        if (currencyCodes == null) {
            Set<Currency> currencies = Currency.getAvailableCurrencies();
            currencyCodes = currencies.stream()
                    .map(Currency::getCurrencyCode)
                    .collect(toSet());
        }
        return currencyCodes.contains(code);
    }

    public static boolean isCorrectLength(String name, String sign) {
        return name.length() <= CURRENCY_NANE_MAX_LENGTH && sign.length() <= SIGN_LENGTH;
    }

    public static boolean isValidRateAndAmount(BigDecimal check) {
        if (check.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (check.scale() > 6) {
            return false;
        }
        return true;
    }
}
