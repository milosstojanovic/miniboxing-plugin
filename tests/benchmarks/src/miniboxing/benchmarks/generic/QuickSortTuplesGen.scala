package miniboxing.benchmarks.ideal

object QuickSortTuplesGen {
  def quicksortByKeyGen[T <% Ordered[T]](arr: Array[(T, Double)]): Array[(T, Double)] =
    arr.sortBy(f => f._1)
}
