package daa.utils;

import daa.model.Instance;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

  /**
   * Retorna una lista de todos los archivos .dzn en el directorio especificado.
   */
  public static List<File> getDznFiles(String directoryPath) {
    File folder = new File(directoryPath);
    File[] listOfFiles = folder.listFiles();
    List<File> dznFiles = new ArrayList<>();

    if (listOfFiles != null) {
      for (File file : listOfFiles) {
        if (file.isFile() && file.getName().endsWith(".dzn")) {
          dznFiles.add(file);
        }
      }
    }
    return dznFiles;
  }
}