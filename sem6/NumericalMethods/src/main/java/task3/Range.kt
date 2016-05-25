package task3
/**
 * Created by alexander on 09.04.16.
 */
class Range(internal var begin: Double, internal var end: Double, internal var alpha:Double) {
    override fun toString(): String {
        return begin.toString() + " < Лi < " + end.toString() + " alpha = " + alpha.toString()
    }
}