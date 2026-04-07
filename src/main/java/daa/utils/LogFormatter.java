package daa.utils;

import daa.model.Solution;
import java.util.List;

/**
 * Utility class for formatting and printing algorithm results to the console.
 * * This class generates the tables required by the project documentation,
 * including headers and rows for both Greedy and GRASP strategies, as well
 * as the final average (Promedio) row.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class LogFormatter {

  /**
   * Prints the header for the Greedy algorithm results table.
   */
  public static void printGreedyHeader() {
    System.out.println("\nAlgoritmo Voraz (MS-CFLP-CI)");
    System.out.printf("%-12s | %-7s | %-12s | %-12s | %-12s | %-7s | %-12s%n",
      "Instancia", "|Jopen|", "Coste Fijo", "Coste Asig.", "Coste Total", "Incomp.", "CPU_Time (s)");
    System.out.println("--------------------------------------------------------------------------------------------------");
  }

  /**
   * Prints the header for the GRASP algorithm results table, including LRC and Execution columns.
   */
  public static void printGraspHeader() {
    System.out.println("\nAlgoritmo GRASP (Ajuste y Resultados)");
    System.out.printf("%-12s | %-5s | %-5s | %-7s | %-12s | %-12s | %-12s | %-7s | %-10s%n",
      "Instancia", "|LRC|", "Ejec.", "|Jopen|", "C. Fijo", "C. Asig.", "C. Total", "Incomp.", "CPU_Time");
    System.out.println("-----------------------------------------------------------------------------------------------------------------------");
  }

  /**
   * Prints a single result row for the Greedy algorithm.
   * * @param filename The name of the processed instance file.
   * @param sol The solution object obtained.
   * @param time The execution time in seconds.
   */
  public static void printGreedyRow(String filename, Solution sol, double time) {
    System.out.printf("%-12s | %-7d | %-12.2f | %-12.2f | %-12.2f | %-7d | %-12.4f%n",
      filename, countOpen(sol), sol.getFixedCost(), sol.getTransportCost(), sol.getTotalCost(), 0, time);
  }

  /**
   * Prints a single result row for the GRASP algorithm.
   * * @param filename The name of the processed instance file.
   * @param lrc The size of the Restricted Candidate List used.
   * @param ejec The current execution number for the configuration.
   * @param sol The solution object obtained.
   * @param time The execution time in seconds.
   */
  public static void printGraspRow(String filename, int lrc, int ejec, Solution sol, double time) {
    System.out.printf("%-12s | %-5d | %-5d | %-7d | %-12.2f | %-12.2f | %-12.2f | %-7d | %-10.4f%n",
      filename, lrc, ejec, countOpen(sol), sol.getFixedCost(), sol.getTransportCost(), sol.getTotalCost(), 0, time);
  }

  /**
   * Calculates and prints the average (Promedio) row for a set of results.
   * * @param solutions List of solutions processed.
   * @param times List of execution times corresponding to the solutions.
   * @param isGrasp {@code true} if formatting for a GRASP table; {@code false} for Greedy.
   */
  public static void printSummary(List<Solution> solutions, List<Double> times, boolean isGrasp) {
    double avgFixed = 0, avgTrans = 0, avgTotal = 0, avgTime = 0;
    double avgOpen = 0;

    for (int i = 0; i < solutions.size(); i++) {
      avgFixed += solutions.get(i).getFixedCost();
      avgTrans += solutions.get(i).getTransportCost();
      avgTotal += solutions.get(i).getTotalCost();
      avgOpen += countOpen(solutions.get(i));
      avgTime += times.get(i);
    }

    int n = solutions.size();
    String label = "Promedio";

    if (isGrasp) {
      System.out.println("-----------------------------------------------------------------------------------------------------------------------");
      System.out.printf("%-26s | %-7.1f | %-12.2f | %-12.2f | %-12.2f | %-7d | %-10.4f%n",
        label, avgOpen / n, avgFixed / n, avgTrans / n, avgTotal / n, 0, avgTime / n);
    } else {
      System.out.println("--------------------------------------------------------------------------------------------------");
      System.out.printf("%-12s | %-7.1f | %-12.2f | %-12.2f | %-12.2f | %-7d | %-12.4f%n",
        label, avgOpen / n, avgFixed / n, avgTrans / n, avgTotal / n, 0, avgTime / n);
    }
  }

  /**
   * Helper method to count the number of opened facilities in a solution.
   * * @param sol The solution to analyze.
   * @return The number of facilities where {@code open[j] == true}.
   */
  private static int countOpen(Solution sol) {
    int count = 0;
    for (boolean b : sol.open) {
      if (b) count++;
    }
    return count;
  }
}