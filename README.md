# interval

[![Unit Tests](https://github.com/clintval/interval/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/clintval/interval/actions/workflows/unit-tests.yml)
[![Coverage Status](https://codecov.io/gh/clintval/interval/branch/main/graph/badge.svg)](https://codecov.io/gh/clintval/interval)
[![Language](https://img.shields.io/badge/language-scala-c22d40.svg)](https://www.scala-lang.org/)

A better genomic interval compatible with HTSJDK.

![[Snoqualmie Mountain, Washington]](.github/img/cover.jpg)

```scala
val interval = Interval("chr1", start = 1, end = 2, name = "MyInterval")
interval.sameCoordinates(interval) shouldBe true
interval.asHtsJdk.getContig shouldBe "chr1"
```

#### If Mill is your build tool

```scala
ivyDeps ++ Agg(ivy"io.cvbio.coord::interval::0.0.1")
```

#### If SBT is your build tool

```scala
libraryDependencies += "io.cvbio.coord" %% "interval" % "0.0.1"
```
