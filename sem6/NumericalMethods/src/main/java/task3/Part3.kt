
import Operators.*
import Printer.matrixPrinterGen
import Printer.vectorPrinterCol
import Printer.vectorPrinterRowGen
import task3.Range
import java.lang.Math.*
import java.util.*

/**
 * Created by alexander on 09.04.16.
 */

fun main(args : Array<String>) {

    //    val matrix = arrayOf(doubleArrayOf(2.19587, 0.34563, 0.17809), doubleArrayOf(0.34563, 3.16088, 0.55443), doubleArrayOf(0.17809, 0.55443, 4.89781))
    //    val b = doubleArrayOf(2.16764, 6.52980, 6.29389)
    //    var eps = 1e-32
    val matrix = arrayOf(doubleArrayOf(2.20886, 0.31984, 0.15751),
                         doubleArrayOf(0.31984, 3.18182, 0.52629),
                         doubleArrayOf(0.15751, 0.52629, 4.98873) )

    //    val matrix = arrayOf(doubleArrayOf(2.23089, 0.28226, 0.12848),
    //                         doubleArrayOf(0.28226, 3.21321, 0.48141),
    //                         doubleArrayOf(0.12848, 0.48141, 5.02259) )

    var eps = 1e-7

    var b = doubleArrayOf(2.18310, 6.63605, 6.44335)

    //var b = doubleArrayOf(2.20866, 6.78963, 6.66116)

    var k_iter = intArrayOf(0)

    println("matrix A | b")
    matrixPrinterGen(matrix, {i -> ""},
            {i, j -> "| %12.9f ".format(matrix[i][j])},
            { i, j -> "| %12.9f |  %12.9f |".format(matrix[i][j], b[i]) })

    println()
    println("resolve via Gauss")
    var solutionX = Part2.simpleGauss(matrix, b, eps)
    vectorPrinterCol(solutionX)

    println()
    println("resolve via Gersgoshins's circles")
    var a = gershgorinCircle(matrix)
    println(a.toString())

    println()
    println("Bx + c")
    converttoBXplusC(matrix, b, a[0].alpha)

    println()
    println("apriority k")
    println("aprior k = ${apriorK(matrix, b, DoubleArray(3), eps)}")

    println()
    println("iteration method")
    var xk = iterMethod(matrix,b, DoubleArray(3), solutionX, eps, k_iter)

    println()
    println("Chebyshev's method")
    chebyshevMethod(matrix, b, DoubleArray(3), eps, k_iter, a[0].begin, a[0].end)

}

fun gershgorinCircle(matrix: Array<DoubleArray>): ArrayList<Range> {
    val A = Array<DoubleArray>(matrix.size, { i -> DoubleArray(matrix[i].size, { j -> matrix[i][j] }) })
    val n = A.size
    val ranges = ArrayList<Range>(n)

    var min = matrix[0][0]
    var max = 0.0

    for (i in 0..matrix.size - 1) {
        var diag = matrix[i][i]
        var sum = 0.0
        for (j in 0..matrix.size - 1)
            if (j != i)
                sum += abs(matrix[i][j])
        if (min > (diag - sum))
            min = diag - sum
        if (max < (diag + sum))
            max = diag + sum
    }
    ranges.add(Range(min, max, 2.0 / (min + max)))
    return ranges
}

fun converttoBXplusC(matrix: Array<DoubleArray>, b: DoubleArray, alpha:Double){
    var B = arrayOf(DoubleArray(3), DoubleArray(3), DoubleArray(3))
    var c = DoubleArray(3)

    var E = arrayOf(doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(0.0, 0.0, 1.0))

    for (i in 0..2)
        c[i] = alpha * b[i]

    for (i in 0 .. 2)
        for (j in 0 .. 2)
            B[i][j] = E[i][j] - (alpha * matrix[i][j])

    matrixPrinterGen(B, {i -> ""},
            {i, j -> "| %12.9f ".format(B[i][j])},
            { i, j -> if (i == 1)"| %12.9f | + | %12.9f |".format(B[i][j], c[i])
            else "| %12.9f |   | %12.9f |".format(B[i][j], c[i])})
    println("matrix norm:  ${matrixNorm(B)}")

}

fun generateDiag(m:Array<DoubleArray>): Array<DoubleArray>{
    var result = Array<DoubleArray>(m.size, {i -> DoubleArray(m[i].size, { j -> if (i == j) m[i][i] else 0.0 }) })
    return result
}
fun cD(m:Array<DoubleArray>, vector: DoubleArray, eps:Double): DoubleArray{
    var reverse = Part2.reverseMatrix(generateDiag(m), eps)
    return reverse * vector
}

fun apriorK(m: Array<DoubleArray>, b: DoubleArray, x0: DoubleArray, eps:Double): Int {

    val pair = transformToHg(m, b)
    val H = pair.first
    val g = pair.second
    val n = H.size
    val normH = matrixNorm(H)

    val cd = cD(m, b, eps)

    var xK = (H * x0) + cd

    //По формулам
    var k = 0
    val normx0 = vectorNorm(x0)
    val normg = vectorNorm(g)
    //x^(k-1)
    val xk_1 = x0.clone()
    val aprior = pow(normH, k.toDouble()) * normx0 + pow(normH, k.toDouble()) / (1 - normH) * normg
    val apost = normH / (1 - normH) * vectorNorm(xK - xk_1)

    val t = eps * (1 - normH) / aprior

    // TODO: 04.04.16 long if double
    k = round(log(t) / log(normH)).toInt()
    return k
}

fun transformToHg(matrix: Array<DoubleArray>, b:DoubleArray): Pair<Array<DoubleArray>, DoubleArray> {
    val A = Array<DoubleArray>(matrix.size, {i -> DoubleArray(matrix[i].size, { j -> if (i == j) 0.0 else -(matrix[i][j] / matrix[i][i]) }) })
    val resb = DoubleArray(b.size, {i -> b[i] / matrix[i][i]})
    return Pair(A, resb)
}

// Kotlin can't change arguments
fun iterMethod(m: Array<DoubleArray>, b:DoubleArray, x0: DoubleArray, solutionX: DoubleArray, eps: Double, k_iter:IntArray): DoubleArray {
    val pair = transformToHg(m, b)
    var H = pair.first
    var g = pair.second

    val normH = matrixNorm(H)

    val cd = cD(m, b, eps)

    var xK = (H * x0) + cd

    val xk_x0 = xK - x0

    var k = 0
    val normx0 = vectorNorm(x0)
    val normg = vectorNorm(g)
    var xk_1 = x0.clone()

    //Апрорная и апостериорная оценка по формулам
    var aprior = pow(normH, k.toDouble() + 1) / (1 - normH) * vectorNorm(xK - x0)//* vectorNorm(xK - xk_1)
    //var aprior = pow(normH, k.toDouble()) * vectorNorm(x0) + pow(normH, k.toDouble()) / (1 - normH) * vectorNorm(g)
    var apost = pow(normH, 1.0) / (1 - normH) * vectorNorm(xK - xk_1)

    val formatter = "Count = %d, \n x_k = %s, \n Error = %9.8e, \n Apriority = %9.8e, \n Aposterior = %9.8e \n\n"
    System.out.printf(formatter, k + 1, Arrays.toString(xK), vectorNorm(xK - solutionX), aprior, apost)

    while (vectorNorm(xK - xk_1) > eps) {
        //Пересчитываем x^(k+1) = Hx^(k)+c
        xk_1 = xK.clone()
        xK = H * xK + cd
        k++

        //Пересчитыаем априорную и апостериорную формулы те же
        aprior = pow(normH, k.toDouble() + 1) * normx0 + pow(normH, k.toDouble()) / (1 - normH) * normg
        apost = normH / (1 - normH) * vectorNorm(xK - xk_1)

        //Вывод
        System.out.printf(formatter, k, Arrays.toString(xK), vectorNorm(xK - solutionX), aprior, apost)
    }

    k_iter[0] = k
    return xK
}

fun chebyshevMethod(m: Array<DoubleArray>, b:DoubleArray, x0: DoubleArray, eps:Double,
                    k_iter:IntArray, minG:Double, maxG:Double){

    fun taoFind(tao:DoubleArray, k_cheb:Double){

        for (i in 1.. k_cheb.toInt())
            tao[i] = 2 / ((maxG + minG) - (maxG - minG) * cos((2 * i - 1) * PI / (2 * k_cheb)));
    }

    k_iter[0] = 0
    var k_cheb = Math.round(0.5 * sqrt(maxG / minG) * log(2 / eps)).toDouble()
    var l = 0.0

    while (pow(2.0, l) != k_cheb){
        l++
        if (pow(2.0, l) > k_cheb)
            while (pow(2.0, l) != k_cheb)
                k_cheb++;
    }

    var tao = DoubleArray(k_cheb.toInt() + 1)

    taoFind(tao, k_cheb)

    var x_k = x0.clone()
    var x_k1 = x0.clone()
    for(i in 0.. k_cheb.toInt() - 1){
        k_iter[0]++
        x_k = x_k1.clone()
        x_k1 = x0.clone()
        for (j in 0..2){
            x_k1[j] = x_k[j] - tao[i + 1] * ((m*x_k)[j] - b[j])
        }

        //stupid decision
        vectorPrinterRowGen(x_k1,
                "${k_iter[0]} aproximate: \n( ",
                {i -> "%12.9f, ".format(x_k1[i])},
                { i -> "%12.9f  ) \nnevyazka is:  ${vectorNorm((m*x_k1) - b)}\n".format(x_k1[i]) })
    }
}