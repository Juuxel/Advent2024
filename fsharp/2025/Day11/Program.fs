open System.Collections.Generic

let parseLine (line: string) =
    let colonIndex = line.IndexOf ':'
    let start = line.Substring(0, colonIndex)
    let tail = line.Substring(colonIndex + 1).Split " "
    start, List.ofArray tail

let rec countPathsFrom (graph: Map<'K, 'K list>) (start: 'K) (target: 'K) (visited: IDictionary<'K, uint64>) =
    if start = target then
        1UL
    elif visited.ContainsKey start then
        visited[start]
    else
        let next = graph.TryFind start |> Option.defaultValue []
        let mutable counter = 0UL
        for nextStart in next do
            counter <- counter + countPathsFrom graph nextStart target visited
        visited[start] <- counter
        counter

let graph =
    System.IO.File.ReadLines "day11.txt"
    |> Seq.map parseLine
    |> Map

let part1 = countPathsFrom graph "you" "out" (Dictionary())

printfn "%d" part1

let svrToDac = countPathsFrom graph "svr" "dac" (Dictionary())
let dacToFft = countPathsFrom graph "dac" "fft" (Dictionary())
let fftToOut = countPathsFrom graph "fft" "out" (Dictionary())
let svrToFft = countPathsFrom graph "svr" "fft" (Dictionary())
let fftToDac = countPathsFrom graph "fft" "dac" (Dictionary())
let dacToOut = countPathsFrom graph "dac" "out" (Dictionary())

let part2 = svrToDac * dacToFft * fftToOut + svrToFft * fftToDac * dacToOut
printfn "%d" part2
