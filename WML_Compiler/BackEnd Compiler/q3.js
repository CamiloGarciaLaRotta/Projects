// CAMILO GARCIA 
// ID# 260657037
// q3 

// entrance point for ast evaluation
function evalWML(ast, env) {
    // output string
    var s = "";

    // no more nodes to evaluate
    if(!ast) return s;

    // depending on the node's keys, do a set of evaluations

    // search for printable text
    if (ast["OUTERTEXT"]) s += (ast["OUTERTEXT"]);

    if (ast["INNERDTEXT"]) s += (ast["INNERDTEXT"]);

    if (ast["INNERTEXT"]) s += (ast["INNERTEXT"]);
    
    if (ast["itext"]) return s += evalWML(ast.itext, env);

    if (ast["dtext"]) return s += evalWML(ast.dtext, env);
    
    // print bound value of parameter
    if (ast["tparam"]) {
         var binding = lookup(ast.tparam.pname, env);
         // if no binding is found, return the litreal text of the parameter
         if(binding) s += binding;
         else s += "{{{"+ast.tparam.pname+"}}}";
    } 

    // evaluate invocation
    s += evalInvoc(ast["templateinvoc"], env);

    // evaluate definition
    s += evalDef(ast["templatedef"], env);
    
    // evaluate next node
    s += evalWML(ast.next, env);

    return s;
}

// evaluation of a function invocation
function evalInvoc(ast, env) {
    // output string
    var s = "";

    // no invocation to evaluate
    if(!ast) return s;

    // evaluate function's name
    var functName = evalWML(ast.itext, env);

    // evaluate function's arguments
    var currNode = ast.targs;
    var args = []
    var arg;
    while(currNode) {
        // we must handle nested definitions, but only want to evaluate the necessary body in if/ifeq functions
        // the following switch takes care of this:
        // it stores the body for further evaluation or evaluates it immediatly depending on the function type
        switch(functName) {
        case "#if":
        case "#ifeq":
            args.push(currNode)
            break;
        case "#expr":
        default:
            args.push(evalWML(currNode, env))
        }
       currNode = currNode.next
    }

    // will contain the function to be called
    var funct;

    // check for #special cases
    switch(functName) {
        case "#expr":
            return eval(args[0]);
        case "#if":
            var condition = evalWML(args[0], env);
            // make sure evaluation of expression didn't return a non-bound parameter
            // ex: {:iftest2|x|{{#if|{{{x}}}|A|B}}:}{{iftest2|}} --> because no argumet bound to x it returns {{{x}}}
            return s += (condition && !condition.match(/{{{/)) ? evalWML(args[1], env) : evalWML(args[2], env);
        case "#ifeq":
            return s+= (evalWML(args[0], env) == evalWML(args[1], env)) ? evalWML(args[2], env) : evalWML(args[3], env);
        default:
            funct = lookup(functName, env);
    }
    
    // undefined function
    if (!funct) {
        var argument = (args.length > 0) ? "|"+args.join("|") : "";
        return s += "{{"+functName+argument+"}}";
    }

    // create new env (static)
    var newEnv = createEnv(funct.env);
    // if we wanted dynamic scoping, we would take current called env
    //var newEnv = createEnv(env);

    // bind arguments to parameters
    for(var i = 0; i < funct.params.length; i++) {
        tmpParam = funct.params[i];
        newEnv.bindings[tmpParam] = args[i];
    }

     s += evalWML(funct.body, newEnv);

    return s;
}

// evaluation of a function definition
function evalDef(ast, env) {
    // output string
    var s = "";

    // no definition to evaluate
    if(!ast) return s;

    // evaluate function's name
    var functName = evalWML(ast.dtext, env);

    // evaluate function's arguments
    var currNode = ast.dparams;
    var params = []
    var param;
    // note that my code version for assignment 2 changes the name of the last node
    // in parameters to "body" to facilitate identification at evaluation time
    while(currNode.name != "body") {
        params.push(evalWML(currNode, env))
        currNode = currNode.next
    }
    
    env["bindings"][functName] = {params: params, body: currNode, env: env};    

    return s;
}