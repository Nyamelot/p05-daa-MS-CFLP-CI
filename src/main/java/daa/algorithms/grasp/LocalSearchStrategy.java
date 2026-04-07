package daa.algorithms.grasp;

import daa.model.Instance;
import daa.model.Solution;

public interface LocalSearchStrategy {
  boolean improve(Solution solution, Instance instance);
}