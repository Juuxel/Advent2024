pub type Result<T, E = Box<dyn std::error::Error>> = std::result::Result<T, E>;

// Solutions
pub mod day13;

// Utilities
pub mod math;
