package daa.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file system operations related to the MS-CFLP-CI problem.
 * * This class provides helper methods to manage and filter problem instances
 * stored in the local file system.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class FileUtils {

  /**
   * Retrieves a list of all files with the .dzn extension within a specific directory.
   * * @param directoryPath The relative or absolute path to the directory containing instances.
   * @return A {@link List} of {@link File} objects pointing to the .dzn files found.
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