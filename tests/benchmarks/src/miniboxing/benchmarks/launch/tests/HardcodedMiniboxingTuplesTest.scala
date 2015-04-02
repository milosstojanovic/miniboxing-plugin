package miniboxing.benchmarks.launch.tests

import org.scalameter.api._
import miniboxing.benchmarks.specialized._
import scala.util.Random
import miniboxing.benchmarks.hardcoded.tuples.QuickSortTuplesMb
import miniboxing.runtime.MiniboxedTuple
import miniboxing.runtime.MiniboxConversions
import miniboxing.runtime.MiniboxConstants
import miniboxing.runtime.MiniboxConversionsDouble

trait HardcodedMiniboxingTuplesTest extends BaseTest {

  def testHardcodedMiniboxingTuples() = {

    var arr: Array[(Int, Double)] = new Array[(Int, Double)](2000000)
    val random = new Random()
    for( ind <- 0 to 1999999){
       arr(ind) = MiniboxedTuple.newTuple2_long_double[Int, Double](
           MiniboxConstants.INT, 
           MiniboxConstants.DOUBLE, 
           MiniboxConversions.int2minibox(random.nextInt(2000000)), 
           MiniboxConversionsDouble.double2minibox(0.0)
       )
    }
    test("mb_hardcoded", "tuples", _ => (), QuickSortTuplesMb.quicksortByKeyMb[Int](arr), () => {})
  }
}
