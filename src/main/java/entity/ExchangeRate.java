package entity;

public class ExchangeRate {
    private int id;
    private Currency baseCurrencyId;
    private Currency targetCurrencyId;
    private long rate;

    public ExchangeRate(int id, Currency baseCurrencyId, Currency targetCurrencyId, long rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRate() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(Currency baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public Currency getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(Currency targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" + "\n" +
               "id=" + id + "\n" +
               ", baseCurrencyId=" + baseCurrencyId + "\n" +
               ", TargetCurrencyId=" + targetCurrencyId + "\n" +
               ", rate=" + rate + "\n" +
               '}';
    }
}
