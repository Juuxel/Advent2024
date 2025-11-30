use regex::Regex;

use crate::{Result, math};

struct Button(i64, i64);

struct Machine {
    pub button_a: Button,
    pub button_b: Button,
    pub prize: (i64, i64),
}

pub fn solve(lines: Vec<String>) -> Result<()> {
    let button_regex = Regex::new("^Button .: X\\+([0-9]+), Y\\+([0-9]+)+$")?;
    let prize_regex = Regex::new("^Prize: X=([0-9]+), Y=([0-9]+)$")?;
    let machines: Vec<_> = lines
        .split(|x| x.is_empty())
        .map(|lines| {
            let read_button = |line: &str| {
                let captures = button_regex.captures(line).unwrap();
                let x: i64 = captures[1].parse().unwrap();
                let y: i64 = captures[2].parse().unwrap();
                Button(x, y)
            };
            let button_a = read_button(&lines[0]);
            let button_b = read_button(&lines[1]);
            let prize = {
                let captures = prize_regex.captures(&lines[2]).unwrap();
                let x: i64 = captures[1].parse().unwrap();
                let y: i64 = captures[2].parse().unwrap();
                (x, y)
            };
            Machine { button_a, button_b, prize }
        })
        .collect();
    let part_1: i64 = machines.iter()
        .filter_map(|machine| solve_machine(machine, 100, 0))
        .sum();
    println!("{}", part_1);
    let part_2: i64 = machines.iter()
        .filter_map(|machine| solve_machine(machine, i64::MAX, 10_000_000_000_000))
        .sum();
    println!("{}", part_2);
    Ok(())
}

fn solve_machine(machine: &Machine, upper_bound: i64, offset: i64) -> Option<i64> {
    let sol = solve_linear_diophantine_equation(machine.button_a.0, machine.button_b.0, machine.prize.0 + offset, upper_bound)?;

    let denom = machine.button_a.1 * sol.da  + machine.button_b.1 * sol.db;
    let numer = machine.prize.1 + offset - machine.button_a.1 * sol.a0 - machine.button_b.1 * sol.b0;
    if denom == 0 || numer % denom != 0 { return None; }
    let k = numer / denom;
    let a = sol.a0 + k * sol.da;
    let b = sol.b0 + k * sol.db;
    let price = 3 * a + b;
    Some(price)
}

struct Solution {
    pub a0: i64,
    pub b0: i64,
    pub da: i64,
    pub db: i64,
}

fn solve_linear_diophantine_equation(n: i64, m: i64, c: i64, upper_bound: i64) -> Option<Solution> {
    let gcd = math::gcd(n, m);
    if c % gcd != 0 { return None; }

    let da = m / gcd;
    let db = -n / gcd;

    let mut a = 0;
    while a <= upper_bound {
        let rhs = c - n * a;
        if rhs % m == 0 {
            let b = rhs / m;
            return Some(Solution { a0: a, b0: b, da, db });
        }
        a += 1;
    }

    None
}
