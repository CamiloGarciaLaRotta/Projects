// CAMILO GARCIA LA ROTTA
// ID : 260657037
// q1

function cXXXr(s) { 
    // base case, we return the list itself
    if (!s) { return function(list) { return list } };     
    // recursive cases 
    if (s.charAt(0) == 'a') return function(list) {return car( cXXXr(s.substring(1))(list)) };
    if (s.charAt(0) == 'd') return function(list) {return cdr( cXXXr(s.substring(1))(list)) };
    throw new Error("Invalid input -> " + s.charAt(0));            
}
