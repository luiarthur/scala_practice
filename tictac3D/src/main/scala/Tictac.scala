package tictac

/** Playing tic-tac-toe like a pro.
 *
 *
 */

object Tictac {
  import scala.util.Random
  private val rand = new Random(System.currentTimeMillis());

  /*
    H|_|_|_
    _|H|_|_
    _|_|H|_
     | | |H
  */

  class MotherBoard(val n: Int) {
    val allCells = (1 to n*n*n).toSet

    class Cube(val r: Int, val c: Int, val l: Int) {
      override def toString = "("+r+","+c+","+l+")"
      val z = (l-1)*n*n + (r-1)*n + c
      def oob = r<1 || r>n || c<1 || c>n || l<1 || l>n
      def moveTo(d: (Int,Int,Int)) = new Cube(r+d._1, c+d._2, l+d._3)
    }

    private val lst = List(-1,0,1)
    private val dirs = {for(i <- lst; j <- lst; k <- lst) yield (i,j,k)}.filterNot(x => x == (0,0,0))
    //private val dirs = for(i <- lst; j <- lst; k <- lst; if (i,j,k) != (0,0,0)) yield (i,j,k)
    private val coord = (1 to n).toList
    def build(start: Cube, dir: (Int,Int,Int)): Set[Cube] = {
      def loop(s: Cube, S: Set[Cube]): Set[Cube] = {
        if (S.size == n && !s.oob) S else {
          val dest = s.moveTo(dir)
          val newS = S + dest
          if (dest.oob || newS.size > n) S.empty else loop(dest, newS)
        }
      }
      loop(start, Set(start))
    }
    
    val winSets_tmp = for(i <- coord; j <- coord; k <- coord; d <- dirs) yield { 
      build(new Cube(i,j,k), d).map(x => x.z)
    }
    val winSets = winSets_tmp.toSet.filter(x => x != Set.empty)

    /** 
     * @constructor This creates a board
     */
    class Board(val comp: Set[Int], val human: Set[Int]) { // 3D Board

      val emptyCells = allCells diff (comp union human)

      def mark(player: Char, pos: Int) = {
        if (player == 'C') new Board(comp + pos, human) else
        new Board(comp, human+pos)
      }

      // Can I do this using tail recursion?
      override def toString = {
        var out = ""           // using var!!! Can I do this using val?
        for (k <- 1 to n) {
          for (j <- 1 to n) {
            var q = ""         // using var!!! Can I do this using val?
            out += "  "
            for (i <- 1 to n) {
              val ind = i+n*(j-1)+n*n*(k-1)
              val p = if (comp contains ind) "C" else 
                      if (human contains ind) "H" else "_"
              q = if (ind < 10) q+"  " else q+" "
              q += ind.toString + " "
              out += p
            }
            out += "  |  " + q + "\n"
          }
          out += "\n"
        }
        out
      }

      private def opp(player: Char) = if (player == 'C') 'H' else 'C'
      def win(player: Char) = {
        val p = if (player == 'C') comp else human
        winSets.exists(w => w subsetOf p)
      }
      def lose(player: Char) = win(opp(player))
      def draw = emptyCells == Set[Int]() && !win('C') && !win('P')
      def inProg = emptyCells.size > 0 && !win('C') && !win('P')
      def winner = {
        if (!draw) {
          if (win('C')) 'C' else 'H'
        } else 'D'
      }

      def winMove(player: Char): Int = {
        val s = if (player == 'C') comp else human
        if ( s.size >= 3 ) {
          val w = emptyCells.filter( w => mark(player,w).win(player) )
          if (w.size>0) w.head else 0
        } else 0
      }

      def randMove(player: Char): Board = {
        //new stuff
        val w = winMove(player)
        if (w>0) mark(player,w) else {
          val A = emptyCells.toArray
          val cell = A( rand.nextInt(A.size) );
          mark(player, cell)
        }
      }
      /** Plays a random game based on this current board starting with 
       *  `player`.
       */
      def randomGame(player: Char): Board = {
        if (inProg) {
          randMove(player).randomGame(opp(player)) 
        } else this
      }

      def winGame(player: Char): Int = if (winner==player||draw) 1 else 0

      /** prob of winning is computed by simulating N games that
       *  continue the game board and dividing the number of wins by N. 
       *  Each of the N simulations is a random game.
       */
      def probWin(player: Char, pos: Int, N: Int = 100): Double = {
        val sumWin = (1 to N).toList.map(x => mark(player,pos).
            randomGame(opp(player)).winGame(player)).sum
        sumWin.toDouble / N.toDouble
      }
      def smartMove(player: Char, N: Int = 100): Int = {
        //val cells = this.emptyCells.toList.par
        //val probs = cells.map(x => this.probWin(player,x,N)) zip cells
        val w = winMove(player)
        if (w>0) w else {
          val probs = emptyCells.toList.par.
            map(x => (probWin(player,x,N), x) )
          val pmb = probs.maxBy(_._1)
          println("Computer's prob of win or draw: " + pmb._1)
          pmb._2
        }
      }

      /** Super Smart Move. Doesn't work.
       * def smartRandomGame(player: Char, N: Int): Board = {
       *   if (this.inProg) {
       *     this.mark(player,smartMove(player,N)).smartRandomGame(opp(player),N) 
       *   } else this
       * }
       * def superProbWin(player: Char, pos: Int, N: Int): Double = {
       *   val sumWin = (1 to N).toList.map(x => this.mark(player,pos).
       *       smartRandomGame(opp(player),N).winGame(player)).sum
       *   val sumOWin = (1 to N).toList.map(x => this.mark(player,pos).
       *       smartRandomGame(opp(player),N).winGame(opp(player))).sum
       *   sumWin.toDouble / sumOWin.toDouble
       * }
       * def superSmartMove(player: Char, N: Int): Int = {
       *   val cells = this.emptyCells.toList.par
       *   val probs = cells.map(x => this.superProbWin(player,x,N)) zip cells
       *   //println("Computer's odds of winning: " + probs.maxBy(_._1)._1)
       *   probs.maxBy(_._1)._2
       * }
       *
       *
       */

      def playBoard(player: Char, N: Int = 100): Board = {
        def readMove(): Int = {
          println("Enter your move as an Integer")
          val x = readLine()
          if ( x == "" || !(emptyCells contains x.toInt) ) readMove() else x.toInt
        }
        if (inProg) {
          println("Current Board:")
          show
          if (player=='H') {
            val move = readMove()
            mark('H',move).playBoard('C',N)
          } else {
            println("Your opponent is thinking...")
            val move = smartMove('C',N)
            //val move = this.superSmartMove('C',N)
            println(Console.GREEN + "Computer moved to: " + move + Console.RESET)
            mark('C',move).playBoard('H',N)
          }
        } else {
          println("###################################")
          show
          if ( draw ) println("It's a draw!") else {
            if ( winner == 'H' ) { 
              print(Console.GREEN + "Woohoo! You win with: ")
              print(winSets.filter(x => x subsetOf human).flatten.toList.sorted)
              println(Console.RESET)
              human
            } else {
              print(Console.GREEN + "Computer wins by: ")
              print(winSets.filter(x => x subsetOf comp).flatten.toList.sorted)
              println(Console.RESET)
            }
          }
          println("End of Game")
          this
        }
      }

      def show = print(this) 
    }

    object Board {
      ???
    }
  }
}
