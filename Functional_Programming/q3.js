// CAMILO GARCIA LA ROTTA
// ID : 260657037
// q3

// NB vars noSpaces and noParenthesis are used to avoid modifying s, in order to conform to assignment rules
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
