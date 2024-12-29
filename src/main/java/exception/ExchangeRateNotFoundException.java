package exception;

public class ExchangeRateNotFoundException extends RuntimeException{
    public ExchangeRateNotFoundException(String error){super(error);}
    public ExchangeRateNotFoundException(){}
}
