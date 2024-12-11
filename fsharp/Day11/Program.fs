open System.Collections.Generic
open System.Numerics

type CacheKey = { Depth: int
                  Value: BigInteger }

let numberOfDigits (num: BigInteger) =
    1 + (BigInteger.Log10 num |> int)

let getOrPut (dict: Dictionary<'a, 'b>) key value =
    if dict.ContainsKey key then
        dict[key]
    else
        let computed = value()
        dict[key] <- computed
        computed

let rec blink (cache: Dictionary<CacheKey, int64>) depth (num: BigInteger) =
    let cacheKey =
        { Depth = depth
          Value = num }
    getOrPut cache cacheKey (fun () ->
        if cache.ContainsKey cacheKey then
            cache[cacheKey]
        else if depth <= 0 then
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
    )

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
