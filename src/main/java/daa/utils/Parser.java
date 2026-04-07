package daa.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import daa.model.Instance;

/**
 * Utility class responsible for reading and parsing .dzn files into {@link Instance} objects.
 * * This parser handles standard Minizinc data format variables such as Warehouses, Stores,
 * Capacity, FixedCost, Goods, SupplyCost, and IncompatiblePairs.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class Parser {

  /**
   * Parses a .dzn file to create a problem instance.
   * * @param filePath The path to the .dzn file to be parsed.
   * @return A fully populated {@link Instance} object.
   * @throws FileNotFoundException If the file cannot be located.
   * @throws IllegalStateException If required data fields are missing from the file.
   */
  public static Instance parse(String filePath) throws FileNotFoundException {
    File file = new File(filePath);
    Scanner sc = new Scanner(file);

    int warehouses = 0, stores = 0, nIncomp = 0;
    int[] capacities = null;
    double[] fixedCosts = null;
    int[] demands = null;
    double[][] supplyCosts = null;
    boolean[][] incompatibilityMatrix = null;

    sc.useDelimiter(";");

    while (sc.hasNext()) {
      String block = sc.next().trim();
      if (block.isEmpty()) continue;

      String[] parts = block.split("=", 2);
      if (parts.length < 2) continue;

      String varName = parts[0].trim();
      String valuePart = parts[1].trim();

      switch (varName) {
        case "Warehouses":
          warehouses = Integer.parseInt(valuePart);
          break;
        case "Stores":
          stores = Integer.parseInt(valuePart);
          break;
        case "Incompatibilities":
          nIncomp = Integer.parseInt(valuePart);
          break;
        case "Capacity":
          capacities = parse1DInt(valuePart);
          break;
        case "FixedCost":
          fixedCosts = parse1DDouble(valuePart);
          break;
        case "Goods":
          demands = parse1DInt(valuePart);
          break;
        case "SupplyCost":
          supplyCosts = parseMatrix(valuePart, stores, warehouses);
          break;
        case "IncompatiblePairs":
          incompatibilityMatrix = parsePairs(valuePart, stores);
          break;
      }
    }
    sc.close();

    if (capacities == null || fixedCosts == null || demands == null ||
      supplyCosts == null || incompatibilityMatrix == null) {
      throw new IllegalStateException("Error: Some required data fields were not found in the file: " + filePath);
    }

    return new Instance(warehouses, stores, nIncomp, capacities, fixedCosts, demands, supplyCosts, incompatibilityMatrix);
  }

  /**
   * Parses a 1D integer array from the Minizinc format [x, y, z].
   * * @param content The raw string content of the array.
   * @return An integer array.
   */
  private static int[] parse1DInt(String content) {
    String clean = content.replaceAll("[\\[\\]\\s+]", "");
    String[] parts = clean.split(",");
    int[] arr = new int[parts.length];
    for (int i = 0; i < parts.length; i++) {
      arr[i] = Integer.parseInt(parts[i]);
    }
    return arr;
  }

  /**
   * Parses a 1D double array from the Minizinc format [x, y, z].
   * * @param content The raw string content of the array.
   * @return A double array.
   */
  private static double[] parse1DDouble(String content) {
    String clean = content.replaceAll("[\\[\\]\\s+]", "");
    String[] parts = clean.split(",");
    double[] arr = new double[parts.length];
    for (int i = 0; i < parts.length; i++) {
      arr[i] = Double.parseDouble(parts[i]);
    }
    return arr;
  }

  /**
   * Parses a 2D matrix from the Minizinc format [| x, y | a, b |].
   * * @param content The raw string content of the matrix.
   * @param rows Expected number of rows.
   * @param cols Expected number of columns.
   * @return A 2D double matrix.
   */
  private static double[][] parseMatrix(String content, int rows, int cols) {
    double[][] matrix = new double[rows][cols];
    String clean = content.replace("[|", "").replace("|]", "").trim();
    String[] lineParts = clean.split("\\|");

    for (int i = 0; i < rows; i++) {
      String[] values = lineParts[i].replaceAll("\\s+", "").split(",");
      for (int j = 0; j < cols; j++) {
        matrix[i][j] = Double.parseDouble(values[j]);
      }
    }
    return matrix;
  }

  /**
   * Parses the incompatibility pairs and converts them into a symmetric boolean matrix.
   * Note: Converts 1-based .dzn indices to 0-based Java indices.
   * * @param content The raw string content of the pairs matrix.
   * @param n The total number of stores (dimension of the matrix).
   * @return A symmetric boolean matrix where true indicates incompatibility.
   */
  private static boolean[][] parsePairs(String content, int n) {
    boolean[][] matrix = new boolean[n][n];
    if (!content.contains(",") || content.length() < 5) return matrix;

    String clean = content.replace("[|", "").replace("|]", "").trim();
    String[] pairs = clean.split("\\|");

    for (String pair : pairs) {
      String[] parts = pair.replaceAll("\\s+", "").split(",");
      if (parts.length == 2) {
        int u = Integer.parseInt(parts[0]) - 1;
        int v = Integer.parseInt(parts[1]) - 1;
        matrix[u][v] = true;
        matrix[v][u] = true;
      }
    }
    return matrix;
  }
}