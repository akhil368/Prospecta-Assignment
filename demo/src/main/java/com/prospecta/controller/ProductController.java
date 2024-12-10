package com.prospecta.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.prospecta.dto.ProductDto;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProductController {

	private final RestTemplate restTemplate;

	public ProductController(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Value("${api.base-url}")
	private String baseUrl;

	@GetMapping("/category/{category}")
	public ResponseEntity<List<Object>> getProductByCategory(@PathVariable String category) {
		String url = baseUrl + "/category/" + category;

		try {
			log.info("Fetching products for category: {}", category);

			ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				log.info("Successfully fetched {} products", response.getBody().length);
				return ResponseEntity.ok(Arrays.asList(response.getBody()));
			} else if (response.getBody().length == 0) {
				log.warn("No products found for category: {}", category);
				return new ResponseEntity<>(Arrays.asList(response.getBody()), HttpStatus.NOT_FOUND);
			} else {
				log.error("Failed to fetch products, status: {}", response.getStatusCode());
				return ResponseEntity.status(response.getStatusCode()).build();
			}
		} catch (Exception e) {
			log.error("Error while fetching products for category {}: {}", category, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/add")
	public ResponseEntity<Object> addProduct(@RequestBody @Valid ProductDto productDto) {
		String url = baseUrl;

		try {
			log.info("Adding new product: {}", productDto);

			HttpEntity<ProductDto> request = new HttpEntity<>(productDto);
			ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				log.info("Successfully added product: {}", productDto);
				return ResponseEntity.ok(response.getBody());
			} else {
				log.error("Failed to add product, status: {}", response.getStatusCode());
				return ResponseEntity.status(response.getStatusCode()).build();
			}
		} catch (Exception ex) {
			log.error("Error while adding product: {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
