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

public class Main {
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

    // Listas para almacenar resultados y calcular el Promedio final
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
          for (File f : files) executeGreedy(f, allSolutions, allTimes);
          LogFormatter.printSummary(allSolutions, allTimes, false);
        } else {
          LogFormatter.printGraspHeader();
          for (File f : files) executeGrasp(f, allSolutions, allTimes);
          LogFormatter.printSummary(allSolutions, allTimes, true);
        }
      } else {
        System.err.println("No se encontraron archivos .dzn en la carpeta 'inputs/'");
      }
    }
    scanner.close();
  }

  /**
   * Ejecuta el Algoritmo Voraz siguiendo el Algorithm 1 del documento[cite: 149].
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
   * Ejecuta el Algoritmo GRASP con el ajuste de parámetros requerido[cite: 237].
   */
  private static void executeGrasp(File file, List<Solution> solutions, List<Double> times) {
    try {
      Instance instance = Parser.parse(file.getAbsolutePath());

      // Configuración requerida por la Tabla 13: LRC {2, 3} y 3 ejecuciones por instancia
      int[] lrcSizes = {2, 3};
      int executionsPerConfig = 3;

      for (int lrc : lrcSizes) {
        for (int e = 1; e <= executionsPerConfig; e++) {
          // Se asume que el constructor de GraspStrategy acepta (LRC, Iteraciones, BúsquedasLocales)
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