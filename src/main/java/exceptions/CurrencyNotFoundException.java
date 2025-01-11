package exceptions;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException(Throwable e){super(e);}
    public CurrencyNotFoundException(){}
}
