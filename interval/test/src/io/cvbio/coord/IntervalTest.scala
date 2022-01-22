package io.cvbio.coord

import htsjdk.samtools.util.{Interval => HtsJdkInterval, Locatable => HtsJdkLocatable}
import io.cvbio.coord.Interval.MissingName
import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers


/** Unit tests for [[Interval]]. */
class IntervalTest extends AnyFlatSpec with Matchers {

  /** The reference sequence name for chromosome one. */
  private val Chr1: String = "chr1"

  /** The reference sequence name for chromosome two. */
  private val Chr2: String = "chr2"

  "Interval" should "have sensible defaults set" in {
    val interval = Interval(Chr1, 2, 3)
    interval.contig shouldBe Chr1
    interval.start shouldBe 2
    interval.end shouldBe 3
    interval.positiveStrand shouldBe true
    interval.name shouldBe MissingName
  }

  it should "equal another interval only when all fields are equal" in {
    Interval(Chr1, 2, 3) shouldBe Interval(Chr1, 2, 3, name = MissingName)
    Interval(Chr1, 2, 3) should not be Interval(Chr1, 2, 3, name = "NotMissing")
    Interval(Chr1, 2, 3) should not be Interval(Chr1, 2, 3, positiveStrand = false, name = MissingName)
  }

  it should "build itself from an HTSJDK interval with no optional fields set" in {
    val interval = new HtsJdkInterval(Chr1, 2, 3)
    val enhanced = Interval(interval)
    enhanced.contig shouldBe Chr1
    enhanced.start shouldBe 2
    enhanced.end shouldBe 3
    enhanced.positiveStrand shouldBe true
    enhanced.name shouldBe MissingName
  }

  it should "build itself from an HTSJDK interval with all optional fields set" in {
    val interval = new HtsJdkInterval(Chr1, 2, 3, true, "NotMissing")
    val enhanced = Interval(interval)
    enhanced.contig shouldBe Chr1
    enhanced.start shouldBe 2
    enhanced.end shouldBe 3
    enhanced.positiveStrand shouldBe false
    enhanced.name shouldBe "NotMissing"
  }

  it should "build itself from an HTSJDK locatable with no optional fields set" in {
    val interval: HtsJdkLocatable = new HtsJdkInterval(Chr1, 2, 3)
    val enhanced = Interval(interval)
    enhanced.contig shouldBe Chr1
    enhanced.start shouldBe 2
    enhanced.end shouldBe 3
    enhanced.positiveStrand shouldBe true
    enhanced.name shouldBe MissingName
  }

  it should "build itself from an HTSJDK locatable with all optional fields set" in {
    val interval: HtsJdkLocatable = new HtsJdkInterval(Chr1, 2, 3, true, "NotMissing")
    val enhanced = Interval(interval)
    enhanced.contig shouldBe Chr1
    enhanced.start shouldBe 2
    enhanced.end shouldBe 3
    enhanced.positiveStrand shouldBe true // Locatable does not preserve the negative strand field
    enhanced.name shouldBe MissingName    // Locatable does not preserve the name field
  }

  it should "be equals to another interval with the same fields" in {
    val interval = Interval(Chr1, 2, 3, positiveStrand = false, "NotMissing")
    interval.canEqual(interval) shouldBe true
    interval shouldBe interval
  }

  it should "not think it could be equal to an object that is not an interval" in {
    Interval(Chr1, 2, 3).canEqual(1) shouldBe false
  }

  it should "not be equals to another interval with the different fields" in {
    val interval = Interval(Chr1, 2, 3, positiveStrand = false, "NotMissing")
    interval.canEqual(interval) shouldBe true
    interval should not be Interval(Chr1, 5, 5, positiveStrand = false, "NotMissing")
  }

  it should "have the same hash code as another interval with the same fields" in {
    val interval = Interval(Chr1, 2, 3, positiveStrand = false, "NotMissing")
    interval.hashCode shouldBe interval.hashCode
  }

  it should "have the correct length based on the coordinate system" in {
    Interval(Chr1, 2, 2, positiveStrand = false, "NotMissing").length shouldBe 1
    Interval(Chr1, 2, 3, positiveStrand = false, "NotMissing").length shouldBe 2
    Interval(Chr1, 1, 9, positiveStrand = false, "NotMissing").length shouldBe 9
  }

  it should "be able to be cast to an HTSJDK interval" in {
    val enhanced = Interval(Chr1, 2, 3).asHtsJdk
    val original = new HtsJdkInterval(Chr1, 2, 3)
    enhanced mustBe a[HtsJdkInterval]
    enhanced.getContig shouldBe original.getContig
    enhanced.getStart shouldBe original.getStart
    enhanced.getEnd shouldBe original.getEnd
  }

  "Interval.sameCoordinates" should "be true when coordinates are the same" in {
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr1, 2, 3)) shouldBe true
    Interval(Chr1, 3, 3).sameCoordinates(Interval(Chr1, 2, 3)) shouldBe false
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr2, 2, 3)) shouldBe false
  }

  it should "be true when coordinates are the same against an HTSJDK interval" in {
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr1, 2, 3).asHtsJdk) shouldBe true
    Interval(Chr1, 3, 3).sameCoordinates(Interval(Chr1, 2, 3).asHtsJdk) shouldBe false
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr2, 2, 3).asHtsJdk) shouldBe false
  }

  it should "be true when coordinates are the same against an HTSJDK locatable" in {
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr1, 2, 3).asInstanceOf[HtsJdkLocatable]) shouldBe true
    Interval(Chr1, 3, 3).sameCoordinates(Interval(Chr1, 2, 3).asInstanceOf[HtsJdkLocatable]) shouldBe false
    Interval(Chr1, 2, 3).sameCoordinates(Interval(Chr2, 2, 3).asInstanceOf[HtsJdkLocatable]) shouldBe false
  }

  "Interval.IntervalConversions" should "cast an HTSJDK interval to our type of interval implicitly" in {
    import io.cvbio.coord.Interval.IntervalConversions.htsJdkIntervalToInterval
    val original = new HtsJdkInterval(Chr1, 2, 3)
    def test(interval: Interval): Assertion = interval.sameCoordinates(original) shouldBe true
    val _ = test(original)
  }

  it should "cast our type of interval to an HTSJDK interval implicitly" in {
    import io.cvbio.coord.Interval.IntervalConversions.intervalToHtsJdkInterval
    val original = Interval(Chr1, 2, 3)
    def test(interval: HtsJdkInterval): Assertion = original.sameCoordinates(interval) shouldBe true
    val _ = test(original)
  }

  it should "cast our type of interval to an HTSJDK locatable implicitly" in {
    import io.cvbio.coord.Interval.IntervalConversions.intervalToHtsJdkLocatable
    val original = Interval(Chr1, 2, 3)
    def test(locatable: HtsJdkLocatable): Assertion = original.sameCoordinates(locatable) shouldBe true
    val _ = test(original)
  }
}
