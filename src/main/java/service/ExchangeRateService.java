package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dto.ExchangeDto;
import dto.ExchangeRateDto;
import entity.Currency;
import entity.ExchangeRate;
import exception.CurrencyNotFoundException;
import exception.ExchangeRateNotFoundException;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private ExchangeRateService() {
    }

    public List<ExchangeRateDto> findAll() {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(), exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId(), exchangeRate.getRate()
                ))
                .collect(toList());
    }

    public Optional<ExchangeRateDto> findByPair(String pair) {
        var base = pair.substring(0, 3);
        var target = pair.substring(3);

        return exchangeRateDao.findByPair(base, target).stream()
                .findFirst()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(), exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId(), exchangeRate.getRate()
                ));
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, float rate) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);


        ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrency, targetCurrency, rate);
        ExchangeRate saved = exchangeRateDao.save(exchangeRate);

        return new ExchangeRateDto(saved.getId(), saved.getBaseCurrencyId(), saved.getTargetCurrencyId(), saved.getRate());
    }

    public ExchangeRateDto update(String baseCurrencyCode, String targetCurrencyCode, float rates) {

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);

        ExchangeRate exchangeRate = exchangeRateDao.findByPair(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(ExchangeRateNotFoundException::new);

        ExchangeRate exchangeRate1 = new ExchangeRate(exchangeRate.getId(), baseCurrency, targetCurrency, rates);
        exchangeRateDao.update(exchangeRate1);

        return new ExchangeRateDto(exchangeRate1.getId(), baseCurrency, targetCurrency, rates);
    }

    public ExchangeDto convert(String baseCurrencyCode, String targetCurrencyCode, float amount) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        String currencyUSD = "USD";

        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByPair(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate.isPresent()) {
            float rate = exchangeRate.get().getRate();
            float convertedAmount = rate * amount;
            return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, convertedAmount);
        }

        Optional<ExchangeRate> exchangeRateReverse = exchangeRateDao.findByPair(targetCurrencyCode, baseCurrencyCode);
        if (exchangeRateReverse.isPresent()) {
            float rateReverse = 1 / (exchangeRateReverse.get().getRate());
            float convertedAmount = rateReverse * amount;
            return new ExchangeDto(targetCurrency, baseCurrency, rateReverse, amount, convertedAmount);
        }

        Optional<ExchangeRate> fromUSDToBase = exchangeRateDao.findByPair(currencyUSD, baseCurrencyCode);
        Optional<ExchangeRate> fromUSDToTarget = exchangeRateDao.findByPair(currencyUSD, targetCurrencyCode);

        float baseRate = fromUSDToBase.get().getRate();
        float targetRate = fromUSDToTarget.get().getRate();

        float rateFromUsd = targetRate / baseRate;
        float convertedAmount = rateFromUsd * amount;

        return new ExchangeDto(baseCurrency, targetCurrency, rateFromUsd, amount, convertedAmount);
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
