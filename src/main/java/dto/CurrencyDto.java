package dto;

import java.util.Objects;

public class CurrencyDto {

    private final int id;
    private final String description;

    public CurrencyDto(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDto that = (CurrencyDto) o;
        return id == that.id && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "CurrencyDto{" +
               "id=" + id +
               ", description='" + description + '\'' +
               '}';
    }
}
