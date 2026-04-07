package daa.model;

/**
 * Represents the MS-CFLP-CI problem data.
 * This class is immutable once created to ensure data integrity.
 */
public class Instance {
  private final int numWarehouses; // m
  private final int numStores;     // n
  private final int numIncompatibilities;
  private final int[] capacities;   // s_j
  private final double[] fixedCosts; // f_j
  private final int[] demands; // d_i
  private final double[][] supplyCosts;
  private final boolean[][] isIncompatible;

  /**
   * Constructor used by the Parser to create the problem instance.
   */
  public Instance(int numWarehouses, int numStores, int numIncompatibilities,
                  int[] capacities, double[] fixedCosts, int[] demands,
                  double[][] supplyCosts, boolean[][] isIncompatible) {
    this.numWarehouses = numWarehouses;
    this.numStores = numStores;
    this.numIncompatibilities = numIncompatibilities;
    this.capacities = capacities.clone();
    this.fixedCosts = fixedCosts.clone();
    this.demands = demands.clone();
    this.supplyCosts = new double[numStores][numWarehouses];
    for (int i = 0; i < numStores; i++) {
      this.supplyCosts[i] = supplyCosts[i].clone();
    }
    this.isIncompatible = new boolean[numStores][numStores];
    for (int i = 0; i < numStores; i++) {
      this.isIncompatible[i] = isIncompatible[i].clone();
    }
  }

  // --- Getters for Dimensions ---
  public int getNumWarehouses() { return numWarehouses; }
  public int getNumStores() { return numStores; }
  public int getNumIncompatibilities() { return numIncompatibilities; }

  // --- Getters for Warehouse Data [cite: 103, 105] ---
  public int getCapacity(int j) { return capacities[j]; }
  public double getFixedCost(int j) { return fixedCosts[j]; }

  // --- Getters for Store Data [cite: 106] ---
  public int getDemand(int i) { return demands[i]; }

  /**
   * Returns the unit transport cost c_ij[cite: 107].
   */
  public double getTransportCost(int i, int j) {
    return supplyCosts[i][j];
  }

  /**
   * Checks if two customers are incompatible[cite: 36, 101].
   * Returns true if <i1, i2> exists in the incompatibility set.
   */
  public boolean areIncompatible(int i1, int i2) {
    return isIncompatible[i1][i2];
  }
}