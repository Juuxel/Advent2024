pub trait Gcd {
    fn gcd(a: Self, b: Self) -> Self;
}

macro_rules! gcd_impl {
    ($type:ident) => {
        impl Gcd for $type {
            fn gcd(a: Self, b: Self) -> Self {
                let mut a = a;
                let mut b = b;

                while b != 0 {
                    let new_a = b;
                    b = a % b;
                    a = new_a;
                }

                a.abs()
            }
        }
    }
}

gcd_impl!(i32);
gcd_impl!(i64);

pub fn gcd<T: Gcd>(a: T, b: T) -> T {
    T::gcd(a, b)
}
