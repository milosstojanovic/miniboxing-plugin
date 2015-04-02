package miniboxing.benchmarks.ideal

object QuickSortTuples {
  def quicksortByKey(arr: Array[(Int, Double)]): Array[(Int, Double)] = 
    arr.sortBy(f => f._1)
}
