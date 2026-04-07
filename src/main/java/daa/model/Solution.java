package daa.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a candidate solution for the MS-CFLP-CI problem.
 * * This class maintains the state of opened facilities, customer assignments,
 * and auxiliary structures like residual capacities and incompatibility counters
 * to allow for efficient feasibility checks during search processes.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class Solution {
  private final Instance instance;
  public boolean[] open;
  public double[][] x;
  public boolean[][] w;
  public double[] residualCap;
  public List<Integer>[] clientsOf;
  public List<Integer>[] facilitiesOf;
  public int[][] incompCount;
  private double fixedCost;
  private double transportCost;
  private double totalCost;

  /**
   * Initializes a new Solution with empty assignments and full residual capacities.
   * * @param instance The problem instance defining dimensions and constraints.
   */
  @SuppressWarnings("unchecked")
  public Solution(Instance instance) {
    this.instance = instance;
    int m = instance.getNumWarehouses();
    int n = instance.getNumStores();
    this.open = new boolean[m];
    this.x = new double[n][m];
    this.w = new boolean[n][m];
    this.residualCap = new double[m];
    this.incompCount = new int[n][m];
    this.clientsOf = new ArrayList[m];
    for (int j = 0; j < m; j++) {
      this.clientsOf[j] = new ArrayList<>();
      this.residualCap[j] = instance.getCapacity(j);
    }
    this.facilitiesOf = new ArrayList[n];
    for (int i = 0; i < n; i++) {
      this.facilitiesOf[i] = new ArrayList<>();
    }
  }

  /**
   * Determines if a store is compatible with a warehouse based on current assignments.
   * * @param storeId The ID of the store to check.
   * @param warehouseId The ID of the target warehouse.
   * @return {@code true} if no incompatible stores are currently assigned to the warehouse; {@code false} otherwise.
   */
  public boolean isCompatible(int storeId, int warehouseId) {
    return incompCount[storeId][warehouseId] == 0;
  }

  /**
   * Calculates the fixed costs of open facilities and the variable transportation
   * costs based on current assignments.
   */
  public void calculateCosts() {
    this.fixedCost = 0;
    this.transportCost = 0;
    for (int j = 0; j < instance.getNumWarehouses(); j++) {
      if (open[j]) {
        this.fixedCost += instance.getFixedCost(j);
      }
    }
    for (int i = 0; i < instance.getNumStores(); i++) {
      for (int j = 0; j < instance.getNumWarehouses(); j++) {
        if (x[i][j] > 0) {
          this.transportCost += instance.getTransportCost(i, j) * instance.getDemand(i) * x[i][j];
        }
      }
    }
    this.totalCost = this.fixedCost + this.transportCost;
  }

  /**
   * @return The total cost (Fixed + Transport).
   */
  public double getTotalCost() {
    return totalCost;
  }

  /**
   * @return The sum of fixed costs for all open facilities.
   */
  public double getFixedCost() {
    return fixedCost;
  }

  /**
   * @return The sum of variable transportation costs.
   */
  public double getTransportCost() {
    return transportCost;
  }
}