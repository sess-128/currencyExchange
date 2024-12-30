package dto;

import java.util.Objects;

public class CurrencyDto {

    private final Integer id;
    private final String code;
    private final String name;
    private final String sign;

    public CurrencyDto(Integer id, String code, String name, String sign) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDto that = (CurrencyDto) o;
        return id == that.id && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(sign, that.sign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, sign);
    }


    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", code='" + code + '\'' +
               ", fullName='" + name + '\'' +
               ", sign='" + sign + '\'' +
               '}';
    }
}

