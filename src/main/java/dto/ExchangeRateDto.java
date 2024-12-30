package dto;

import entity.Currency;
import java.util.Objects;

public class ExchangeRateDto {
    private final Integer id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final float rate;


    public ExchangeRateDto(Integer id, Currency baseCurrency, Currency targetCurrency, float rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public float getRate() {
        return rate;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeRateDto that = (ExchangeRateDto) o;
        return Float.compare(rate, that.rate) == 0 && Objects.equals(id, that.id) && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrency, targetCurrency, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRateDto{" +
               "id=" + id +
               ", baseCurrencyId=" + baseCurrency +
               ", targetCurrencyId=" + targetCurrency +
               ", rate=" + rate +
               '}';
    }
}
