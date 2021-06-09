package es.weso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException; 

public class SPARQLReader {
 
  public static String readFile(String file) throws IOException {
    StringBuilder resultStringBuilder = new StringBuilder();
    String filePath = new File("").getAbsolutePath();
    BufferedReader br = new BufferedReader(new FileReader(filePath.concat(file)));
    
    String line;
    while ((line = br.readLine()) != null) {
        resultStringBuilder.append(line).append("\n");
    }
    br.close();
    return resultStringBuilder.toString();
}

}