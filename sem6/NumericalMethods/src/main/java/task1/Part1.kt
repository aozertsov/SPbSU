package Part1

/**
 * Created by alexander on 29.02.16.
 */

import Printer.twoVectorsPrinterColStandart
import java.lang.Math.log

fun main(args : Array<String>) {
    fun q (x:Double) = 1 + 2.0 * x
    fun p(x: Double) = 1.0
    fun r (x:Double) = -log(1.0 + x)

    fun f(x: Double) = x - 1.0

    val alfa0 = 0.5
    val alfa1 = -1
    val betta0 = 0.7
    val betta1 = 1

    val x0: Double = 0.0
    val xn: Double = 1.0


    var A = 0
    var B = 0

    println("write n")
    val n = readLine()!!.toInt() + 1
    val h: Double = (xn - x0) / (n - 1)

    val a = DoubleArray(n, { i ->
        if (i == 0) 0.0
        else if (i == n - 1) -1.0*betta1
        else p(x0 + i * h) - 0.5 * q(x0 + i * h) * h
    })

    val b = DoubleArray(n, { i ->
        if (i == 0) h * alfa0 - alfa1
        else if (i == n- 1) h * betta0 + betta1
        else -2.0 * p(x0 + i * h) + r(x0 + i * h) * h * h
    })

    val d = DoubleArray(n, { i ->
        if (i == 0) h * A
        else if (i == n - 1) h * B
        else f(x0 + i * h) * h * h
    })

    val c = DoubleArray(n, { i ->
        if (i == 0) alfa1.toDouble()
        else if (i == n - 1) 0.0
        else p(x0 + i * h) + 0.5 * q(x0 + i * h) * h
    })

    var mk = mkGenerate(a, b, c, d)
    var m = mk.first
    var k = mk.second
    var x = xGenerator(a, b, c, d, m , k)

    println("Matrix A | d")
    printingMatrix(a, b, c, d)
    println("vectors m | k")
    twoVectorsPrinterColStandart(m, k)
    nevyazkaPrinter(a, b, c, d, m , k, x)

    a[0] = 0.0;
    b[0] = 2 * h * alfa0 + alfa1 * ((a[1] / c[1]) - 3);
    c[0] = alfa1 * ((b[1] / c[1]) + 4);
    d[0] = 2 * h * A + alfa1 * (d[1] / c[1]);
    a[n - 1] = -betta1 * ((b[n - 2] / a[n - 2]) + 4);
    b[n - 1] = 2 * h * betta0 + betta1 * (3 - (c[n - 2] / a[n - 2]));
    c[n - 1] = 0.0;
    d[n - 1] = 2 * h * B - betta1 * (d[n - 2] / a[n - 2]);

    mk = mkGenerate(a, b, c, d)
    m = mk.first
    k = mk.second
    x = xGenerator(a, b, c, d, m , k)

    println("Second Variant")
    println("Matrix A | d")
    printingMatrix(a, b, c, d)
    println("vectors m | k")
    twoVectorsPrinterColStandart(m, k)
    nevyazkaPrinter(a, b, c, d, m , k, x)
}

fun mkGenerate(a:DoubleArray, b: DoubleArray, c: DoubleArray, d: DoubleArray): Pair<DoubleArray, DoubleArray> {
    if (a.size == b.size && c.size == d.size && a.size == c.size) {
        val m = DoubleArray(a.size)
        val k = DoubleArray(a.size)
        m[0] = -c[0] / b[0]
        k[0] = d[0] / b[0]
        for (i in 0..m.size - 2) {
            m[i + 1] = (-c[i]) / (a[i] * m[i] + b[i]);
            k[i + 1] = (d[i] - a[i] * k[i]) / (a[i] * m[i] + b[i]);
        }
        return Pair(m, k)
    }
    else
        throw IllegalArgumentException("arguments has different sizes")
}

fun printingMatrix(a:DoubleArray, b: DoubleArray, c: DoubleArray, d: DoubleArray) {
    if (a.size == b.size && c.size == d.size && a.size == c.size) {
        for (i in 0..a.size - 1) {
            for (k in 0..a.size - 1) {
                if (k > i) {
                    if (k != i + 1)
                        print ("| %12.9f | ".format(0.0))
                    else {
                        print("| %12.9f | ".format(c[i]))
                    }
                } else {
                    if (k == i) {
                        print("| %12.9f | ".format(b[k]))
                    } else {
                        if (k == i - 1) {
                            print("| %12.9f | ".format(a[i]))
                        } else
                            print ("| %12.9f | ".format(0.0))
                    }
                }
            }
            println("| %12.9f".format(d[i]))
        }
    }
    else
        throw IllegalArgumentException("arguments has different sizes")
}

fun xGenerator(a: DoubleArray, b: DoubleArray, c: DoubleArray, d: DoubleArray, m: DoubleArray, k: DoubleArray): DoubleArray {
    if (a.size == b.size && c.size == d.size && m.size == k.size && a.size == c.size && a.size == m.size) {
        var n = a.size
        val x = DoubleArray(n)
        x[n - 1] = (d[n - 1] - a[n - 1] * k[n - 1]) / (a[n - 1] * m[n - 1] + b[n - 1]);
        for (i in n - 2 downTo 0)
            x[i] = m[i + 1] * x[i + 1] + k[i + 1];
        return x
    }
    else
        throw IllegalArgumentException("arguments has different sizes")
}

fun nevyazkaPrinter(a: DoubleArray, b: DoubleArray, c: DoubleArray, d: DoubleArray, m: DoubleArray, k: DoubleArray, x: DoubleArray){
    if (a.size == b.size && c.size == d.size && m.size == k.size && a.size == c.size && a.size == m.size && a.size == x.size) {
        val nevyazka = DoubleArray(a.size, { i ->
            if (i > 0 && i < a.size - 1)
                (a[i] * x[i - 1] + b[i] * x[i] + c[i] * x[i + 1]) - d[i]
            else
                if (i == 0)
                    b[i] * x[i] + c[i] * x[i + 1] - d[i]
                else
                    a[i] * x[i - 1] + b[i] * x[i] - d[i]
        })
        println("vectors x | nevyazka")
        twoVectorsPrinterColStandart(x, nevyazka)
    }
    else
        throw IllegalArgumentException("arguments has different sizes")
}