package Part4

import Operators.minus
import Operators.times
import Operators.vectorNorm
import Printer.matrix2DPrinter
import Printer.matrixPrinterGen
import Printer.vectorPrinterRow
import java.lang.Math.*
import java.util.*
/**
 * Created by alexander on 07.04.16.
 */

fun main(args : Array<String>) {

    val s1 = 2.0
    val s2 = 20.0
    val s3 = 200.0

    var a = DoubleArray(2003)
    a[0] = 2.0
    a[1] = 20.0
    a[2] = 200.0
    for (i in 3 .. a.size - 1)
        a[i] = (1 + a[i - 2] + a[i - 1]) / a[i - 3]
    fun sn(n:Int): Double {
        if (n == 3)
            return 200.0
        if (n == 2)
            return 20.0
        if (n == 1)
            return 2.0
        return  ((1 + sn(n - 2) + sn(n - 1)) / sn(n - 3))
    }
    println("posledovat")
    println(a[2001])

    val matrix = arrayOf(doubleArrayOf(-0.881923, -0.046444,  0.334218),
            doubleArrayOf(-0.046444,  0.560226,  0.010752),
            doubleArrayOf( 0.334218,  0.010752, -0.883417) )

    val eps = 0.00001

    var A = arrayOf(doubleArrayOf(-0.881923, -0.046444,  0.334218),
            doubleArrayOf(-0.046444,  0.560226,  0.010752),
            doubleArrayOf( 0.334218,  0.010752, -0.883417) )

    println("we have matrix:")
    matrix2DPrinter(matrix)

    var X = Jacob(A, eps)

    println("we found eigennumbers:")
    for (i in 0 .. A.size - 1)
        println("L${i} = %12.9f ".format(A[i][i]))
    var eigenums = DoubleArray(3, { i -> A[i][i]})

    println("we found corresponding eigenvectors")
    matrixPrinterGen(X, {i -> "V${i} = ( " },
            {i, j -> " %12.9f, ".format(X[j][i])},
            {i, j -> " %12.9f )".format(X[j][i]) } )

    println("eigennumbers * eigenvectors")
    matrixPrinterGen(X, {i -> "V${i} = ( " },
            {i, j -> " %12.9f, ".format(X[j][i] * A[i][i])},
            {i, j -> " %12.9f )".format(X[j][i] * A[i][i]) } )

    //maybe want to check eigenvectors
    var v0 = DoubleArray(3, { i -> X[i][0] })
    println("check L0 * V0 via matrix * V0")
    vectorPrinterRow(matrix * v0)

    var v1 = DoubleArray(3, { i -> X[i][1] })
    println("check L1 * V1 via matrix * V1")
    vectorPrinterRow(matrix * v1)

    var v2 = DoubleArray(3, { i -> X[i][2] })
    println("check L2 * V2 via matrix * V2")
    vectorPrinterRow(matrix * v2)


    println()
    println("matrix * V0 - L0 * V0")
    v0 = (matrix * v0) - (A[0][0]*v0)
    vectorPrinterRow(v0)

    println("nevyazka is")
    println(vectorNorm(v0))

    println("matrix * V1 - L1 * V1")
    v1 = (matrix * v1) - (A[1][1]*v1)
    vectorPrinterRow(v1)
    println("nevyazka is")
    println(vectorNorm(v1))

    println("matrix * V2 - L2 * V2")
    v2 = (matrix * v2) - (A[2][2]*v2)
    vectorPrinterRow(v2)

    println("nevyazka is")
    println(vectorNorm(v2))

}

fun findIJ(m: Array<DoubleArray>):Pair<Int, Int>{
    var a = 0
    var b = 1
    var currentNum = abs(m[0][1])
    for (i in 0..m.size - 1)
        for (j in i + 1..m.size - 1)
            if (currentNum < abs(m[i][j])) {
                a = i;
                b = j;
                currentNum = abs(m[i][j])
            }
    return Pair(a, b)
}

fun findCS(m: Array<DoubleArray>, i: Int, j: Int): Pair<Double, Double>{
    val d = sqrt(pow(m[i][i] - m[j][j], 2.0) + 4 * pow(m[i][j], 2.0))
    val c = sqrt(0.5 * (1.0 + abs(m[i][i] - m[j][j]) / d))
    val s = (m[i][j] * (m[i][i] - m[j][j])) / abs(m[i][j] * (m[i][i] - m[j][j])) *
            sqrt(0.5 * (1.0 - abs(m[i][i] - m[j][j]) / d))

    return Pair(c, s)
}

fun getVIJ(m: Array<DoubleArray>, c: Double, s: Double, i: Int, j: Int): Array<DoubleArray> {
    var V = Array<DoubleArray>(m.size, { i -> DoubleArray(m.size, { j -> if (i == j) 1.0 else 0.0 })})

    V[i][i] = c
    V[j][j] = c
    V[i][j] = -s
    V[j][i] = s
    return V
}

fun generateX(m: ArrayList<Array<DoubleArray>>): Array<DoubleArray>{
    if (m.size > 1){
        var result = m[0]*m[1]
        for (i in 2..m.size - 1)
            result = result * m[i]
        return result}
    else return m[0]
}

fun getNewA(newI: Int, newJ: Int, c:Double, s:Double, A: Array<DoubleArray>) {
    val newA = Array<DoubleArray>(A.size, {i -> DoubleArray(A.size, {j -> A[i][j]}) })
    for (i in 0..A.size - 1) {
        newA[i][newI] = c * A[i][newI] + s * A[i][newJ]
        newA[newI][i] = c * A[i][newI] + s * A[i][newJ]

        if (i != newI && i != newJ) {
            newA[i][newJ] = c * A[i][newJ] - s * A[i][newI]
            newA[newJ][i] = c * A[i][newJ] - s * A[i][newI]
        }
    }
    newA[newI][newI] = c * c * A[newI][newI] + 2 * c * s * A[newI][newJ] + s * s * A[newJ][newJ]
    newA[newJ][newJ] = s * s * A[newI][newI] - 2 * c * s * A[newI][newJ] + c * c * A[newJ][newJ]
    newA[newI][newJ] = 0.0//(c * c - s * s) * A[newI][newJ] + c * s * (A[newJ][newJ] - A[newI][newI])
    newA[newJ][newI] = 0.0

    //kotlin hasn't references as ar
    for (i in 0..newA.size - 1)
        for (j in 0..newA.size - 1)
            A[i][j] = newA[i][j]
}

fun Jacob(A: Array<DoubleArray>, eps:Double): Array<DoubleArray>{

    val historyVIJ = ArrayList<Array<DoubleArray>>()

    var pair = findIJ(A)
    var cs = findCS(A, pair.first, pair.second)
    while (abs(A[pair.first][pair.second]) >= eps) {
        historyVIJ.add(getVIJ(A, cs.first, cs.second, pair.first, pair.second))
        getNewA(pair.first, pair.second, cs.first, cs.second, A)
        pair = findIJ(A)
        cs = findCS(A, pair.first, pair.second)
    }
    return generateX(historyVIJ)
}