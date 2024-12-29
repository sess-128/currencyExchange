package exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(Throwable throwable) {
        super(throwable);
    }
    public CurrencyNotFoundException() {}
}
