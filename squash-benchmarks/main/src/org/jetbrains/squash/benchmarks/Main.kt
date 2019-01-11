package org.jetbrains.squash.benchmarks

/*
rows = 100000

Benchmark                               Mode  Cnt    Score    Error  Units

H2QueryBenchmark.iterateJdbc            avgt   10   17.550 ±  5.258  ms/op
H2QueryBenchmark.iterateJdbcName        avgt   10   21.838 ±  5.289  ms/op
H2QueryBenchmark.iterateJdbcObject      avgt   10   19.614 ±  7.932  ms/op
H2QueryBenchmark.iterateMapping         avgt   10   44.539 ±  9.057  ms/op
H2QueryBenchmark.iterateQuery           avgt   10   40.585 ± 14.006  ms/op
H2QueryBenchmark.iterateQueryWhere      avgt   10   43.762 ±  5.420  ms/op

MySqlQueryBenchmark.iterateJdbc         avgt   10   42.771 ±  0.880  ms/op
MySqlQueryBenchmark.iterateJdbcName     avgt   10   44.969 ±  0.847  ms/op
MySqlQueryBenchmark.iterateJdbcObject   avgt   10   44.249 ±  1.077  ms/op
MySqlQueryBenchmark.iterateMapping      avgt   10   68.215 ±  1.362  ms/op
MySqlQueryBenchmark.iterateQuery        avgt   10   61.319 ±  1.196  ms/op
MySqlQueryBenchmark.iterateQueryWhere   avgt   10   42.460 ±  1.202  ms/op

PgQueryBenchmark.iterateJdbc            avgt   10   86.689 ±  1.614  ms/op
PgQueryBenchmark.iterateJdbcName        avgt   10   89.428 ±  0.862  ms/op
PgQueryBenchmark.iterateJdbcObject      avgt   10  104.058 ± 90.815  ms/op
PgQueryBenchmark.iterateMapping         avgt   10  104.205 ±  2.572  ms/op
PgQueryBenchmark.iterateQuery           avgt   10   97.656 ±  1.448  ms/op
PgQueryBenchmark.iterateQueryWhere      avgt   10   71.603 ±  1.424  ms/op

SqLiteQueryBenchmark.iterateJdbc        avgt   10   24.481 ±  1.072  ms/op
SqLiteQueryBenchmark.iterateJdbcName    avgt   10   23.852 ±  0.353  ms/op
SqLiteQueryBenchmark.iterateJdbcObject  avgt   10   32.431 ±  2.004  ms/op
SqLiteQueryBenchmark.iterateMapping     avgt   10   91.309 ±  4.503  ms/op
SqLiteQueryBenchmark.iterateQuery       avgt   10   84.763 ±  3.710  ms/op
SqLiteQueryBenchmark.iterateQueryWhere  avgt   10   44.895 ±  5.184  ms/op
*/

fun main(args: Array<String>) {
    benchmark(args) {
        run<H2QueryBenchmark>()
        run<MySqlQueryBenchmark>()
        run<PgQueryBenchmark>()
        run<SqLiteQueryBenchmark>()
    }
}