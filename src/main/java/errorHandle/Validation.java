package errorHandle;

import java.util.Currency;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Validation {
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
}
