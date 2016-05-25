/**
 * Created by alexander on 09.04.16.
 */

package Printer

fun twoVectorsPrinterColStandart(v1:DoubleArray, v2:DoubleArray){
    twoVectorsPrinterColGen(v1, v2, { i, v1, v2 -> "| %12.9f | | %12.9f |".format(v1[i], v2[i]) })
}

fun twoVectorsPrinterColGen(v1:DoubleArray, v2:DoubleArray, rowPrinterFun: (i:Int, v1: DoubleArray, v2: DoubleArray) -> String){
    if (v1.size == v2.size){
        for (i in 0..v1.size - 1)
            println(rowPrinterFun(i, v1, v2))
    }
    else
        throw IllegalArgumentException("vectors has different sizes")

}

fun matrix2DPrinter(m:Array<DoubleArray>):Unit {
    matrixPrinterGen(m, { i -> "" },
            { i, j -> "| %12.9f ".format(m[i][j]) },
            { i, j -> "| %12.9f |".format(m[i][j]) } )
}

fun vectorPrinterRow(m:DoubleArray){
    vectorPrinterRowGen(m, "( ",
            { i -> "%12.9f, ".format(m[i])},
            { i -> "%12.9f )".format(m[i]) })
}

fun vectorPrinterCol(m:DoubleArray){
    vectorPrinterColGen(m, { i -> "x${m.size - 1 - i} = %12.9f".format(m[i])})
}

fun vectorPrinterRowGen(m:DoubleArray, startOfLine: String,
                        elementPrintFun:  (i:Int) -> String,
                        endOfLine: (i:Int) -> String){
    print(startOfLine)
    for (i in 0..m.size - 2) {
        print(elementPrintFun(i))
    }
    println(endOfLine(m.size - 1))
}

fun vectorPrinterColGen(m:DoubleArray, elementPrintFun:  (i:Int) -> String){
    for (i in 0..m.size - 1)
        println(elementPrintFun(i))
}

/*
* print 2D matrix to screen
*
* @param m is matrix to print
*
* @param startLineFun is function with arg = rowIndex of matrix. Generate string which
* want to print to start each line
*
* @param elementPrintFun is function with args = rowIndex and colIndex of matrix. Generate string which
* want to print for each element matrix without element with last colIndex.
*
* @param endLineFun if function with arg = colIndex of matrix. Generate string which want to print
* for elements with last index in row.
* */
fun matrixPrinterGen(m:Array<DoubleArray>, startLineFun: (i:Int) -> String,
                     elementPrintFun:  (i:Int, j:Int) -> String,
                     endLineFun: (i:Int, j:Int) -> String){
    for (i in 0..m.size - 1) {
        print(startLineFun(i))
        for (j in 0..m.size - 2)
            print(elementPrintFun(i,j))
        println(endLineFun(i, m.size - 1))
    }
}