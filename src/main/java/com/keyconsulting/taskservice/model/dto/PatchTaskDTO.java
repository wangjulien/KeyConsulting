package com.keyconsulting.taskservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchTaskDTO {
    @NonNull
    private Long id;
    @NonNull
    private Boolean completed;
}
