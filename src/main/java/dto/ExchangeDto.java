package dto;

import entity.Currency;

import java.util.Objects;

public class ExchangeDto {
    private final Currency baseCurrencyId;
    private final Currency targetCurrencyId;
    private final float rate;
    private final float amount;
    private final float convertedAmount;

    public ExchangeDto(Currency baseCurrencyId, Currency targetCurrencyId, float rate, float amount, float convertedAmount) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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
        return Float.compare(rate, that.rate) == 0 && Float.compare(amount, that.amount) == 0 && Float.compare(convertedAmount, that.convertedAmount) == 0 && Objects.equals(baseCurrencyId, that.baseCurrencyId) && Objects.equals(targetCurrencyId, that.targetCurrencyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseCurrencyId, targetCurrencyId, rate, amount, convertedAmount);
    }

    @Override
    public String toString() {
        return "ExchangeDto{" +
               "baseCurrencyId=" + baseCurrencyId +
               ", targetCurrencyId=" + targetCurrencyId +
               ", rate=" + rate +
               ", amount=" + amount +
               ", convertedAmount=" + convertedAmount +
               '}';
    }


}
