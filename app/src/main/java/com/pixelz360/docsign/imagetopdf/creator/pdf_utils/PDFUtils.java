package com.pixelz360.docsign.imagetopdf.creator.pdf_utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;
public class PDFUtils {

    private static final String PDF_DIRECTORY = "DigitalSignature";

    // Get or create the directory for PDFs
    public static File getPdfDirectory(Context context) {
        File root = new File(context.getExternalFilesDir(null), PDF_DIRECTORY);
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    // Create a new PDF file with a unique name
    public static File createPdfFile(Context context) throws IOException {
        long timestamp = System.currentTimeMillis();
        String fileName = "PDF_" + timestamp + ".pdf";
        File pdfFile = new File(getPdfDirectory(context), fileName);
        if (pdfFile.exists()) {
            pdfFile.delete(); // Delete if it exists
        }
        pdfFile.createNewFile();
        return pdfFile;
    }

    // Get a FileOutputStream for a given file
    public static FileOutputStream getFileOutputStream(File file) throws IOException {
        return new FileOutputStream(file);
    }


    public static String displayMonthlyPrice(String annualPriceStr) {
        if (annualPriceStr == null || annualPriceStr.isEmpty()) {
            Log.e("Error", "Annual price string is empty");
            return "";
        }

        // Extract currency symbol and numeric value
        String currencySymbol = extractCurrencySymbol(annualPriceStr);
        String numericValue = extractNumericValue(annualPriceStr);

        try {
            double annualPrice = Double.parseDouble(numericValue.replace(",", "")); // Remove commas
            double monthlyPrice = annualPrice / 12;

            // Format monthly price to 2 decimal places
            String formattedMonthlyPrice = String.format("%.2f", monthlyPrice); // Ensures precision like 583.33

            // Get actual country from the device
            String country = Locale.getDefault().getCountry(); // Gets PK, US, IN, etc.

            // Detect correct locale
            Locale userLocale = new Locale("en", country);
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(userLocale);
            Currency currency = Currency.getInstance(userLocale);

            // If country is Pakistan, force PKR
            if (country.equalsIgnoreCase("PK")) {
                currency = Currency.getInstance("PKR");  // Set Pakistani Rupee
            }

            currencyFormatter.setCurrency(currency);
            String formattedPriceWithCurrency = currencyFormatter.format(Double.parseDouble(formattedMonthlyPrice));

            // Logging for debugging
            Log.d("Monthly Price", "Original Price: " + annualPriceStr);
            Log.d("Monthly Price", "Extracted Currency Symbol: " + currencySymbol);
            Log.d("Monthly Price", "User Locale: " + userLocale.toString());
            Log.d("Monthly Price", "Country Code: " + country);
            Log.d("Monthly Price", "Currency Code: " + currency.getCurrencyCode());
            Log.d("Monthly Price", "Formatted Monthly Price: " + formattedPriceWithCurrency);
            Log.d("Monthly Price", "Monthly Price (without currency): " + formattedMonthlyPrice);

//            return formattedPriceWithCurrency;
            return currencySymbol+formattedMonthlyPrice;
        } catch (NumberFormatException e) {
            Log.e("Error", "Invalid price format: " + annualPriceStr, e);
        }
        return numericValue;
    }

    // Extracts currency symbol from the price string
    private static String extractCurrencySymbol(String priceStr) {
        Pattern pattern = Pattern.compile("[^0-9.,\\s]+"); // Matches non-numeric characters (currency symbols)
        Matcher matcher = pattern.matcher(priceStr);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    // Extracts only the numeric part from the price string
    private static String extractNumericValue(String priceStr) {
        return priceStr.replaceAll("[^0-9.,]", ""); // Keeps only numbers, dots, and commas
    }
}

