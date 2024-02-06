package com.chatop.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;
    private String name;
    private int surface;
    private int price;
    private String picture;
    private String description;
    private Long owner_id;
    private Date created_at;
    private Date updated_at;
}
