package daa;

import daa.algorithms.GreedyStrategy;
import daa.algorithms.grasp.GraspStrategy;
import daa.algorithms.InstanceSolveStrategy;
import daa.model.Instance;
import daa.model.Solution;
import daa.utils.Parser;
import daa.utils.LogFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the MS-CFLP-CI Solver application.
 * * This class provides a command-line interface to select between Greedy and GRASP
 * algorithms and allows for processing a single instance or all instances in a directory.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class Main {
  /**
   * Main method that handles user input and orchestrates the execution of algorithms.
   * * @param args Command line arguments (not used).
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("=== MS-CFLP-CI Solver (Curso 2025-2026) ===");
    System.out.println("1. Algoritmo Voraz (Greedy)");
    System.out.println("2. Algoritmo GRASP");
    System.out.print("Seleccione Algoritmo: ");
    int algoChoice = scanner.nextInt();

    System.out.println("\n--- Modo de Ejecución ---");
    System.out.println("1. Una sola instancia");
    System.out.println("2. Todas las instancias (carpeta inputs/)");
    System.out.print("Seleccione opción: ");
    int modeChoice = scanner.nextInt();

    List<Solution> allSolutions = new ArrayList<>();
    List<Double> allTimes = new ArrayList<>();

    if (modeChoice == 1) {
      System.out.print("Nombre del archivo en inputs/ (ej: wlp01.dzn): ");
      String filename = scanner.next();
      File file = new File("inputs/" + filename);

      if (algoChoice == 1) {
        LogFormatter.printGreedyHeader();
        executeGreedy(file, allSolutions, allTimes);
        LogFormatter.printSummary(allSolutions, allTimes, false);
      } else {
        LogFormatter.printGraspHeader();
        executeGrasp(file, allSolutions, allTimes);
        LogFormatter.printSummary(allSolutions, allTimes, true);
      }
    } else {
      File folder = new File("inputs/");
      File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".dzn"));

      if (files != null && files.length > 0) {
        if (algoChoice == 1) {
          LogFormatter.printGreedyHeader();
          for (File f : files) {
            executeGreedy(f, allSolutions, allTimes);
          }
          LogFormatter.printSummary(allSolutions, allTimes, false);
        } else {
          LogFormatter.printGraspHeader();
          for (File f : files) {
            executeGrasp(f, allSolutions, allTimes);
          }
          LogFormatter.printSummary(allSolutions, allTimes, true);
        }
      } else {
        System.err.println("No se encontraron archivos .dzn en la carpeta 'inputs/'");
      }
    }
    scanner.close();
  }

  /**
   * Logic to execute the Greedy strategy on a specific file and log the results.
   * * @param file The .dzn instance file.
   * @param solutions List to store the resulting solution.
   * @param times List to store the execution time.
   */
  private static void executeGreedy(File file, List<Solution> solutions, List<Double> times) {
    try {
      Instance instance = Parser.parse(file.getAbsolutePath());
      InstanceSolveStrategy strategy = new GreedyStrategy();

      long start = System.nanoTime();
      Solution sol = strategy.solve(instance);
      double time = (System.nanoTime() - start) / 1e9;

      LogFormatter.printGreedyRow(file.getName(), sol, time);

      solutions.add(sol);
      times.add(time);
    } catch (Exception e) {
      System.err.println("Error en " + file.getName() + ": " + e.getMessage());
    }
  }

  /**
   * Logic to execute the GRASP strategy on a specific file with required parameter tuning.
   * * @param file The .dzn instance file.
   * @param solutions List to store resulting solutions across configurations.
   * @param times List to store execution times.
   */
  private static void executeGrasp(File file, List<Solution> solutions, List<Double> times) {
    try {
      Instance instance = Parser.parse(file.getAbsolutePath());

      int[] lrcSizes = {2, 3};
      int executionsPerConfig = 3;

      for (int lrc : lrcSizes) {
        for (int e = 1; e <= executionsPerConfig; e++) {
          InstanceSolveStrategy strategy = new GraspStrategy(lrc, 100, new ArrayList<>());

          long start = System.nanoTime();
          Solution sol = strategy.solve(instance);
          double time = (System.nanoTime() - start) / 1e9;

          LogFormatter.printGraspRow(file.getName(), lrc, e, sol, time);

          solutions.add(sol);
          times.add(time);
        }
      }
    } catch (Exception e) {
      System.err.println("Error en " + file.getName() + ": " + e.getMessage());
    }
  }
}