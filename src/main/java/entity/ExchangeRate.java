package entity;

public class ExchangeRate {
    private Integer id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private float rate;

    public ExchangeRate(Integer id, Currency baseCurrency, Currency targetCurrency, float rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeRate() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" + "\n" +
               "id=" + id + "\n" +
               ", baseCurrencyId=" + baseCurrency + "\n" +
               ", TargetCurrencyId=" + targetCurrency + "\n" +
               ", rate=" + rate + "\n" +
               '}';
    }
}
