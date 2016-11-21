package org.jetbrains.squash.benchmarks

/*
rows = 100000

Benchmark                               Mode  Cnt   Score   Error  Units

H2QueryBenchmark.iterateJdbc            avgt   10   21.300 ±  3.258  ms/op
H2QueryBenchmark.iterateJdbcName        avgt   10   28.475 ±  4.347  ms/op
H2QueryBenchmark.iterateJdbcObject      avgt   10   20.977 ±  3.832  ms/op
H2QueryBenchmark.iterateMapping         avgt   10   48.772 ± 10.844  ms/op
H2QueryBenchmark.iterateQuery           avgt   10   38.918 ±  6.280  ms/op
H2QueryBenchmark.iterateQueryWhere      avgt   10   40.673 ±  5.529  ms/op

PgQueryBenchmark.iterateJdbc            avgt   10   72.763 ±  1.816  ms/op
PgQueryBenchmark.iterateJdbcName        avgt   10   73.546 ±  1.116  ms/op
PgQueryBenchmark.iterateJdbcObject      avgt   10   78.501 ± 11.667  ms/op
PgQueryBenchmark.iterateMapping         avgt   10   89.469 ±  0.882  ms/op
PgQueryBenchmark.iterateQuery           avgt   10   80.785 ±  0.944  ms/op
PgQueryBenchmark.iterateQueryWhere      avgt   10   54.750 ±  0.885  ms/op

SqLiteQueryBenchmark.iterateJdbc        avgt   10   51.163 ±  1.596  ms/op
SqLiteQueryBenchmark.iterateJdbcName    avgt   10   52.438 ±  4.282  ms/op
SqLiteQueryBenchmark.iterateJdbcObject  avgt   10   48.491 ±  3.911  ms/op
SqLiteQueryBenchmark.iterateMapping     avgt   10  102.348 ± 13.197  ms/op
SqLiteQueryBenchmark.iterateQuery       avgt   10   85.798 ±  3.819  ms/op
SqLiteQueryBenchmark.iterateQueryWhere  avgt   10   77.219 ± 11.202  ms/op
*/

fun main(args: Array<String>) {
    benchmark(args) {
        run<H2QueryBenchmark>()
        run<SqLiteQueryBenchmark>()
        run<PgQueryBenchmark>()
    }
}