package miniboxing.benchmarks.launch.tests

import org.scalameter.api._
import miniboxing.benchmarks.generic._
import scala.util.Random
import miniboxing.benchmarks.ideal.QuickSortTuplesGen

trait GenericBenchTest extends BaseTest {

  private[this] object TestList {
    def list_insert(): List[Int] = {
      var l: List[Int] = null
      var i = 0
      while (i < testSize) {
        l = new List[Int](i, l)
        i += 1
      }
      l
    }

    def list_hashCode(list: List[Int]): Int = {
      list.hashCode
    }

    def list_find(l: List[Int]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ l.contains(i)
        i += 10000
      }
      b
    }

    def list_insert_DOUBLE(): List[Double] = {
      var l: List[Double] = null
      var i = 0
      while (i < testSize) {
        l = new List[Double](i, l)
        i += 1
      }
      l
    }

    def list_hashCode_DOUBLE(list: List[Double]): Int = {
      list.hashCode
    }

    def list_find_DOUBLE(l: List[Double]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ l.contains(i)
        i += 10000
      }
      b
    }

    def list_insert_LONG(): List[Long] = {
      var l: List[Long] = null
      var i = 0
      while (i < testSize) {
        l = new List[Long](i, l)
        i += 1
      }
      l
    }

    def list_hashCode_LONG(list: List[Long]): Int = {
      list.hashCode
    }

    def list_find_LONG(l: List[Long]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ l.contains(i)
        i += 10000
      }
      b
    }
  }

  private[this] object TestArray {
    def array_insert(): ResizableArray[Int] = {
      val a: ResizableArray[Int] = new ResizableArray[Int]()
      var i = 0
      while (i < testSize) {
        a.add(i)
        i += 1
      }
      a
    }

    def array_reverse(a: ResizableArray[Int]): ResizableArray[Int] = {
      a.reverse
      a
    }

    def array_find(a: ResizableArray[Int]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ a.contains(i) // TODO: Does this cost much?
        i += 10000
      }
      b
    }

    def array_insert_DOUBLE(): ResizableArray[Double] = {
      val a: ResizableArray[Double] = new ResizableArray[Double]()
      var i = 0
      while (i < testSize) {
        a.add(i)
        i += 1
      }
      a
    }

    def array_reverse_DOUBLE(a: ResizableArray[Double]): ResizableArray[Double] = {
      a.reverse
      a
    }

    def array_find_DOUBLE(a: ResizableArray[Double]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ a.contains(i) // TODO: Does this cost much?
        i += 10000
      }
      b
    }

    def array_insert_LONG(): ResizableArray[Long] = {
      val a: ResizableArray[Long] = new ResizableArray[Long]()
      var i = 0
      while (i < testSize) {
        a.add(i)
        i += 1
      }
      a
    }

    def array_reverse_LONG(a: ResizableArray[Long]): ResizableArray[Long] = {
      a.reverse
      a
    }

    def array_find_LONG(a: ResizableArray[Long]): Boolean = {
      var i = 0
      var b = true
      while (i < testSize) {
        b = b ^ a.contains(i) // TODO: Does this cost much?
        i += 10000
      }
      b
    }
  }

  def testGeneric(megamorphic: Boolean) = {
    import TestArray._
    import TestList._

    val transformation = "generic " + (if (megamorphic) "mega" else "mono")

    def forceMegamorphicCallSites(): Unit =
      if (megamorphic) {
        withTestSize(megamorphicTestSize) {
          array_find(array_reverse(array_insert()))
          list_hashCode(list_insert()); list_find(list_insert())
          array_find_LONG(array_reverse_LONG(array_insert_LONG()))
          list_hashCode_LONG(list_insert_LONG()); list_find_LONG(list_insert_LONG())
          array_find_DOUBLE(array_reverse_DOUBLE(array_insert_DOUBLE()))
          list_hashCode_DOUBLE(list_insert_DOUBLE()); list_find_DOUBLE(list_insert_DOUBLE())
        }
      }

    var a: ResizableArray[Int] = null
    var b: Boolean = true
  //  test(transformation, "array.insert ", _ => { forceMegamorphicCallSites; () },                 a = array_insert(),   () => { assert(a.length == testSize); a = null })
  //  test(transformation, "array.reverse", _ => { forceMegamorphicCallSites; a = array_insert() }, a = array_reverse(a), () => { assert(a.length == testSize); a = null })
 //   test(transformation, "array.find   ", _ => { forceMegamorphicCallSites; a = array_insert() }, b = array_find(a),    () => { assert(b == true); a = null })

    var l: List[Int] = null
    var i: Int = 0
  //  test(transformation, "list.insert  ", _ => { forceMegamorphicCallSites; () },                 l = list_insert(),    () => { assert(l.length == testSize); l = null })
  //  test(transformation, "list.hashCode", _ => { forceMegamorphicCallSites; l = list_insert() },  i = list_hashCode(l), () => { assert(i != 0); l = null })
 //   test(transformation, "list.find    ", _ => { forceMegamorphicCallSites; l = list_insert() },  b = list_find(l),     () => { assert(b == true); l = null })
 
    var arr: Array[(Int, Double)] = new Array[(Int, Double)](2000000)
    val random = new Random()
    for( ind <- 0 to 1999999){
       arr(ind) = (random.nextInt(2000000), 0.0)
    }
    test(transformation, "tuples", _ => (), QuickSortTuplesGen.quicksortByKeyGen[Int](arr), () => {})
  }
}
