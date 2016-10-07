
function cXXXr(s) { 
    // base case, we return the list itself
    if (!s) { return function(list) { return list } };     
    // recursive cases 
    if (s.charAt(0) == 'a') return function(list) {return car( cXXXr(s.substring(1))(list)) };
    if (s.charAt(0) == 'd') return function(list) {return cdr( cXXXr(s.substring(1))(list)) };
    throw new Error("Invalid input -> " + s.charAt(0));            
}

function makeXXX(tree, s) {
    function helper(tree) {
        // base case, we dont return an empty string because in js "" == false
        if ((tree == s)) return " ";            
        if(!isList(tree)) return false;
        // recursive cases
        if (helper(car(tree))) return (helper(car(tree))) + "a";
        if (helper(cdr(tree))) return (helper(cdr(tree))) + "d";
    }
    return helper(tree).substring(1);         // crop extra space of base case
}

function wohs(s) {
    // returnd index at which the parenthesis balance
    function balance(s, n) {
        if (n == undefined) n = 1;
        if (n == 0) return s;
        if (s.charAt(0) == "(") return balance(s.substring(1), n + 1);
        if (s.charAt(0) == ")") return balance(s.substring(1), n - 1);
        return balance(s.substring(1), n);
    }
    // returns index of split point between 2 elements of list
    function splitIndex(s) {
        // ex: a(b()) --> return index 1
        if (s.charAt(0) != "(") return s.indexOf("(");
        // ex: (a())b --> return index 5
        return s.length - balance(s.substring(1)).length
    }
    // main helper function
    function helper(s) {
        // base case, we find null list
        if (s.indexOf("()") == 0) return null;
        // we find element of list
        if (s.charAt(0) != "(") {
            if (s.indexOf("(") == -1 ) return s;  // element is alone, no more lists
            return s.substr(0,s.indexOf('('));    // string is followed by more lists 
        }
        var noParenthesis = s.slice(1,-1);
        // recursive case, we find a list, thus we will go inside of it
        return cons( helper(noParenthesis.substring(0, splitIndex(noParenthesis)+1)), helper(noParenthesis.substring(splitIndex(noParenthesis))) );
    }
    var noSpaces = s.replace(/\s/g, '');
    return helper(noSpaces);
}


function partition(list) {
    // return sublist of elements which validate function(list)
    function createUnitaryList(list, fct) {
        // base case, we find empty list
        if (!isList(list)) return null;
        // recursive cases, if function returns true we add to list
        if ( fct(car(list)) )  return cons( car(list), createUnitaryList(cdr(list), fct));          
        else return createUnitaryList(cdr(list), fct);
    }
    // main helper, returns main list of lists
    function createFinalList(fctArr) {
        // base cases, we return null if no function was specified
        if (fctArr.length == 0) return null;
        if (fctArr.length == 1) return createUnitaryList(list, fctArr[0]);
        // to respect functional paradigm we create local arrays 
        // of the first element and the rest of the array
        var unitArr = fctArr.slice(0,1);
        var restOfArr = fctArr.slice(1);
        // recursive case
        return cons( createFinalList(unitArr), createFinalList(restOfArr));
    }

    // to respect functional paradigm we create local arrays 
    // of the arguments obj and a second one without the first arg -> list
    var arr = Array.prototype.slice.call(arguments);
    var noListArr = arr.slice(1);

    return createFinalList(noListArr);
}


function constructAA() {
    return cons("", "");
}

//console.log(show(constructAA()))

function addAA(aa,key,value) {
    // if key doesn't exist in AA
    if( show(aa).indexOf(key) == -1) return cons(cons(key, value), aa)
    // replaces key in AA
    function replace(aa) {
        // handle empty arrays
        if (show(aa) == "") return  "";
        // base cases
        if ((!isList(car(aa))) && (!isList(cdr(aa)))){
            // if the aa doesn't contain the key
            if (show(aa).indexOf(key) < 0) return aa;
            // if the aa contains the key
            return cons(key, value);
        }
        //recursive case
        return cons(replace(car(aa)), replace(cdr(aa)));
    }   
    return replace(aa);
}

//console.log(show(addAA(addAA(constructAA(),"name","clark"),"name",100)))

function getValueAA(aa,key) {
    // base cases
    if (!isList(aa)) return false;
    if (car(aa) == key) return cdr(aa);
    // recursive case
    return ((getValueAA(car(aa), key)) || getValueAA(cdr(aa), key))
}

// heavily based on pairs.js show(list)
function showAA(aa) {
    var sval;
    // ignore empty aa
    if (car(aa) == "" && cdr(aa) == "") sval = "";          
    else if (!isList(car(aa))) {
        if (!isList(cdr(aa))) sval = car(aa) + " : " + cdr(aa);
        else sval = showAA(cdr(aa)) + "\n";
    } 
    else if (!isList(cdr(aa))) sval = showAA(car(list)) + "\n";
    else sval = showAA(car(aa)) + "\n" + showAA(cdr(aa));

    String(sval)
    return sval
}



console.log(showAA(addAA(addAA(addAA(constructAA(),"name","clark"),"age",10000), "name", 123)))
/////////////////////////////////////////////////////////////////////////

function cons(a,b) {
    return function (selector) {
        if (selector=='areyoualist?')
            return 'yesIam';
        return selector(a,b);
    };
}

function car(list) {
    function carHelper(a,b) {
        return a;
    }
    return list(carHelper);
}

function cdr(list) {
    function cdrHelper(a,b) {
        return b;
    }
    return list(cdrHelper);
}

function isList(thing) {
    if (typeof(thing)!='function')
        return false;
    try {
        if (thing('areyoualist?')=='yesIam')
        return true;
    } catch(e) {
    }
    return false;
}

function show(list) {
    var sval;
    if (list==null)
        sval = '()';
    else if (isList(list))
        sval = '('+ show(car(list)) +' '+show(cdr(list))+')';
    else 
        sval = String(list);
    return sval;
}


function cons(a,b) {
    return function (selector) {
        if (selector=='areyoualist?')
            return 'yesIam';
        return selector(a,b);
    };
}

function car(list) {
    function carHelper(a,b) {
        return a;
    }
    return list(carHelper);
}

function cdr(list) {
    function cdrHelper(a,b) {
        return b;
    }
    return list(cdrHelper);
}

function isList(thing) {
    if (typeof(thing)!='function')
        return false;
    try {
        if (thing('areyoualist?')=='yesIam')
        return true;
    } catch(e) {
    }
    return false;
}

function show(list) {
    var sval;
    if (list==null)
        sval = '()';
    else if (isList(list))
        sval = '('+ show(car(list)) +' '+show(cdr(list))+')';
    else 
        sval = String(list);
    return sval;
}

// Construct a random tree based on pairs constructed using cons.
// Branches are deepened with probability p.
// Returns an object, with a "tree" field containing the random tree,
// and a "target" field containing a randomly selected, unique name
// within the tree.
function rndTree(p) {
    // Keep track of all names used, so we can ensure they are unique,
    // and also find a random name later too.
    var allNames = [];
    // The number of characters in a random string.
    var nameLength = 5;

    // Returns a random integer 0..max-1.
    function rndInt(max) {
        return Math.floor(Math.random() * max);
    }

    // Constructs a random string, as a tree element.
    function rndString() {
        // The set of characters from which the random name will be derived.
        var alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        // A recursive helper to progressively append n chars onto s.
        function helper(n,s) {
            if (n==0)
                return s;
            return helper(n-1,s + alphas.charAt(rndInt(alphas.length)));
        }
        var name = helper(nameLength,'');
        // Here we ensure the name just constructed is unique within the tree,
        // and if not we try again recursively.
        if (allNames.indexOf(name)>=0)
            return rndString();
        // Ok, unique, so record the name.
        allNames.push(name);
        return name;
    }

    // This function actually constructs the random tree, recursively deepening either the 
    // first or second of the pair with probability p.
    function rndTreeHelper(p) {
        return cons(
            (Math.random()<p) ? rndTreeHelper(Math.max(0,p-0.01)) : rndString(),
            (Math.random()<p) ? rndTreeHelper(Math.max(0,p-0.01)) : null);
    }
    
    var t = rndTreeHelper(p);

    return { target: allNames[rndInt(allNames.length)],
             tree: t };
}
