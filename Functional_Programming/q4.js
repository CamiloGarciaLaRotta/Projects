// CAMILO GARCIA LA ROTTA
// ID : 260657037
// q4

// NB the question demanded to handle lists only, not trees
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
