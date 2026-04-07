package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

public class SwapStrategy implements LocalSearchStrategy {
  @Override
  public boolean improve(Solution sol, Instance instance) {
    for (int i1 = 0; i1 < instance.getNumStores(); i1++) {
      for (int i2 = i1 + 1; i2 < instance.getNumStores(); i2++) {
        int j1 = sol.facilitiesOf[i1].get(0);
        int j2 = sol.facilitiesOf[i2].get(0);

        if (j1 == j2) continue;

        // 1. Feasibility Check (Check if i1 fits in j2 AND i2 fits in j1)
        if (checkFeasibility(sol, instance, i1, j2, i2, j1)) {
          double currentCost = instance.getTransportCost(i1, j1) + instance.getTransportCost(i2, j2);
          double swapCost = instance.getTransportCost(i1, j2) + instance.getTransportCost(i2, j1);

          if (swapCost < currentCost) {
            applySwap(sol, instance, i1, j1, i2, j2);
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean checkFeasibility(Solution sol, Instance instance, int i1, int targetJ1, int i2, int targetJ2) {
    // Capacity: residual + demand being removed must be >= demand being added
    boolean cap1 = (sol.residualCap[targetJ1] + instance.getDemand(i2)) >= instance.getDemand(i1);
    boolean cap2 = (sol.residualCap[targetJ2] + instance.getDemand(i1)) >= instance.getDemand(i2);

    // Incompatibility: must exclude the client being swapped out
    // For simplicity in this example, we assume they are compatible if incompCount is 0
    // or only contains the client we are removing.
    return cap1 && cap2 && sol.isCompatible(i1, targetJ1) && sol.isCompatible(i2, targetJ2);
  }

  private void applySwap(Solution sol, Instance instance, int i1, int j1, int i2, int j2) {
    // Reuse shift logic twice or implement specific swap update
  }
}
