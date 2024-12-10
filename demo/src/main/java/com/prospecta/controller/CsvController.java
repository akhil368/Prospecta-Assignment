package com.prospecta.controller;


import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.prospecta.service.CsvService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/csv")
public class CsvController {
	
	@Autowired
	private CsvService csvService;

    @PostMapping("/process")
    public ResponseEntity<byte[]> processCsv(@RequestPart("file") MultipartFile file) {
        try {
        	if (file.isEmpty()) {
        	    System.out.println("No File Found");
        	}
            ByteArrayOutputStream outputStream = csvService.evaluateCsv(file.getInputStream());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=processed.csv");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

   
}