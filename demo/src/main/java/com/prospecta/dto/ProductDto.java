package com.prospecta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDto {
	
	 private Long id;

	    @NotNull(message = "Title cannot be null")
	    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
	    private String title;

	    @Positive(message = "Price must be positive")
	    private double price;

	    @NotNull(message = "Category cannot be null")
	    @Size(min = 1, max = 50, message = "Category must be between 1 and 50 characters")
	    private String category;

	    @Size(max = 255, message = "Description must be less than 255 characters")
	    private String description;

	    @NotNull(message = "Image URL cannot be null")
	    private String image;

		
}
