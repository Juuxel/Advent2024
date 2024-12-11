open System.Collections.Generic
open System.Numerics

let numberOfDigits (num: BigInteger) = 1 + int (BigInteger.Log10 num)

let rec blink (cache: Dictionary<int * BigInteger, int64>) depth num =
    let cacheKey = (depth, num)
    if cache.ContainsKey cacheKey then
        cache[cacheKey]
    else
        let result =
            if depth <= 0 then
                1L
            elif num = BigInteger.Zero then
                blink cache (depth - 1) BigInteger.One
            else
                let d = numberOfDigits num
                if d % 2 = 0 then
                    let div = BigInteger.Pow(new BigInteger(10), d / 2)
                    let (l, r) = BigInteger.DivRem(num, div)
                    (blink cache (depth - 1) l) + (blink cache (depth - 1) r)
                else
                    blink cache (depth - 1) (num * BigInteger(2024))
        cache[cacheKey] <- result
        result

let cache = Dictionary()
let input =
    System.IO.File.ReadLines("day11.txt")
    |> Seq.exactlyOne
    |> _.Split(" ")
    |> Array.map (BigInteger.Parse)

input
|> Array.sumBy (blink cache 25)
|> printfn "Part 1: %d"

input
|> Array.sumBy (blink cache 75)
|> printfn "Part 2: %d"
