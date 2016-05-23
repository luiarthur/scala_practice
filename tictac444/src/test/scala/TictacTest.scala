package tictac
import org.scalatest.FunSuite

class FunTicTacSuite extends FunSuite  {
    import Tictac._

    test("Print a 2 x 2 x 2 matrix") {
      println(new Board(Set[Int](), Set[Int](), 2))
    }

    test("Check 3D diag win in 3 x 3 x 3") {
      val B3 = new Board(Set[Int](), Set[Int](), 3)
      val A3 = B3.mark('C',1).mark('C',14).mark('C',27)
      A3.show
      assert(A3.win('C'))
    }

    test("Check 3D diag win in 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val A4 = B4.mark('C',1).mark('C',22).mark('C',43).mark('C',64)
      assert(A4.win('C') && !A4.inProg)
    }

    test("Check 3D diag draw in 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val A4 = B4.mark('C',1).mark('C',22).mark('C',43).mark('C',64)
      assert(!A4.draw())
    }

    test("Check 3D diag lose in 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val A4 = B4.mark('C',1).mark('C',22).mark('C',43).mark('C',64)
      assert(A4.lose('P'))
    }

    test("Check 3D diag win not in Progress in 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val A4 = B4.mark('C',1).mark('C',22).mark('C',43).mark('C',64)
      assert(!A4.inProg)
    }

    test("Check 3D game in Progress in 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val A4 = B4.mark('C',2).mark('C',22).mark('C',43).mark('C',64)
      assert(A4.inProg)
    }

    test("Check all cells for new  4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      assert(B4.allCells == (1 to 64).toSet)
    }

    test("Check all cells for changed  4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      assert(B4.mark('C',4).mark('H',3).allCells == (1 to 64).toSet)
    }

    test("Check empty cells for 4 x 4 x 4") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      assert(B4.mark('C',4).mark('H',3).emptyCells == (1 to 64).filter(x => x != 4 && x != 3).toSet)
    }

    test("Play Random 3 Board") {
      val n = 3
      val B = new Board(Set[Int](), Set[Int](), n)
      val Brand = B.randomGame('H')
      println("The winner is: " + Brand.winner() )
      Brand.show
      assert(true)
    }

    test("Play Random 4 Board") {
      val B4 = new Board(Set[Int](), Set[Int](), 4)
      val Brand = B4.mark('C',1).randomGame('H')
      println("The winner is: " + Brand.winner() )
      Brand.show
      assert(true)
    }

    test("Calculate Prob of End Game 1") {
      val B = new Board(Set[Int](), Set[Int](), 3)
      val C = B.mark('C',1).mark('H',2).mark('C',4).mark('H',5)
      println("P(C wins | C moves to 7) = " + C.probWin('C',7))
      C.show
      assert(true)
    }

    test("Play a smart move") {
      val B = new Board(Set[Int](), Set[Int](), 3)
      val Brand = B.mark('C',1).mark('H',2).mark('C',4).mark('H',5)
      println("Computer should move to: " + Brand.smartMove('C'))
      Brand.show
      assert(true)
    }

    //test("Play vs Computer") {
    //  val B = new Board(Set[Int](), Set[Int](), 3)
    //  B.playBoard('H',100)
    //}
}
