// CAMILO GARCIA 
// ID# 260657037
// q4

// construct the valid string representation of the input AST 
function printAST(node) {
    var str = "";

    // iterate through the node's properties
    for(var prop in node) {
        // we ignore nulls and the name property
       if(prop != "name" && node[prop]) {
           // f the property is itself a node return its string aswell
           if(typeof(node[prop]) == 'object') str += printAST(node[prop]);
           else str += node[prop];
       }
    }

    return str;
}