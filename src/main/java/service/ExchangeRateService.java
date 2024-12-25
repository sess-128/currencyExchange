package service;

import dao.ExchangeRateDao;
import dto.ExchangeRateDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    private ExchangeRateService(){}

    public List<ExchangeRateDto> findAll () {
        return exchangeRateDao.findAll().stream()
                .map(exchangeRate -> new ExchangeRateDto(
                        exchangeRate.getId(),
                        """
                                %s - %s - %d
                                """.formatted(exchangeRate.getBaseCurrencyId(), exchangeRate.getTargetCurrencyId(), exchangeRate.getRate())
                ))
                .collect(toList());
    }

    public static ExchangeRateService getInstance(){
        return INSTANCE;
    }
}
