# JorkLang

A scripting language built using YAVA.

## How to use

### Prerequisite

- Install java 21
- Install Gradle
- Install cmake

### Step 1

Create a file with extension `.jork` inside the `test-programs` folder

### Step 2

Run the following command

```bash
#debug can be set to true or false
#give the correct filename
make run ARGS="--eval --debug=true --file=<filename>.jork";

```

### REPL

To run the repl use the following command

```bash

make run ARGS="--repl --debug=true"

```

## Documentation

### Variable Declaration

#### Primitives

```
let a = 1;
let b = 20;

a = 12;

let flag = true;
flag = false;

let str = "Hello World"

```

#### Arrays

```
let  arr = [1,2,3];
print(arr);

print(arr[1]);

arr = [1,2,[3,4,5]];
print(arr[2][1]);

```

#### Hash Objects

Keys can only be of hashable types: String, integer, boolean

```
let dic = { "a":[1,2,3] , true: "hello", 1:["hello"] };

print(dic);

```

### Statements

#### Conditionals

Conditionals produce values, i.e. they can be used as expressions

```
let a = 1;
if( a < 1 ) { print(1); }
elif( a < 2 ) { print(2); }
else { print("something"); }


let b = if( a <= 1 ) { 2; } else { 3; }
print(b);

```

#### Function

```
function outerCall(){

    function recur(x) {
        if (x==0) {
            return 0;
        }
        recur(x-1);
        print(x);
    }
    recur(10);

}

outerCall();
```

#### Loop

#### Loops are working now yayyyyy (Only while loop)

```

function printData(){
    let x = 1;

    while(x < 10){
        let y = 1;
        while(y < 10){
            print(x,y,x*y);
            y = y + 1;
        }
        x = x + 1;
    }
}

printData();

```

### BuiltIn functions

- print(data)
- len(data)
- first(array)
- last(array)
- push(array)
- pop(array)
- shift(array)
- sum(array)
- filter(data,function)
- map(data,function)

