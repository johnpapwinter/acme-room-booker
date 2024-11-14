package com.acme.roombooker.dto;

import com.acme.roombooker.enums.AcmeRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcmeUserDTO {

    private String name;
    private String email;
    private String username;
    private AcmeRole role;

}
