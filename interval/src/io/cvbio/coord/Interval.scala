package io.cvbio.coord

import htsjdk.samtools.util.{Interval => HtsJdkInterval, Locatable => HtsJdkLocatable}

/** Any genomic instance that can be stranded. */
trait Stranded { def positiveStrand: Boolean }

/** An intermediate mixin that will provide access to HTSJDK's interval API. */
private[coord] trait IntervalIntermediate extends HtsJdkInterval

/** A better version of HTSJDK's interval class with the upstream API mixed in, but magically hidden. */
sealed trait Interval extends Stranded { this: IntervalIntermediate =>
  val contig: String = this.getContig
  val start: Int = this.getStart
  val end: Int = this.getEnd
  val positiveStrand: Boolean = this.isPositiveStrand
  val name: String = this.getName

  /** The length of this interval. */
  override lazy val length: Int = end - start + 1

  /** Cast this interval to it's HTSJDK counterpart for use in older APIs. */
  def asHtsJdk: HtsJdkInterval = this.asInstanceOf[HtsJdkInterval]

  /** Whether this interval can equal other object instances. */
  def canEqual(a: Any): Boolean = a.isInstanceOf[Interval]

  /** Test for equality of this interval with another of it's kind. */
  override def equals(that: Any): Boolean = that match {
    case that: Interval => that.canEqual(this) &&
      this.contig         == that.contig &&
      this.start          == that.start &&
      this.end            == that.end &&
      this.positiveStrand == that.positiveStrand &&
      this.name           == that.name
    case _ => false
  }

  /** Hash code for this class. */
  override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + (if (contig == null) 0 else contig.hashCode)
    result = prime * result + getStart
    result = prime * result + getEnd
    result = prime * result + (if (positiveStrand) 1 else 0)
    result = prime * result + (if (name == null) 0 else name.hashCode)
    result
  }

  /** If this interval has the same coordinates as another interval. */
  def sameCoordinates(that: Interval): Boolean = {
    this.contig == that.contig && this.start == that.start && this.end == that.end
  }

  /** If this interval has the same coordinates as another HTSJDK interval. */
  def sameCoordinates(that: HtsJdkInterval): Boolean = {
    this.contig == that.getContig && this.start == that.getStart && this.end == that.getEnd
  }

  /** If this interval has the same coordinates as another HTSJDK locatable. */
  def sameCoordinates(that: HtsJdkLocatable): Boolean = {
    this.contig == that.getContig && this.start == that.getStart && this.end == that.getEnd
  }
}

/** Companion object for building [[Interval]] instances. */
object Interval {

  /** The name to use for an [[Interval]] when one is missing. */
  val MissingName: String = "."

  /** The string that separates joined interval names. */
  val NameSeparator: String = "|"

  /** A [[HtsJdkInterval]] enhanced into a Scala-esque API.  */
  private final case class EnhancedInterval(
    override val contig: String,
    override val start: Int,
    override val end: Int,
    override val positiveStrand: Boolean = true,
    override val name: String            = MissingName
  ) extends HtsJdkInterval(contig, start, end, !positiveStrand, name) with IntervalIntermediate with Interval

  /** Build an interval from a locatable. Strand will default to positive and a missing name will be used. */
  def apply(locatable: HtsJdkLocatable): Interval = {
    EnhancedInterval(contig = locatable.getContig, start = locatable.getStart, end = locatable.getEnd)
  }

  /** Build an interval from an HTSJDK interval. Strand will default to positive and a missing name will be used. */
  def apply(htsJdkInterval: HtsJdkInterval): Interval = {
    EnhancedInterval(
      contig         = htsJdkInterval.getContig,
      start          = htsJdkInterval.getStart,
      end            = htsJdkInterval.getEnd,
      positiveStrand = htsJdkInterval.isPositiveStrand,
      name           = Option(htsJdkInterval.getName).getOrElse(MissingName)
    )
  }

  /** Build an interval from a contig, start, end and optional strandedness and name. */
  def apply(contig: String, start: Int, end: Int, positiveStrand: Boolean = true, name: String = MissingName): Interval = {
    EnhancedInterval(contig = contig, start = start, end = end, positiveStrand = positiveStrand, name = name)
  }

  /** Implicit conversions between the various interval types. */
  object IntervalConversions {
    import scala.language.implicitConversions

    /** Implicitly build our type of interval from an HTSJDK type of interval. */
    implicit def htsJdkIntervalToInterval(interval: HtsJdkInterval): Interval = Interval(interval)

    /** Implicitly cast our type of interval to the HTSJDK type of interval. */
    implicit def intervalToHtsJdkInterval(interval: Interval): HtsJdkInterval = interval.asHtsJdk

    /** Implicitly cast our type of interval to the HTSJDK type of locatable. */
    implicit def intervalToHtsJdkLocatable(interval: Interval): HtsJdkLocatable = interval.asHtsJdk
  }
}
