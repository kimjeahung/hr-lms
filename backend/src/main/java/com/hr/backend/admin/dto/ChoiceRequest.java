package com.hr.backend.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChoiceRequest {

    private String choiceText;
    private boolean correct;
    private int sortOrder;
}
