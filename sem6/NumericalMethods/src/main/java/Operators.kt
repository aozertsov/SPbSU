/**
 * Created by alexander on 09.04.16.
 */

package Operators

import org.jetbrains.annotations.NotNull

infix operator fun Array<DoubleArray>.times(b:Array<DoubleArray>): Array<DoubleArray> {
    var result = Array<DoubleArray>(this.size, {i -> DoubleArray(this.size, { j -> 0.0 }) })
    if (this.size == b.size) {
        for (i in 0..this.size - 1)
            for (j in 0..b.size - 1)
                for (k in 0..b.size - 1)
                    result[i][j] += this[i][k] * b[k][j]
        return result
    }
    else throw IllegalArgumentException("Different sizes")
}

infix operator fun Array<DoubleArray>.times(b: DoubleArray): DoubleArray {
    var result = DoubleArray(b.size)
    if (this[1].size == b.size){
        for (i in 0..this.size - 1)
            for (j in 0.. this.size - 1)
                result[i] += this[i][j] * b[j]
        return result
    }
    else throw IllegalArgumentException(" ds")
}

infix operator fun Double.times(b: DoubleArray):DoubleArray {
    var result = DoubleArray(b.size, { i -> this * b[i]})
    return result
}

infix operator fun ((Double) -> Double).times(b: (Double) -> Double): (Double) -> Double{
    return { x -> this(x) * b(x) }
}

fun fact(n: Int): Double {
    if (n < 0)
        throw IllegalArgumentException("argument < 0")
    if (n == 1 || n == 0)
        return 1.toDouble()
    var res = 1.0
    for (i in 2 .. n)
        res *= i
    return res.toInt().toDouble() //to int delete mistakes
}

infix operator fun DoubleArray.plus(b: DoubleArray): DoubleArray {
    var result = DoubleArray(b.size)
    if (this.size == b.size){
        for (i in 0..this.size - 1)
            result[i] = this[i] + b[i]
        return result
    }
    else throw IllegalArgumentException("vectors has different sizes")
}

infix operator fun DoubleArray.minus(b: DoubleArray): DoubleArray{
    var result = DoubleArray(b.size)
    if (this.size == b.size){
        for (i in 0..this.size - 1)
            result[i] = this[i] - b[i]
        return result
    }
    else throw IllegalArgumentException("vectors has different sizes")
}


fun matrixNorm(@NotNull x: Array<DoubleArray>): Double {
    var result = 0.0;
    for (i in 0..x.size - 1) {
        var res = 0.0;
        for (j in 0..x.get(0).size - 1)
            res += Math.abs(x.get(i).get(j))
        if (res > result)
            result = res
    }
    return result
}

fun vectorNorm(@NotNull x: DoubleArray): Double {
    var result = 0.0
    for (i in 0..x.size - 1)
        if (result < Math.abs(x[i]))
            result = Math.abs(x[i])
    return result
}

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

fun scalarMult(@NotNull f: (Double) -> Double, @NotNull g: (Double) -> Double,
               @NotNull a: Double, @NotNull b: Double): Double{
    return simpson({ x -> f(x)*g(x) }, a, b, 1000)
}

fun derivate1(f:(Double) -> Double, h: Double): (Double) -> Double {
    return {x -> (f(x + h) - f(x - h)) / (2.0 * h) }
}

fun derivate2(f:(Double) -> Double, h: Double): (Double) -> Double {
    return {x -> (f(x + h) - 2 * f(x) +  f(x - h)) / (h * h) }
}