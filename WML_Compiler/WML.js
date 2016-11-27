function compile() {
    var inputText = document.getElementById('input').value;
    var AST = parse(inputText)
    var evaluatedAST = evalWML(AST, createEnv(null))
    document.getElementById('output').value = evaluatedAST;
}


//////////// TOKENS ////////////

// pipe simbol : must only have 1 pipe "|"
var PIPE = /^\|/;

// template invocation start : must only have 2 curly braces "{{"
var TSTART = /^{{(?!{)/;

// template invocation end : must only have 2 curly braces "}}"
var TEND = /^}}(?!})/;

// parameter start : must only have 3 curly braces "{{{"
var PSTART = /^{{{(?!{)/;

// parameter end : must only have 3 curly braces "}}}"
var PEND =  /^}}}(?!})/;

// template definiton start : must only have curlybraces + semicolon "{:"
var DSTART = /^\{:/;

// template definition end : must only have semicolon + curlybraces ":}"
var DEND = /^:}/;

// parameter name : anything but "|" or "}}}"
var PNAME = /^((?!}}}|\|)[\s\S])+/ ;

// meaningless text outside template invocation
// anything but "{{" or "{:"
var OUTERTEXT = /^((?!{{|{:)[\s\S])+/; 
    
// meaningful text inside template invocation to be scanned
// anything but "{{{" "{:" "{{" "}}" "|"
var INNERTEXT = /^((?!{{|{{{|{:|\||}})[\s\S])+/;

// meaningful text inside template definition to be scanned
// anything but "{{{" "{{" "{:" ":}" "|"
var INNERDTEXT = /^((?!{{|{{{|{:|\||:})[\s\S])+/;



//////////// PARSER ////////////

// scan a string s for its first found token among an input tokenSet
function scan(s, set) {
	// because keys are transformed into strings and
	// because of the restriction on the use of eval, 
	// I use a globalset to which I map the regex to the string
	var GLOBALSET = {PIPE: PIPE,
				TSTART: TSTART,
				TEND: TEND,
				PSTART: PSTART,
				PEND: PEND,
				DSTART: DSTART,
				DEND: DEND,
				PNAME: PNAME,
				OUTERTEXT: OUTERTEXT,
				INNERTEXT: INNERTEXT,
				INNERDTEXT: INNERDTEXT};

    // for all the tokens in input set		
	for(var t in set){
        // if they are in our global tokenset and they match
        if(GLOBALSET[t] && s.match(GLOBALSET[t])) {
            // return the object containing the token and the matched string
            return {token: t, tokenvalue: s.match(GLOBALSET[t])[0]}
        }     
    }

    // no token matched
	return null;
}

// CAMILO GARCIA 
// ID# 260657037
// q3

// question stated that we could assume correct syntax was recieved as input

// main parser function
function parse(s) {
    // cut off unnecessary spaces surrounding all tokens but OUTERTEXT
    var reg = /\{\{([\s\S]*?)}}|\{:([\s\S]*?):}/g;
    s = s.replace(reg, function(string) {return string.replace(/\s/g, '');})

    // start parsing
    return parseOuter(s);
}

// <outer>
function parseOuter(s) {

    // final Object
    var outer = {name: "outer"};

    // parse OUTERTEXT
    var t = scan(s, {OUTERTEXT: true});
    if(t) {
        // fill AST
        outer[t.token] = t.tokenvalue;       
        // adjust string size           
        s = s.substr(t.tokenvalue.length);
        // null the unmatched nodes
        outer["templateInvocation"] = null;
        outer["templateDefinition"] = null;
    } else {
        // parse <template_Invocation>
        outer["templateInvocation"] = parseTemplateInvocation(s);
        if(outer["templateInvocation"]) {
            // adjust string size
            s = s.substring(getLength(outer["templateInvocation"]));
            // null the unmatched nodes
            outer["OUTERTEXT"] = null;
            outer["templateDefinition"] = null;
        } else {
            // parse <template_Definition>
            // because we expect correct syntax, if we reach this point 
            // text must  absolutly be a templateDefinition
            outer["templateDefinition"] = parseTemplateDefinition(s);
            if(outer["templateDefinition"]) {
                // adjust string size
                s = s.substring(getLength(outer["templateDefinition"]))
                 // null the unmatched nodes
                outer["OUTERTEXT"] = null;
                outer["templateInvocation"] = null;
            }
        }
    }
    
    // verify if there is more string to parse
    outer["next"] = (s) ? parseOuter(s) : null;

    return outer;
}

// <template_Definition>
function parseTemplateDefinition(s) {

    // verify if it is the beginning of <template_Definition>
    var t = scan(s,{DSTART: true});
    if(!t) return null;

    // final object
    var TemplateDef = {name: "templateDefinition"};

    // parse DSTART
    TemplateDef[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    // parse Dtext
    TemplateDef["dtext"] = parseDtext(s);
    s = s.substr(getLength(TemplateDef["dtext"]));
    
    // create AST of parameters
    TemplateDef["param"] = parseParam(s, "D");
    s = s.substr(getLength(TemplateDef["param"]));

    // parse DEND
    t = scan(s, {DEND: true})
    TemplateDef[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    return TemplateDef;
}

// <template_Invocation>
function parseTemplateInvocation(s) {

    // verify if it is the beginning of <template_Invocation>
    var t = scan(s, {TSTART: true});
    if(!t) return null;
    
    // final object
    var TemplateInvo = {name: "templateInvocation"};

    // parse TSTART
    TemplateInvo[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    // parse Itext
    TemplateInvo["itext"] = parseItext(s);
    s = s.substr(getLength(TemplateInvo["itext"]));

    // parse Targs
    TemplateInvo["targ"] = parseParam(s, "I");
    s = s.substr(getLength(TemplateInvo["targ"]));

    // parse TEND
    var t = scan(s, {TEND: true});
    TemplateInvo[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    return TemplateInvo;
}

// generic function to parse parameter (PIPE <(i/d)text>)
function parseParam(s, textForm) {

    // verify if there are any more parameters
    var t = scan(s,{PIPE: true});
    if(!t) return null;

    // final Object
    var param = {name: "param"};

    // parse PIPE
    param[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);
    
    // parse (I/D)text
    param["param"] = (textForm == "D") ? parseDtext(s) : parseItext(s);
    s = s.substr(getLength(param["param"]));

    // parse next arg
    param["next"] = parseParam(s, textForm);

    // in templateDefinition, if no more parameters,
    // then this node is not parameter, but body of function
    if (textForm == "D" && !param["next"]) param.name = "body";

    return param; 
}   

// generic Dtext and Itext parser
// textForm can be "D" to parse Dtext
function parseText(s, textForm){

    // final object
    var text, t;

    if (textForm == "D") {
        text = {name : "dtext"};
        t = scan(s, {INNERDTEXT: true});
    } else if (textForm == "I") {
        text = {name : "itext"};
        t = scan(s, {INNERTEXT: true});
    }
    
    // parse INNER_X_TEXT
    if(t) {
        // fill AST
        text[t.token] = t.tokenvalue;
        // adjust string size           
        s = s.substr(t.tokenvalue.length);
        // null the unmatched nodes
        text["templateInvocation"] = null;
        text["templateDefinition"] = null;
        text["tparam"] = null;
    } else {
         // parse <template_Invocation>
        text["templateInvocation"] = parseTemplateInvocation(s);
        if(text["templateInvocation"]) {
            // adjust string size
            s = s.substring(getLength(text["templateInvocation"]));
            // null the unmatched nodes
            if (textForm == "D") text["INNERDTEXT"] = null;
            else if (textForm == "I") text["INNERTEXT"] = null;
            text["templateDefinition"] = null;
            text["tparam"] = null;
        } else {
            // parse <template_Definition>
            text["templateDefinition"] = parseTemplateDefinition(s);
            if(text["templateDefinition"]) {
                // adjust string size
                s = s.substring(getLength(text["templateDefinition"]))
                // null the unmatched nodes
                if (textForm == "D") text["INNERDTEXT"] = null;
                else if (textForm == "I") text["INNERTEXT"] = null;
                text["templateInvocation"] = null;
                text["tparam"] = null;
            } else {
                // parse <Tparam>
                // because we expect correct syntax, if we reach this point 
                // text must  absolutly be Tparam
                text["tparam"] = parseTparam(s);
                if(text["tparam"]) {
                    // adjust string size
                    s = s.substring(getLength(text.tparam));
                    // null the unmatched nodes
                    if (textForm == "D") text["INNERDTEXT"] = null;
                    else if (textForm == "I") text["INNERTEXT"] = null;
                    text["templateInvocation"] = null;
                    text["templateDefinition"] = null;
                } else {
                    // no matched pattern
                    return null;
                }
            }
        }
    }

    // verify if there is more string to parse
    text["next"] = (s) ? parseText(s, textForm) : null;

    return text;
}

// <Dtext>
function parseDtext(s) {
    return parseText(s, "D");
}

// <Itext>
function parseItext(s){
    return parseText(s, "I");
}

// <Tparam>
function parseTparam(s) {

    // parse <Tparam>
    var t = scan(s,{PSTART: true});
    if(!t) return null;

    // final Object
    var TParam = {name: "tparam"};

    // parse PSTART
    TParam[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    // parse PNAME
    t = scan(s, {PNAME: true})
    TParam[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    // parse PEND
    t = scan(s, {PEND: true})
    TParam[t.token] = t.tokenvalue;
    s = s.substr(t.tokenvalue.length);

    return TParam;
}

// recursively traverse object to count chars parsed from s,
// this value is the number of chars to cut from s to continue parsing
function getLength(obj) {

    var numOfChars = 0;

    for (var key in obj) {
        // ignore name and null properties
        if(key !=  "name" && obj[key]) {
            // if property is itself a node, recursively evaluate
            if(typeof(obj[key]) == 'object') numOfChars += getLength(obj[key])
            else numOfChars += obj[key].length;
        }    
    }

    return numOfChars;
}

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

//////////// SCANNER ////////////

// create empty env with parent node
function createEnv(parent) {
    return {name: Math.random(),
            bindings: {},    
            parent: parent}
}

// lookup name binding in chain of env
function lookup(name, env) {
    // binding not found
    if (!env) return null;
    // binding found, return value
    if(env["bindings"][name]) return env["bindings"][name];
    // continue looking up recursively
    return lookup(name, env.parent)
}


// CAMILO GARCIA 
// ID# 260657037
// q2 


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
        args.push(evalWML(currNode, env))
       currNode = currNode.next
    }

    // lookup body for function
    var funct= lookup(functName, env);
    
    
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
    
    // detect closure
    if (functName.match(/`/)) {
        // non-anonymous function -> store functName in env
        if(functName.length > 1) {
            functName = functName.substring(1);
            env["bindings"][functName] = {params: params, body: currNode, env: env};
        } 
        // return closure
        return stringify({params: params, body: currNode, env: env});
    } else {
        env["bindings"][functName] = {params: params, body: currNode, env: env};
    }        

    return s;
}

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

// CAMILO GARCIA 
// ID# 260657037
// q4 

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
            // test for anonymous function
            funct = (functName.match(/{\"/)) ? unstringify(functName) : lookup(functName, env);
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
    
    // detect closure
    if (functName.match(/`/)) {
        // non-anonymous function -> store functName in env
        if(functName.length > 1) {
            functName = functName.substring(1);
            env["bindings"][functName] = {params: params, body: currNode, env: env};
        } 
        // return closure
        return stringify({params: params, body: currNode, env: env});
    } else {
        env["bindings"][functName] = {params: params, body: currNode, env: env};
    }        

    return s;
}




