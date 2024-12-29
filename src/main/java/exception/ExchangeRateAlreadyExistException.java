package exception;

public class ExchangeRateAlreadyExistException extends RuntimeException {
  public ExchangeRateAlreadyExistException(Throwable e) {
    super(e);
  }
}
