"use script"

function Variable(letter){ this.letter = letter; }
Variable.prototype.evaluate = function (x, y, z) {
    if (this.letter === 'x') return x;
    if (this.letter === 'y') return y;
    if (this.letter === 'z') return z;
}
Variable.prototype.toString = function () {
    return this.letter;
}
Variable.prototype.prefix = function () {
    return this.letter;
}

function Const(a){ this.a = a; }
Const.prototype.evaluate = function () {
    return this.a;
}
Const.prototype.toString = function () {
    return this.a.toString();
}
Const.prototype.prefix = function () {
    return this.a.toString();
}

function classCreator(Ancestor, f, operate){
    function Func(g1, g2){ Ancestor.call(this, f, operate, g1, g2); }
    Func.prototype = Object.create(Ancestor.prototype);
    return Func;
}

function Operate1Arg(mainF, operate, F){
    this.mainF = mainF;
    this.operate = operate;
    this.F = F;
}
Operate1Arg.prototype.evaluate = function (x, y, z){
    return this.mainF(this.F.evaluate(x, y, z));
}
Operate1Arg.prototype.toString = function (){
    return this.F.toString() + " " + this.operate;
}
Operate1Arg.prototype.prefix = function (){
    return "(" + this.operate + " " + this.F.prefix() + ")";
}

function Operate2Arg(mainF, operate, F1, F2){
    this.mainF = mainF;
    this.operate = operate;
    this.F1 = F1;
    this.F2 = F2;
}
Operate2Arg.prototype.evaluate = function (x, y, z){
    return this.mainF(this.F1.evaluate(x, y, z), this.F2.evaluate(x, y, z));
}
Operate2Arg.prototype.toString = function (){
    return this.F1.toString() + " " + this.F2.toString() + " " + this.operate;
}
Operate2Arg.prototype.prefix = function (){
    return "(" + this.operate + " " + this.F1.prefix() + " " + this.F2.prefix() + ")"
}

Negate = classCreator(Operate1Arg, (x) => x * -1, "negate");

ArcTan = classCreator(Operate1Arg, (x) => Math.atan(x), "atan");

Add = classCreator(Operate2Arg, (x, y) => x + y, "+");

Subtract = classCreator(Operate2Arg, (x, y) => x - y, "-");

Multiply = classCreator(Operate2Arg, (x, y) => x * y, "*");

Divide = classCreator(Operate2Arg, (x, y) => x / y, "/");

ArcTan2 = classCreator(Operate2Arg, (x, y) => Math.atan2(x, y), "atan2");

Sinh = classCreator(Operate1Arg, (x) => Math.sinh(x), "sinh");

Cosh = classCreator(Operate1Arg, (x) => Math.cosh(x), "cosh");

let pos = 0;
let balance = 0;
let expression = "";
let size = 0;

letters = {
    "x": Variable,
    "y": Variable,
    "z": Variable,
}

operates2Args = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide
}

operates1Args = {
    "negate": Negate,
    "sinh" : Sinh,
    "cosh" : Cosh
}

function parsePrefix(exp){
    pos = 0;
    balance = 0;
    expression = exp;
    size = exp.length;
    let res = useCommand()
    while (pos < size) {
        if (expression[pos] === ")") balance--;
        if (expression[pos] !== " " && expression[pos] !== ")"){
            throw new CallError("Лишний символ для данного выражения на позиции " + pos + " " + expression);
        }
        pos++;
    }
    if (balance !== 0) {
        throw new CallError("Число открывающихся скобок не равно числу закрывающихся " + expression);
    }
    return res;
}

function useCommand(){
    let op = takeCommand();
    if (op === "("){
        balance++;
        op = takeCommand();
        if (op in operates1Args){
            let f = operates1Args[op];
            return new f(useCommand());
        }
        let f = operates2Args[op];
        if (f === undefined) throw new CallError("Данный символ на позиции " + (pos - 1) + " не является операцией " + expression);
        return new f(useCommand(), useCommand());
    }
    if (!isNaN(op)) return new Const(parseInt(op));
    if (op in letters) return new Variable(op);
    throw new CallError("Минус может быть либо операцией, либо вместе с числом. Позиция " + pos + " в " + expression);
}

isLetter = letter => letter.toUpperCase() !== letter.toLowerCase();

isDigit = num => !isNaN(num) && !(num === " ");

function skip(){
    while (pos < size && (expression[pos] === ' ' || expression[pos] === ")")){
        if (expression[pos] === ")"){
            balance--;
            if (balance < 0) throw new CallError("Баланс скобок нарушен на позиции " + pos + " выражения " + expression);
        }
        pos++;
    }
}

function takeCommand() {
    if (expression.length === 0) throw new CallError("Пустой ввод");
    skip();
    if (pos >= size) throw new CallError("Недостаточно аргументов для операции выражения" + expression);
    let sign = "";
    if (expression[pos] === "-" && isDigit(expression[pos + 1])) {
        sign = "-";
        pos++;
    }
    if (isDigit(expression[pos])) {
        let numb = expression[pos++];
        while (isDigit(expression[pos])) {
            numb += expression[pos++];
        }
        return sign + numb;
    } else if (!(expression[pos] in operates2Args) && expression[pos] !== "(" && !(expression[pos] in letters)) {
        let command = "";
        do {
            command += expression[pos++];
        } while (pos < expression.length && isLetter(expression[pos]));
        if (command in operates1Args) return command;
        else throw new CallError("Неизвестный символ " + command + " в выражении: " + expression);
    }
    return expression[pos++];
}

function CallError(cause){
    this.cause = cause;
    Error.call(cause);
}
CallError.prototype = Object.create(Error.prototype);

