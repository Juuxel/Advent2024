fn main() -> advent2024::Result<()> {
    run_day(13, advent2024::day13::solve)?;
    Ok(())
}

fn run_day<F: FnOnce(Vec<String>) -> advent2024::Result<()>>(n: u8, solution: F) -> advent2024::Result<()> {
    let lines = std::fs::read_to_string("day13.txt")?
        .lines()
        .map(|x| x.to_owned())
        .collect::<Vec<_>>();
    timed(|| { solution(lines) }, format!("Day {} finished in", n))
}

fn timed<R, F: FnOnce() -> R>(f: F, message: impl std::fmt::Display) -> R {
    let start = std::time::Instant::now();
    let result = f();
    let end = std::time::Instant::now();
    println!("{} {:?}", message, end - start);
    return result;
}
