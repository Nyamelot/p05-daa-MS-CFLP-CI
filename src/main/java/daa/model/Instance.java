package daa.model;

/**
 * Represents the MS-CFLP-CI (Multi-Source Capacitated Facility Location Problem
 * with Customer Incompatibilities) problem data.
 * * This class stores the input parameters for the problem, including facility
 * capacities, fixed costs, customer demands, and transportation costs.
 * It also maintains the incompatibility matrix between customers.
 * @author Jose Angel Portillo Garcia
 * @version 2025-2026
 */
public class Instance {
  private final int numWarehouses;
  private final int numStores;
  private final int numIncompatibilities;
  private final int[] capacities;
  private final double[] fixedCosts;
  private final int[] demands;
  private final double[][] supplyCosts;
  private final boolean[][] isIncompatible;

  /**
   * Constructs a new problem instance.
   * * @param numWarehouses Number of potential facility locations.
   * @param numStores Number of customers/stores to be served.
   * @param numIncompatibilities Count of incompatible customer pairs.
   * @param capacities Array containing the maximum capacity for each warehouse.
   * @param fixedCosts Array containing the cost to open each warehouse.
   * @param demands Array containing the demand requirements for each store.
   * @param supplyCosts Matrix of unit transportation costs between stores and warehouses.
   * @param isIncompatible Symmetric matrix representing customer incompatibilities.
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

  /**
   * @return The total number of potential warehouses (m).
   */
  public int getNumWarehouses() {
    return numWarehouses;
  }

  /**
   * @return The total number of stores/customers (n).
   */
  public int getNumStores() {
    return numStores;
  }

  /**
   * @return The number of defined incompatible pairs.
   */
  public int getNumIncompatibilities() {
    return numIncompatibilities;
  }

  /**
   * Gets the maximum capacity of a specific warehouse.
   * * @param j The index of the warehouse.
   * @return The capacity value.
   */
  public int getCapacity(int j) {
    return capacities[j];
  }

  /**
   * Gets the fixed cost associated with opening a specific warehouse.
   * * @param j The index of the warehouse.
   * @return The fixed cost value.
   */
  public double getFixedCost(int j) {
    return fixedCosts[j];
  }

  /**
   * Gets the demand required by a specific store.
   * * @param i The index of the store.
   * @return The demand value.
   */
  public int getDemand(int i) {
    return demands[i];
  }

  /**
   * Gets the unit transportation cost between a store and a warehouse.
   * * @param i The index of the store.
   * @param j The index of the warehouse.
   * @return The transport cost c_ij.
   */
  public double getTransportCost(int i, int j) {
    return supplyCosts[i][j];
  }

  /**
   * Checks if two specific customers are incompatible and cannot be assigned
   * to the same facility.
   * * @param i1 The index of the first store.
   * @param i2 The index of the second store.
   * @return {@code true} if they are incompatible; {@code false} otherwise.
   */
  public boolean areIncompatible(int i1, int i2) {
    return isIncompatible[i1][i2];
  }
}