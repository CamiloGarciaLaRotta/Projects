// CAMILO GARCIA LA ROTTA
// ID : 260657037
// q2

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
