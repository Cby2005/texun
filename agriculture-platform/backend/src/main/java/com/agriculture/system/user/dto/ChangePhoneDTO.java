package com.agriculture.system.user.dto;

import lombok.Data;

@Data
public class ChangePhoneDTO {
    private String newPhone;
    private String code;
}
