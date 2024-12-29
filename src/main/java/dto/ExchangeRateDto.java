package dto;

import entity.Currency;
import java.util.Objects;

public class ExchangeRateDto {
    private final Integer id;
    private final Currency baseCurrencyId;
    private final Currency targetCurrencyId;
    private final float rate;


    public ExchangeRateDto(Integer id, Currency baseCurrencyId, Currency targetCurrencyId, float rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public Currency getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public Currency getTargetCurrencyId() {
        return targetCurrencyId;
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
        return Float.compare(rate, that.rate) == 0 && Objects.equals(id, that.id) && Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCurrencyId, targetCurrencyId, rate);
    }

    @Override
    public String toString() {
        return "ExchangeRateDto{" +
               "id=" + id +
               ", baseCurrencyId=" + baseCurrencyId +
               ", targetCurrencyId=" + targetCurrencyId +
               ", rate=" + rate +
               '}';
    }
}
