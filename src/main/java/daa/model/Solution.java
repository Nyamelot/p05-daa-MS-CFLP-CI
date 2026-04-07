package daa.model;

import java.util.ArrayList;
import java.util.List;

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

  public boolean isCompatible(int storeId, int warehouseId) {
    return incompCount[storeId][warehouseId] == 0;
  }

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

  // Getters
  public double getTotalCost() { return totalCost; }
  public double getFixedCost() { return fixedCost; }
  public double getTransportCost() { return transportCost; }
}