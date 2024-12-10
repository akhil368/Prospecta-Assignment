package com.prospecta.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

@Service
public class CsvService {
	
	public ByteArrayOutputStream evaluateCsv(InputStream inputStream) throws IOException {
        Map<String, Double> cellValues = new HashMap<>();
        String[][] data;

       
        try (Reader reader = new InputStreamReader(inputStream)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            int rows = 0;
            int cols = 0;

            for (CSVRecord record : records) {
                rows++;
                cols = Math.max(cols, record.size());
            }

           
            data = new String[rows][cols];
        }

       
        try (Reader reader = new InputStreamReader(inputStream)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
            int rowIndex = 0;

            for (CSVRecord record : records) {
                for (int colIndex = 0; colIndex < record.size(); colIndex++) {
                    data[rowIndex][colIndex] = record.get(colIndex).trim();
                }
                rowIndex++;
            }
        }

        
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                String cellId = getCellId(row, col);
                if (!cellValues.containsKey(cellId)) { 
                    double value = evaluateCell(data, row, col, cellValues);
                    cellValues.put(cellId, value);
                }
            }
        }

      
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (int row = 0; row < data.length; row++) {
                String[] evaluatedRow = new String[data[row].length];
                for (int col = 0; col < data[row].length; col++) {
                    String cellId = getCellId(row, col);
                    evaluatedRow[col] = String.valueOf(cellValues.get(cellId));
                }
                csvPrinter.printRecord((Object[]) evaluatedRow);
            }
        }
        return outputStream;
    }

    
    private double evaluateCell(String[][] data, int row, int col, Map<String, Double> cellValues) {
        String cellContent = data[row][col];

      
        String cellId = getCellId(row, col);
        if (cellValues.containsKey(cellId)) {
            return cellValues.get(cellId);
        }

       
        if (cellContent.startsWith("=")) {
            String formula = cellContent.substring(1);
            String[] tokens = formula.split("[+\\-*/]");
            double result = 0;
            char operator = ' ';

            for (String token : tokens) {
                token = token.trim();
                if (token.matches("[A-Z]+[0-9]+")) { 
                    int[] refCoords = getCellCoordinates(token);
                    double value = evaluateCell(data, refCoords[0], refCoords[1], cellValues);
                    result = applyOperator(result, value, operator);
                } else { 
                    double value = Double.parseDouble(token);
                    result = applyOperator(result, value, operator);
                }
                operator = getOperator(formula);
            }

            cellValues.put(cellId, result); 
            return result;
        } else {
            
            double value = Double.parseDouble(cellContent);
            cellValues.put(cellId, value);
            return value;
        }
    }

    
    private String getCellId(int row, int col) {
        return "" + (char) ('A' + col) + (row + 1);
    }

    
    private int[] getCellCoordinates(String cellRef) {
        int col = cellRef.charAt(0) - 'A';
        int row = Integer.parseInt(cellRef.substring(1)) - 1;
        return new int[]{row, col};
    }

   
    private double applyOperator(double result, double value, char operator) {
        switch (operator) {
            case '+': return result + value;
            case '-': return result - value;
            case '*': return result * value;
            case '/': return result / value;
            default: return value;
        }
    }


    private char getOperator(String formula) {
        if (formula.contains("+")) return '+';
        if (formula.contains("-")) return '-';
        if (formula.contains("*")) return '*';
        if (formula.contains("/")) return '/';
        return ' ';
    }

}
