package task5

/**
 * Created by Alexandr on 01.05.2016.
 */
import Operators.fact
import Operators.minus
import Operators.times
import Operators.vectorNorm
import Part2.reverseMatrix
import Printer.vectorPrinterCol
import Printer.vectorPrinterColGen
import java.lang.Math.pow

fun main (args : Array<String>) {

    fun f (x: Double): Double = 2.0 - x + pow(x, 2.0)

    fun K (x: Double, y : Double) = Math.sinh(x * y)

    val a = 0.0

    val b = 1.0

    val N = 10.0

    fun generateAlpha(size: Int): Array<(Double) -> Double>{
        return Array<(Double) -> Double>(size, {i ->
            { x:Double -> pow(x, (2 * i + 1).toDouble())} })
    }

    fun generateBetta(size: Int): Array<(Double) -> Double>{
        return Array<(Double) -> Double>(size, {i ->
            { x:Double -> pow(x, (2 * i + 1).toDouble()) / fact(2 * i + 1)} })
    }

    val kernel3 = Kernel(generateAlpha(3), generateBetta(3))

    println("u3 vector:")
    val u3 = changeKernel(::f, kernel3, a, b)
    vectorPrinterCol(u3)
    println("result in grid:")
    var funcTable3 = evalWithxK(::f, kernel3, a, b, N)
    vectorPrinterColGen(funcTable3.second, { i ->
        "x$i = %12.9f, f(x$i) = %12.9f".format(funcTable3.first[i], funcTable3.second[i])})

    val kernel4 = Kernel(generateAlpha(4), generateBetta(4))

    println("u4 vector:")
    val u4 = changeKernel(::f, kernel4, a, b)
    vectorPrinterCol(u4)
    println("result in grid:")
    var funcTable4 = evalWithxK(::f, kernel4, a, b, N)
    vectorPrinterColGen(funcTable4.second, { i ->
        "x$i = %12.9f, f(x$i) = %12.9f".format(funcTable4.first[i], funcTable4.second[i])})

    println("delta is:")
    println(vectorNorm(funcTable4.second - funcTable3.second))
}

fun changeKernel(f: (Double) -> Double,
                 kernel: Kernel, a: Double, b: Double): DoubleArray{

    fun simpson (f: (Double) -> Double, a: Double, b: Double, m : Int): Double{
        val h = (b - a) / m.toDouble()
        var res = 0.0;
        var x1 = a;
        var x2 = x1 + h;
        for (k in 0.. m - 1)
        {
            res += (h / 6.0) * (f (x1) + 4.0 * f((x1 + x2) / 2.0) + f(x2))
            x1 += h;
            x2 += h;
        }
        return res;
    }

    fun gamma(i : Int, j: Int): Double = simpson({ x -> kernel.betta[i](x) * kernel.alpha[j](x) }, a, b, 1000)
    fun b(i : Int): Double = simpson({ x -> kernel.betta[i](x) * f(x) }, a, b, 1000)
    fun a(i: Int, j: Int) = kroneker(i, j) + gamma(i, j)

    val A = Array<DoubleArray>(kernel.size,
            { i -> DoubleArray(kernel.size,
                    { j -> a(i, j) }) })

    val B = DoubleArray(kernel.size, { i -> b(i) })

    val D = reverseMatrix(A, 1e-10)

    val C = D * B

    return C
}

fun kroneker(i : Int, j : Int): Double = if (i == j) 1.0 else 0.0

fun evalWithxK(f: (Double) -> Double,
               kernel: Kernel, a: Double, b: Double, N: Double): Pair<DoubleArray, DoubleArray>{
    val h = (b - a) / N
    val result = DoubleArray(N.toInt() + 1, { k -> a + k * h})
    val C = changeKernel(f, kernel, a, b)

    fun U(x: Double): Double {
        var accum = f(x)
        for (i in 0.. kernel.size - 1)
            accum -= C[i]*kernel.alpha[i](x)
        return accum
    }
    return Pair(result, result.map { el -> U(el) }.toDoubleArray())
}