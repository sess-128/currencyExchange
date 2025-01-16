package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dto.ExchangeDto;
import dto.ExchangeRateDto;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateNotFoundException;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.math.MathContext.DECIMAL64;
import static java.util.stream.Collectors.toList;

public class ExchangeRateService {
    private static final String CURRENCY_USD_CODE = "USD";
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(), exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate().setScale(2, RoundingMode.HALF_DOWN)
                ))
                .collect(toList());
    }

    public Optional<ExchangeRateDto> findByPair(String pair) {
        var base = pair.substring(0, 3);
        var target = pair.substring(3);

        return exchangeRateDao.findByPair(base, target).stream()
                .findFirst()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(), exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate().setScale(2, RoundingMode.HALF_DOWN)
                ));
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);


        ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrency, targetCurrency, rate.setScale(2, RoundingMode.HALF_DOWN));
        ExchangeRate saved = exchangeRateDao.save(exchangeRate);

        return new ExchangeRateDto(saved.getId(), saved.getBaseCurrency(), saved.getTargetCurrency(), saved.getRate());
    }

    public ExchangeRateDto update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rates) {

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);

        ExchangeRate exchangeRate = exchangeRateDao.findByPair(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(ExchangeRateNotFoundException::new);

        ExchangeRate exchangeRate1 = new ExchangeRate(exchangeRate.getId(), baseCurrency, targetCurrency, rates);
        exchangeRateDao.update(exchangeRate1);

        return new ExchangeRateDto(exchangeRate1.getId(), baseCurrency, targetCurrency, rates.setScale(2, RoundingMode.HALF_DOWN));
    }

    public ExchangeDto convert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        ExchangeRate exchangeRate = getSimpleExchange(baseCurrencyCode, targetCurrencyCode);

        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate());

        return new ExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                convertedAmount.setScale(2, RoundingMode.HALF_DOWN)
        );
    }

    private ExchangeRate getSimpleExchange(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByPair(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            exchangeRate = exchangeRateDao.findByPair(targetCurrencyCode, baseCurrencyCode);
            if (exchangeRate.isPresent()) {
                BigDecimal invertedRate = BigDecimal.ONE.divide(exchangeRate.get().getRate(), DECIMAL64);
                return new ExchangeRate(
                        null,
                        exchangeRate.get().getTargetCurrency(),
                        exchangeRate.get().getBaseCurrency(),
                        invertedRate
                );
            }
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = getCrossExchange(baseCurrencyCode, targetCurrencyCode);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = getCrossExchange(targetCurrencyCode, baseCurrencyCode);
        }

        return new ExchangeRate(
                null,
                exchangeRate.get().getTargetCurrency(),
                exchangeRate.get().getTargetCurrency(),
                exchangeRate.get().getRate()
        );
    }

    private Optional<ExchangeRate> getCrossExchange(String baseCurrencyCode, String targetCurrencyCode) {

        List<ExchangeRate> ratesWithUsdBase = new ArrayList<>();

        Optional<ExchangeRate> fromUSDToBase = exchangeRateDao.findByPair(CURRENCY_USD_CODE, baseCurrencyCode);
        Optional<ExchangeRate> fromUSDToTarget = exchangeRateDao.findByPair(CURRENCY_USD_CODE, targetCurrencyCode);

        ratesWithUsdBase.add(fromUSDToBase.get());
        ratesWithUsdBase.add(fromUSDToTarget.get());

        BigDecimal usdToBaseRate = fromUSDToBase.get().getRate();
        BigDecimal usdToTargetRate = fromUSDToTarget.get().getRate();

        BigDecimal baseToTargetRate = usdToTargetRate.divide(usdToBaseRate, DECIMAL64);

        ExchangeRate exchangeRate = new ExchangeRate(
                null,
                fromUSDToBase.get().getTargetCurrency(),
                fromUSDToTarget.get().getTargetCurrency(),
                baseToTargetRate
        );

        return Optional.of(exchangeRate);
    }


    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
