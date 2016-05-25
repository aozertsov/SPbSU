package task6

/**
 * Created by alexander on 05.05.16.
 */

import Operators.scalarMult
import Operators.times
import Part2.reverseMatrix
import Printer.matrix2DPrinter
import Printer.matrixPrinterGen
import Printer.vectorPrinterRow
import org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.createJacobiPolynomial
import java.lang.Math.*
import java.util.stream.IntStream

fun main(args: Array<String>){

    val n = 4

    val a = -1.0
    val b = 1.0

    fun f(x:Double) = 2.0 + x

    fun Lu(u: (Double) -> Double, ddy: (Double) -> Double, dy: (Double) -> Double) = { x: Double -> -(4.0 + sin(x)) / (5.0 + 2.0 * sin(x)) * ddy(x) + 0.5 * dy(x) + (2.0 + x / 2.0) * u(x) }

    //table for print result of evaluations
    val table = Array<DoubleArray>(7, { i -> DoubleArray(7) })

    val momentfunctions = Array<(Double) -> Double>(7, {i -> moments(::f, a, b, i + 3, ::Lu)})

    val colocatefunctions = Array<(Double) -> Double>(7, {i -> colocate(::f, ::Lu, i + 3)})

    for (i in 0 .. table.size - 1) {
        table[i][0] = (i + 3).toDouble()
        table[i][1] = momentfunctions[i](a + (b - a) / 2.0)
        table[i][2] = momentfunctions[i]((b  - a) / 2.0)
        table[i][3] = momentfunctions[i](b)
        table[i][4] = abs(colocatefunctions[i](a + (b - a) / 2.0) - table[i][1])
        table[i][5] = abs(colocatefunctions[i]((b - a) / 2.0) - table[i][2])
        table[i][6] = abs(colocatefunctions[i](b) -  table[i][3])
    }
    println("moments:")
    matrix2DPrinter(table)



    for (i in 0 .. table.size - 1) {
        table[i][0] = (i + 3).toDouble()
        table[i][1] = colocatefunctions[i](a)
        table[i][2] = colocatefunctions[i]((a + b) / 2.0)
        table[i][3] = colocatefunctions[i](b)
    }

    println("collocate:")
    matrix2DPrinter(table)
}

fun generateW(size: Int): Array<(Double) -> Double>{
    if (size < 1)
        throw IllegalArgumentException("k must be from 1!")
    return Array(size, {
        i ->
        if (i == 0)
            { x:Double -> x * x + 2.0 * x - 5.0 }
        else
            if (i == 1)
                { x:Double -> x * x * x - 3.0 * x + 2.0 }
            else
                { x -> pow(1 - pow(x, 2.0), 2.0) * createJacobiPolynomial(i - 2, 2, 2).value(x) } })
}

fun generateDW(size: Int): Array<(Double) -> Double>{
    if (size < 1)
        throw IllegalArgumentException("k must be from 1!")
    return Array(size, {
        i ->
        if (i == 0)
            { x:Double -> 2.0 * x + 2.0 }
        else
            if (i == 1)
                { x:Double -> 3.0 * x * x - 3.0}
            else
                { x -> - 2.0 * (i - 1) * pow(1 - pow(x, 2.0), 1.0) * createJacobiPolynomial(i - 1, 1, 1).value(x)} })
}

fun generateDDW(size: Int): Array<(Double) -> Double>{
    if (size < 1)
        throw IllegalArgumentException("k must be from 1!")
    return Array(size, {
        i ->
        if (i == 0)
            { x:Double -> 2.0}
        else
            if (i == 1)
                { x:Double -> 6.0 * x}
            else
                { x ->  4.0 * (i - 1) * i * createJacobiPolynomial(i, 0, 0).value(x)} })
}

fun generatePsi(size: Int):Array<(Double) -> Double>{
    if (size < 1)
        throw IllegalArgumentException("k must be from 1!")
    return Array(size, { i -> { x:Double -> createJacobiPolynomial(i, 0, 0).value(x) } })
}

fun moments(f: (Double) -> Double, a: Double, b: Double, size: Int, Lu: ((Double) -> Double, (Double) -> Double, (Double) -> Double) -> (Double) -> Double): (Double) -> Double{
    val arrayW = generateW(size)
    val arrayDW = generateDW(size)
    val arrayDDW = generateDDW(size)
    val arrayPsi = generatePsi(size)

    val A = Array<DoubleArray>(size, {i -> DoubleArray(size,
            //скалярное произведение Lwj  и psi i (используем  i + 1 и j + 1 тк тут нумерация от нуля, а у Лебедевой от 1)
            { j ->  scalarMult(Lu(arrayW[j], arrayDDW[j], arrayDW[j]), arrayPsi[i], a, b) })
    })
    val B = DoubleArray(size, { i -> scalarMult(f, arrayPsi[i], a, b)})

    println("Matrix A| b:")
    matrixPrinterGen(A, { i -> "" },
            {i, j -> "| %12.9f ".format(A[i][j])},
            { i, j -> "| %12.9f | | %12.9f |".format(A[i][j], B[i])} )

    val D = reverseMatrix(A, 1e-10)

    val CVector = D * B

    println("koefficients:")
    vectorPrinterRow(CVector)

    return generateFunction(arrayW, CVector)
}

fun colocate(function: (Double) -> Double, Lu: ((Double) -> Double, (Double) -> Double, (Double) -> Double) -> (Double) -> Double, size: Int): (Double) -> Double{
    val arrayW = generateW(size)
    val arrayDW = generateDW(size)
    val arrayDDW = generateDDW(size)

    val node = Array<Double>(size, { i -> Math.cos((2 * i) * Math.PI / (2 * size)) })
    node.sort()

    val A = Array<DoubleArray>(size, {i -> DoubleArray(size, { j ->  Lu(arrayW[j], arrayDDW[j], arrayDW[j])(node[i]) }) } )

    val B = DoubleArray(size, { i -> function(node[i])})

    val D = reverseMatrix(A, 1e-10)

    val CVector = D * B

    return generateFunction(arrayW, CVector)
}

fun generateFunction(w: Array<(Double) -> Double>, c: DoubleArray): (Double) -> Double{
    if (w.size != c.size)
        throw IllegalArgumentException("arrays has different sizes!")
    return { x -> IntStream.range(0, c.size - 1).mapToDouble { i -> c[i] * w[i](x) }.sum() }
}