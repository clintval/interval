# interval

[![Unit Tests](https://github.com/clintval/interval/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/clintval/interval/actions/workflows/unit-tests.yml)
[![Coverage Status](https://codecov.io/gh/clintval/interval/branch/main/graph/badge.svg)](https://codecov.io/gh/clintval/interval)
[![Language](https://img.shields.io/badge/language-scala-c22d40.svg)](https://www.scala-lang.org/)

A better genomic interval that is compatible with [HTSJDK](https://samtools.github.io/htsjdk/javadoc/htsjdk/htsjdk/samtools/util/Interval.html).

![[Snoqualmie Mountain, Washington]](.github/img/cover.jpg)

```scala
val interval = Interval("chr1", start = 1, end = 2, name = "MyInterval")
interval.sameCoordinates(interval) shouldBe true
interval.asHtsJdk.getContig shouldBe "chr1"
```
#### Features

- The Interval is a 1-based fully closed span upon a reference sequence
- Each Interval magically mixes in HTSJDK's Interval while hiding the HTSJDK API
- The HTSJDK API can be summoned by casting the Interval with `.asHtsJdk()`
- Implicit conversions allow for seamless interoperability between either Interval class
- Grants you a memory-friendly way of having a Scala-esque API upon HTSJDK's Interval

#### If Mill is your build tool

```scala
ivyDeps ++ Agg(ivy"io.cvbio.coord::interval::0.0.1")
```

#### If SBT is your build tool

```scala
libraryDependencies += "io.cvbio.coord" %% "interval" % "0.0.1"
```
