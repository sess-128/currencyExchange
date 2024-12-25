package service;

import dao.CurrencyDao;
import dto.CurrencyDto;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {}

    public List<CurrencyDto> findAllByPair (int id) {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(),
                        """
                                %s - %s - %s
                                """.formatted(currency.getCode(), currency.getFullName(), currency.getSign())
                ))
                .collect(toList());
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
