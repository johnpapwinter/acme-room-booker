package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.AcmeRole;
import lombok.Data;

@Data
public class AcmeUserDTO {

    private String name;
    private String email;
    private String username;
    private AcmeRole role;

}
