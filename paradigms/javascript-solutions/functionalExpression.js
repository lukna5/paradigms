"use script"

const operation2Arg = f => (f1, f2) => (x, y, z) => f(f1(x, y, z), f2(x, y, z));

const operation1Arg = f => f1 => (x, y, z) => f(f1(x, y, z));

const variable = letter => (x, y, z) => {
    if (letter === 'x') return x;
    if (letter === 'y') return y;
    if (letter === 'z') return z;
}

const cnst = value => () => value;

const negate = operation1Arg((num) => num * -1);

const add = operation2Arg((f1, f2) => f1 + f2)

const multiply = operation2Arg((f1, f2) => f1 * f2);

const subtract = operation2Arg((f1, f2) => f1 - f2);

const divide = operation2Arg((f1, f2) => f1 / f2);

expr = subtract(
    multiply(
        cnst(2),
        variable("x")
    ),
    cnst(3)
);
console.log(expr(4));


