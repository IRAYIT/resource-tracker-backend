package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SendEmailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String emailBody;
    private String subject;
    private Boolean isTrue;
}
