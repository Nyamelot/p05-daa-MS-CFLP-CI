package daa.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import daa.model.Instance;

public class Parser {

  public static Instance parse(String filePath) throws FileNotFoundException {
    File file = new File(filePath);
    Scanner sc = new Scanner(file);

    int warehouses = 0, stores = 0, nIncomp = 0;
    int[] capacities = null;
    double[] fixedCosts = null;
    int[] demands = null;
    double[][] supplyCosts = null;
    boolean[][] incompatibilityMatrix = null;

    // Split by semicolon to isolate each variable block
    sc.useDelimiter(";");

    while (sc.hasNext()) {
      String block = sc.next().trim();
      if (block.isEmpty()) continue;

      // Handle variables using split with limit to avoid issues with content inside arrays
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

    // Safety check to ensure no nulls are passed to the Instance constructor
    if (capacities == null || fixedCosts == null || demands == null ||
      supplyCosts == null || incompatibilityMatrix == null) {
      throw new IllegalStateException("Error: Some required data fields were not found in the file: " + filePath);
    }

    return new Instance(warehouses, stores, nIncomp, capacities, fixedCosts, demands, supplyCosts, incompatibilityMatrix);
  }

  private static int[] parse1DInt(String content) {
    // Remove brackets and all whitespace (newlines/spaces)
    String clean = content.replaceAll("[\\[\\]\\s+]", "");
    String[] parts = clean.split(",");
    int[] arr = new int[parts.length];
    for (int i = 0; i < parts.length; i++) {
      arr[i] = Integer.parseInt(parts[i]);
    }
    return arr;
  }

  private static double[] parse1DDouble(String content) {
    String clean = content.replaceAll("[\\[\\]\\s+]", "");
    String[] parts = clean.split(",");
    double[] arr = new double[parts.length];
    for (int i = 0; i < parts.length; i++) {
      arr[i] = Double.parseDouble(parts[i]);
    }
    return arr;
  }

  private static double[][] parseMatrix(String content, int rows, int cols) {
    double[][] matrix = new double[rows][cols];
    // Remove the [| and |] and then split by the internal pipe |
    String clean = content.replace("[|", "").replace("|]", "").trim();
    String[] lineParts = clean.split("\\|");

    for (int i = 0; i < rows; i++) {
      // Clean each row of whitespace and split by comma
      String[] values = lineParts[i].replaceAll("\\s+", "").split(",");
      for (int j = 0; j < cols; j++) {
        matrix[i][j] = Double.parseDouble(values[j]);
      }
    }
    return matrix;
  }

  private static boolean[][] parsePairs(String content, int n) {
    boolean[][] matrix = new boolean[n][n];
    // Handle the case where IncompatiblePairs might be empty [| |]
    if (!content.contains(",") || content.length() < 5) return matrix;

    String clean = content.replace("[|", "").replace("|]", "").trim();
    String[] pairs = clean.split("\\|");

    for (String pair : pairs) {
      String[] parts = pair.replaceAll("\\s+", "").split(",");
      if (parts.length == 2) {
        // Convert 1-based index from .dzn to 0-based Java index
        int u = Integer.parseInt(parts[0]) - 1;
        int v = Integer.parseInt(parts[1]) - 1;
        matrix[u][v] = true;
        matrix[v][u] = true;
      }
    }
    return matrix;
  }
}