package cat.gencat.access


inline fun f(a: Int, b: Int, func: (Int, Int) -> Int): Int = func(a, b)

val initValue = 0

val prod = { x: Int, y: Int -> x * y + initValue }

val sum: (Int, Int) -> Int = { x: Int, y: Int -> x + y + initValue }

val div = fun(x: Int, y: Int): Int = x / y + initValue

val substraction = fun Int.(other: Int): Int = this - other

fun main(args: Array<String>) {
    println(f(3, 2, prod))
    println(f(3, 2, sum))
    println(f(6, 2, div))
}