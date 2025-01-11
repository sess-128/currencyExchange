package dto;

import model.Currency;

import java.math.BigDecimal;
import java.util.Objects;

public record ExchangeDto(Currency baseCurrency,
                          Currency targetCurrency,
                          BigDecimal rate,
                          BigDecimal amount,
                          BigDecimal convertedAmount) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeDto that = (ExchangeDto) o;
        return Objects.equals(baseCurrency, that.baseCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(baseCurrency);
    }

    @Override
    public String toString() {
        return "ExchangeDto{" +
               "baseCurrency=" + baseCurrency +
               ", targetCurrency=" + targetCurrency +
               ", rate=" + rate +
               ", amount=" + amount +
               ", convertedAmount=" + convertedAmount +
               '}';
    }
}
