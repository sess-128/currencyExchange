package dto;

public record CurrencyFilter(int limit,
                             int offset,
                             String fullName,
                             String code) {

}
