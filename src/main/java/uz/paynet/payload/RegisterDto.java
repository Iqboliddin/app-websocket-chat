package uz.paynet.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RegisterDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String prePassword;
}
