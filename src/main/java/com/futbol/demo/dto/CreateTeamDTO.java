package com.futbol.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTeamDTO {
    @NotBlank
    private String name;

    private String location;
    
    private String category;
    
    private String description;
    
   
    
    


}

