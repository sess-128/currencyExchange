package dto;

import entity.Currency;

import java.util.Objects;

public class ExchangeDto {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final float rate;
    private final float amount;
    private final float convertedAmount;

    public ExchangeDto(Currency baseCurrency, Currency targetCurrency, float rate, float amount, float convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public float getAmount() {
        return amount;
    }

    public float getConvertedAmount() {
        return convertedAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExchangeDto that = (ExchangeDto) o;
        return Float.compare(rate, that.rate) == 0 && Float.compare(amount, that.amount) == 0 && Float.compare(convertedAmount, that.convertedAmount) == 0 && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(targetCurrency, that.targetCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    @Override
    public String toString() {
        return "ExchangeDto{" +
               "baseCurrencyId=" + baseCurrency +
               ", targetCurrencyId=" + targetCurrency +
               ", rate=" + rate +
               ", amount=" + amount +
               ", convertedAmount=" + convertedAmount +
               '}';
    }


}
