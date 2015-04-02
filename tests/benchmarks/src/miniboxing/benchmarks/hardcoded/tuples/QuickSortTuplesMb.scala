package miniboxing.benchmarks.hardcoded.tuples

import scala.math.Ordered
import miniboxing.runtime.MiniboxConstants
import miniboxing.runtime.MiniboxConversions
import miniboxing.runtime.MiniboxedTuple

object QuickSortTuplesMb {
  def quicksortByKeyMb[@miniboxed T <% Ordered[Int]](arr: Array[(T, Double)]): Array[(T, Double)] = 
    arr.sortBy(f => MiniboxedTuple.tuple2_accessor_1_long(MiniboxConstants.INT, f))(new Ordering[Long]() { 
       import scala.math.Ordered.orderingToOrdered
       override def compare(x: Long, y: Long) = {
         MiniboxConversions.minibox2int(x) - 
         MiniboxConversions.minibox2int(y)
       }
    })
}