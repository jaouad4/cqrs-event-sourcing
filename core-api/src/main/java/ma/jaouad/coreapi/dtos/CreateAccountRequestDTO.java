package ma.jaouad.coreapi.dtos;

import lombok.Data;

@Data
public class CreateAccountRequestDTO {
    private double initialBalance;
    private String currency;
}
