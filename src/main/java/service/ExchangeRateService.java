package service;

import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dto.ExchangeDto;
import dto.ExchangeRateDto;
import model.Currency;
import model.ExchangeRate;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateNotFoundException;

import java.math.BigDecimal;
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
                        exchangeRate.getId(), exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate()
                ))
                .collect(toList());
    }

    public Optional<ExchangeRateDto> findByPair(String pair) {
        var base = pair.substring(0, 3);
        var target = pair.substring(3);

        return exchangeRateDao.findByPair(base, target).stream()
                .findFirst()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(), exchangeRate.getBaseCurrency(), exchangeRate.getTargetCurrency(), exchangeRate.getRate()
                ));
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);


        ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrency, targetCurrency, rate);
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

        return new ExchangeRateDto(exchangeRate1.getId(), baseCurrency, targetCurrency, rates);
    }

    public Optional<ExchangeDto> convert2(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Optional<ExchangeDto> currentConvert = getCurrentConvert(baseCurrencyCode, targetCurrencyCode, amount);
        if (currentConvert.isPresent()) {
            return currentConvert;
        }
        Optional<ExchangeDto> reverseConvert = getReverseConvert(baseCurrencyCode, targetCurrencyCode, amount);
        if (reverseConvert.isPresent()){
            return reverseConvert;
        }

        return getUsdConvert(baseCurrencyCode, targetCurrencyCode, amount);
    }


    private Optional<ExchangeDto> getUsdConvert (String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        String usd = "UDS";

        Optional<ExchangeRate> fromUSDToBase = exchangeRateDao.findByPair(usd, baseCurrencyCode);
        Optional<ExchangeRate> fromUSDToTarget = exchangeRateDao.findByPair(usd, targetCurrencyCode);

        BigDecimal baseRate = fromUSDToBase.get().getRate();
        BigDecimal targetRate = fromUSDToTarget.get().getRate();

        BigDecimal rate = targetRate.divide(baseRate);
        BigDecimal convertedAmount = rate.multiply(amount);

        return Optional.of(new ExchangeDto(
                fromUSDToBase.get().getBaseCurrency(),
                fromUSDToTarget.get().getTargetCurrency(),
                rate,
                amount,
                convertedAmount));
    }

    private Optional<ExchangeDto> getCurrentConvert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Optional<ExchangeRateDto> pair = findByPair(baseCurrencyCode + targetCurrencyCode);
        BigDecimal rate = pair.get().rate();
        BigDecimal convertedAmount = rate.multiply(amount);

        Optional<Currency> base = currencyDao.findByCode(baseCurrencyCode);
        Optional<Currency> target = currencyDao.findByCode(targetCurrencyCode);
        return Optional.of(new ExchangeDto(
                base.get(),
                target.get(),
                rate,
                amount,
                convertedAmount));
    }

    private Optional<ExchangeDto> getReverseConvert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount){
        Optional<ExchangeRateDto> pair = findByPair(targetCurrencyCode + baseCurrencyCode);
        BigDecimal rate = BigDecimal.ONE.divide(pair.get().rate());
        BigDecimal convertedAmount = rate.multiply(amount);
        Optional<Currency> base = currencyDao.findByCode(baseCurrencyCode);
        Optional<Currency> target = currencyDao.findByCode(targetCurrencyCode);
        return Optional.of(new ExchangeDto(
                base.get(),
                target.get(),
                rate,
                amount,
                convertedAmount));

    }

    public ExchangeDto convert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(CurrencyNotFoundException::new);
        String currencyUSD = "USD";

        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByPair(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate.isPresent()) {
            BigDecimal rate = exchangeRate.get().getRate();
            BigDecimal convertedAmount = rate.multiply(amount);
            return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, convertedAmount);
        }

        Optional<ExchangeRate> exchangeRateReverse = exchangeRateDao.findByPair(targetCurrencyCode, baseCurrencyCode);
        if (exchangeRateReverse.isPresent()) {
            BigDecimal rateReverse = BigDecimal.ONE.divide(exchangeRateReverse.get().getRate());
            BigDecimal convertedAmount = rateReverse.multiply(amount);
            return new ExchangeDto(targetCurrency, baseCurrency, rateReverse, amount, convertedAmount);
        }

        Optional<ExchangeRate> fromUSDToBase = exchangeRateDao.findByPair(currencyUSD, baseCurrencyCode);
        Optional<ExchangeRate> fromUSDToTarget = exchangeRateDao.findByPair(currencyUSD, targetCurrencyCode);

        BigDecimal baseRate = fromUSDToBase.get().getRate();
        BigDecimal targetRate = fromUSDToTarget.get().getRate();

        BigDecimal rateFromUsd = targetRate.divide(baseRate);
        BigDecimal convertedAmount = rateFromUsd.multiply(amount);

        return new ExchangeDto(baseCurrency, targetCurrency, rateFromUsd, amount, convertedAmount);
    }


    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }
}
