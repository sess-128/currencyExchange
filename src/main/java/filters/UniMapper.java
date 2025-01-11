package filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import dto.ExchangeRateDto;

import java.util.Optional;

public class UniMapper {

    public static String toJSON (Optional<?> optionalDto) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(optionalDto.get());
    }
    public static String toJSON(CurrencyDto currencyDto) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(currencyDto);
    }
    public static String toJSON(ExchangeRateDto exchangeRateDto) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(exchangeRateDto);
    }
}
