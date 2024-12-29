package service;

import dao.CurrencyDao;
import dto.CurrencyDto;
import entity.Currency;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();

    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private CurrencyService() {}

    public List<CurrencyDto> findAll () {
        return currencyDao.findAll().stream()
                .map(currency -> new CurrencyDto(
                        currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign()
                ))
                .collect(toList());
    }

    public Optional<CurrencyDto> findByCode(String code){
        return currencyDao.findByCode(code).stream()
                .findFirst()
                .map(currency -> new CurrencyDto(
                        currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign()
                ));
    }

    public CurrencyDto save(Currency currency) {
        Currency save = currencyDao.save(currency);
        return new CurrencyDto(save.getId(), save.getCode(), save.getFullName(), save.getSign());
    }

    public boolean currencyExist (String code) {
        return findByCode(code).isPresent();
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }
}
