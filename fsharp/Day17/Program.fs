let program = [2UL; 4UL; 1UL; 3UL; 7UL; 5UL; 0UL; 3UL; 4UL; 3UL; 1UL; 5UL; 5UL; 5UL; 3UL; 0UL]

let tryMin s =
    let l = Seq.toList s
    if List.isEmpty l then
        None
    else
        List.min l |> Some

let seqOf = function
    | Some x -> Seq.singleton x
    | None -> Seq.empty

let filterMap fn =
    Seq.collect (fn >> seqOf)

let rec reverseEngineer program a =
    match program with
    | [] -> Some a
    | b :: tail ->
        let originalB = b
        let b = b ^^^ 5UL
        seq { 0UL..7UL }
        |> Seq.map (fun j -> b ^^^ j &&& 0b111UL)
        |> Seq.filter (fun b2 ->
            let a2 = a ||| b2
            let b3 = a2 &&& 0b111UL
            let b3 = b3 ^^^ 3UL
            let b3 = b3 ^^^ (a2 >>> int32 b3)
            let b3 = b3 ^^^ 5UL
            b3 = originalB)
        |> filterMap (fun b2 ->
            let next = (a ||| b2) <<< 3
            reverseEngineer tail next)
        |> tryMin

reverseEngineer program 0UL |> printfn "%A"
