package miniboxing.benchmarks.ideal

object QuickSortTuplesSpec {
  def quicksortByKeySpec[@specialized T <% Ordered[T]](arr: Array[(T, Double)]): Array[(T, Double)] = 
    arr.sortBy(f => f._1)
}
