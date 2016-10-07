// CAMILO GARCIA LA ROTTA
// ID : 260657037
// q5

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