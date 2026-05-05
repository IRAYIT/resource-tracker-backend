package com.ikonicit.resource.tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeletedLeaveResponseDTO {
    private int id;
    private String status;
    private String message;
}
