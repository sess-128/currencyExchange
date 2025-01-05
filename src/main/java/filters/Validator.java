package filters;

import java.util.Currency;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Validator {
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
//    private static final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();
//    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{3}$";
//    private static final String EXCHANGE_RATE_REGEX = "^[A-Z]{7}$";
//    private static final int SIGN_LENGTH = 1;
//
//    public static boolean isCorrectParameterCurr(String parameter, HttpServletResponse response) throws IOException {
//        if (parameter == null || !parameter.matches(CURRENCY_CODE_REGEX)){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write(errorsHandler.getMessage(response));
//            return false;
//        }
//        return true;
//    }
//    public static boolean isCorrectParameterRate(String parameter, HttpServletResponse response) throws IOException {
//        if (parameter == null || !parameter.matches(EXCHANGE_RATE_REGEX)){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write(errorsHandler.getMessage(response));
//            return false;
//        }
//        return true;
//    }
//    public static boolean isCorrectParameters(HttpServletResponse response, String sign, String... parameters) throws IOException {
//        // Проверяем знак
//        if (sign.length() != SIGN_LENGTH) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().write(errorsHandler.getMessage(response));
//            return false;
//        }
//
//        // Проверяем все параметры
//        for (String parameter : parameters) {
//            if (!isCorrectParameterCurr(parameter, response)) {
//                return false; // Если один из параметров не корректен
//            }
//        }
//        return true; // Все параметры корректны
//    }
//
//    public static boolean isEmptyDto(HttpServletResponse resp, Optional<?> optionalDto) throws IOException {
//        if (optionalDto.isEmpty()) {
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            resp.getWriter().write(errorsHandler.getMessage(resp));
//            return true;
//        }
//        return false;
//    }
}

