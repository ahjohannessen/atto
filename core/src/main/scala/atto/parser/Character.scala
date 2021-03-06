package atto
package parser

import java.lang.String
import atto.syntax.parser._
import scala.{ Char, Boolean, Unit, StringContext, Option }
import scala.Predef.{ charWrapper, augmentString }
import scalaz.syntax.std.option._
import scalaz.syntax.functor._

/** Parsers for various kinds of characters. */
trait Character {
  import combinator._
  import text._

  /** Parser that returns a `Char` if it satisfies predicate `p`. */
  def elem(p: Char => Boolean, what: => String = "elem(...)"): Parser[Char] =
    ensure(1) flatMap (s => {
      val c = s.charAt(0)
      if (p(c)) advance(1) ~> ok(c)
      else err(what)
    }) namedOpaque what

  /** Equivalent to `elem(p)` but without optional label arg. */
  def satisfy(p: Char => Boolean): Parser[Char] =
    elem(p, "satisfy(...)")

  /** Parser that matches and returns only `c`. */
  def char(c: Char): Parser[Char] =
    elem(_ == c, "'" + c + "'")

  /** Parser that matches any character. */
  lazy val anyChar: Parser[Char] =
    satisfy(_ => true)

  /** Parser that matches any character other than `c`. */
  def notChar(c: Char): Parser[Char] =
    satisfy(_ != c) named ("not '" + c + "'")

  /** Decimal digit, 0-9. */
  def digit: Parser[Char] =
    elem(_.isDigit, "digit")

  /** Decimal digit, 0-9. */
  def decimalDigit: Parser[Char] =
    digit

  /** Hex digit, 0-9, A-F, a-f */
  def hexDigit: Parser[Char] =
    charRange('0' to '9', 'a' to 'f', 'A' to 'F')

  /** Binary digit, 0 or 1 */
  def binaryDigit: Parser[Char] =
    elem {
      case '0' | '1' => true
      case _ => false
    }

  /** Octal digit, 0-7 */
  def octalDigit: Parser[Char] =
    charRange('0' to '7')

  def letter: Parser[Char] =
    elem(_.isLetter, "letter")

  def letterOrDigit: Parser[Char] =
    elem(_.isLetterOrDigit, "letterOrDigit")

  def lower: Parser[Char] =
    elem(_.isLower, "lower")

  def spaceChar: Parser[Char] =
    elem(_.isSpaceChar, "spaceChar")

  def upper: Parser[Char] =
    elem(_.isUpper, "upper")

  type CharRange = scala.collection.immutable.NumericRange[Char]

  def charRange(rs: CharRange*) =
    elem(c => rs.exists(_.contains(c)))

  /** `elem` + `map` in a single operation. */
  def optElem[A](p: Char => Option[A]): Parser[A] =
    ensure(1) flatMap { s =>
      p(s.head).cata(a => advance(s.tail.length) ~> ok(a), err("optElem(...)"))
    } named "optElem(...)"

  /** Parser that skips a `Char` if it satisfies predicate `p`. */
  def skip(p: Char => Boolean): Parser[Unit] =
    elem(p).void named "skip(...)"

}


