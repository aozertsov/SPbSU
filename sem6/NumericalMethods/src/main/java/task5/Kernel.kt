package task5

/**
 * Created by alexander on 01.05.16.
 */
class Kernel(internal val alpha: Array<(Double) -> Double>, internal val betta: Array<(Double) -> Double>) {
    init {
        if (betta.size != alpha.size) {
            throw IllegalArgumentException("different sizes")
        }
    }

    val size = alpha.size
}