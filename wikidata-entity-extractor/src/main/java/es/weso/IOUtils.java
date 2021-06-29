package es.weso;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IOUtils {

    public static List<String[]> readCSV(String fileLocation){
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileLocation))) {
            data = reader.readAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void createFolderIfNecesary(String fileLocation){
        File theDir = new File(fileLocation);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
    }


    public static void writeFile(String fileLocation, String fileContent){
        try {
            FileWriter myWriter = new FileWriter (fileLocation);
            myWriter.write(fileContent);
            myWriter.close();
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String readFile(String fileLocation){
        String data = "";
        try {
            File myObj = new File(fileLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }
}
