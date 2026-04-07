package daa.utils;

import daa.model.Solution;
import java.util.List;

public class LogFormatter {

  // --- GREEDY TABLE HEADERS ---
  public static void printGreedyHeader() {
    System.out.println("\nAlgoritmo Voraz (MS-CFLP-CI)");
    System.out.printf("%-12s | %-7s | %-12s | %-12s | %-12s | %-7s | %-12s%n",
      "Instancia", "|Jopen|", "Coste Fijo", "Coste Asig.", "Coste Total", "Incomp.", "CPU_Time (s)");
    System.out.println("--------------------------------------------------------------------------------------------------");
  }

  // --- GRASP TABLE HEADERS ---
  public static void printGraspHeader() {
    System.out.println("\nAlgoritmo GRASP (Ajuste y Resultados)");
    System.out.printf("%-12s | %-5s | %-5s | %-7s | %-12s | %-12s | %-12s | %-7s | %-10s%n",
      "Instancia", "|LRC|", "Ejec.", "|Jopen|", "C. Fijo", "C. Asig.", "C. Total", "Incomp.", "CPU_Time");
    System.out.println("-----------------------------------------------------------------------------------------------------------------------");
  }

  // Row for Greedy [cite: 234]
  public static void printGreedyRow(String filename, Solution sol, double time) {
    System.out.printf("%-12s | %-7d | %-12.2f | %-12.2f | %-12.2f | %-7d | %-12.4f%n",
      filename, countOpen(sol), sol.getFixedCost(), sol.getTransportCost(), sol.getTotalCost(), 0, time);
  }

  // Row for GRASP
  public static void printGraspRow(String filename, int lrc, int ejec, Solution sol, double time) {
    System.out.printf("%-12s | %-5d | %-5d | %-7d | %-12.2f | %-12.2f | %-12.2f | %-7d | %-10.4f%n",
      filename, lrc, ejec, countOpen(sol), sol.getFixedCost(), sol.getTransportCost(), sol.getTotalCost(), 0, time);
  }

  // --- PROMEDIO (AVERAGE) ROW  ---
  public static void printSummary(List<Solution> solutions, List<Double> times, boolean isGrasp) {
    double avgFixed = 0, avgTrans = 0, avgTotal = 0, avgTime = 0;
    int avgOpen = 0;

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
        label, (double)avgOpen/n, avgFixed/n, avgTrans/n, avgTotal/n, 0, avgTime/n);
    } else {
      System.out.println("--------------------------------------------------------------------------------------------------");
      System.out.printf("%-12s | %-7.1f | %-12.2f | %-12.2f | %-12.2f | %-7d | %-12.4f%n",
        label, (double)avgOpen/n, avgFixed/n, avgTrans/n, avgTotal/n, 0, avgTime/n);
    }
  }

  private static int countOpen(Solution sol) {
    int count = 0;
    for (boolean b : sol.open) if (b) count++;
    return count;
  }
}