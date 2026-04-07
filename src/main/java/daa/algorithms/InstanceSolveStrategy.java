package daa.algorithms;

import daa.model.Instance;
import daa.model.Solution;

public interface InstanceSolveStrategy {
  Solution solve(Instance instance);
}
