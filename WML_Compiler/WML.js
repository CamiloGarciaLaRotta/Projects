function compile() {
    var inputText = document.getElementById('input').value;
    var AST = parse(inputText)
    var evaluatedAST = evalWML(AST, createEnv(null))
	document.getElementById('output').innerHTML = evaluatedAST + "<br><br>"+ JSON.stringify(AST,null,2);
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


//////////// SCANNER ////////////

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
    
    // find a token that matches the string
	for(var t in set){
		var m;
        if(GLOBALSET[t] && (m = s.match(GLOBALSET[t]))) {
            return {token: t, tokenvalue:m[0]}
        }     
    }

    // no token matched
	return {token: t, tokenvalue:null};
}

//////////// PARSER ////////////

// main parser function
function parse(s) {
    // start parsing
    return parseOuter(s);
}

// <outer>
function parseOuter(s) {

    // final Object
    var outer = {name: "outer", length: 0};

    // temporary length of child objects
    var tmpLength;

    // parse OUTERTEXT
    var t = scan(s, {OUTERTEXT: true});
    outer[t.token] = t.tokenvalue;    
    tmpLength = (t.tokenvalue) ? t.tokenvalue.length : 0;         
    outer["length"] += tmpLength;
    s = s.substr(tmpLength);

    // parse <template_Invocation>
    outer["templateInvocation"] = parseTemplateInvocation(s);
    tmpLength = getRecursiveLength(outer["templateInvocation"]);
    outer["length"] += tmpLength;
    s = s.substring(tmpLength);

    // parse <template_Definition>
    outer["templateDefinition"] = parseTemplateDefinition(s);
    tmpLength = getRecursiveLength(outer["templateDefinition"]);
    outer["length"] += tmpLength;
    s = s.substring(tmpLength);

    // verify if there is more string to parse
    outer["next"] = (s) ? parseOuter(s) : null;

    return outer;
}

// <template_Definition>
function parseTemplateDefinition(s) {

    // verify if it is the beginning of <template_Definition>
    var t = scan(s, {DSTART: true});
    if(!t.tokenvalue) return null;

    // final object
    var TemplateDef = {name: "templateDefinition", length: 0};

    // temporary length of child objects
    var tmpLength;

    // parse DSTART
    TemplateDef["length"] += t.tokenvalue.length;
    s = s.substr(t.tokenvalue.length);

    // parse dtext
    TemplateDef["dtext"] = parseDtext(s);
    tmpLength = getRecursiveLength(TemplateDef["dtext"]);
    TemplateDef["length"] += tmpLength;
    s = s.substring(tmpLength);
    
    // parse dparams
    TemplateDef["dparams"] = parseDparams(s);
    tmpLength = getRecursiveLength(TemplateDef["dparams"]);
    TemplateDef["length"] += tmpLength;
    s = s.substring(tmpLength);

    // parse DEND
    t = scan(s, {DEND: true})
    TemplateDef["length"] += t.tokenvalue.length
    s = s.substr(t.tokenvalue.length);

    return TemplateDef;
}

// <template_Invocation>
function parseTemplateInvocation(s) {

    // verify if it is the beginning of <template_Invocation>
    var t = scan(s, {TSTART: true});
    if(!t.tokenvalue) return null;
    
    // final object
    var TemplateInvo = {name: "templateInvocation", length: 0};

    // temporary length of child objects
    var tmpLength;

    // parse TSTART
    TemplateInvo["length"] += t.tokenvalue.length;
    s = s.substr(t.tokenvalue.length);

    // parse itext
    TemplateInvo["itext"] = parseItext(s);
    tmpLength = getRecursiveLength(TemplateInvo["itext"]);
    TemplateInvo["length"] += tmpLength;
    s = s.substring(tmpLength);

    // parse Targs
    TemplateInvo["targs"] = parseTargs(s);
    tmpLength = getRecursiveLength(TemplateInvo["targs"]);
    TemplateInvo["length"] += tmpLength;
    s = s.substring(tmpLength);

    // parse TEND
    var t = scan(s, {TEND: true});
    TemplateInvo["length"] += t.tokenvalue.length
    s = s.substr(t.tokenvalue.length);

    return TemplateInvo;
}

// <targs>
function parseTargs(s){
    return parseParam(s, "i");
}

// <dparams>
function parseDparams(s){
    return parseParam(s, "d");
}

// generic targs and dparams parser
// textForm can be "i" or "d" respectively
function parseParam(s, textForm) {

    // verify if there are any more parameters
    var t = scan(s,{PIPE: true});
    if(!t.tokenvalue) return null;

    // temporary length of child objects
    var tmpLength;

    // final Object
    var param = {length: 0};
    switch(textForm){
        case "i": 
            param["name"] = "targs";

            // parse PIPE
            param["length"] = t.tokenvalue.length;
            s = s.substr(t.tokenvalue.length);

            // parse itext
            param["itext"] = parseItext(s);
            tmpLength = getRecursiveLength(param["itext"]);
            param["length"] += tmpLength;
            s = s.substring(tmpLength);
            break;
        case "d": 
            param["name"] = "dparams";
                       
            // parse PIPE
            param["length"] = t.tokenvalue.length;
            s = s.substr(t.tokenvalue.length);

            // parse dtext
            param["dtext"] = parseDtext(s);
            tmpLength = getRecursiveLength(param["dtext"]); 
            param["length"] += tmpLength;
            s = s.substring(tmpLength);
            break;
    }

    // parse next arg
    param["next"] = parseParam(s, textForm);

    // in templateDefinition, if no more parameters,
    // then this node is not parameter, but body of function
    if (textForm == "d" && !param["next"]) param.name = "body";

    return param; 
}   

// <dtext>
function parseDtext(s) {
    return parseText(s, "d");
}

// <itext>
function parseItext(s){
    return parseText(s, "i");
}

// generic dtext and itext parser
// textForm can be "i" or "d" respectively
function parseText(s, textForm){

    var t, tmpLength;
    
    // final object
    var text = {length: 0};
    switch(textForm){
        case "i": 
            text["name"] = "itext";
            t = scan(s, {INNERTEXT: true});
            break;
        case "d": 
            text["name"] = "dtext";
            t = scan(s, {INNERDTEXT: true});
            break;
    }
    
    // parse INNER_X_TEXT
    text[t.token] = t.tokenvalue;      
    tmpLength = (t.tokenvalue) ? t.tokenvalue.length : 0;     
    text["length"] += tmpLength;
    s = s.substr(tmpLength);

    // parse <template_Invocation>
    text["templateInvocation"] = parseTemplateInvocation(s);
    tmpLength = getRecursiveLength(text["templateInvocation"]);
    text["length"] += tmpLength;
    s = s.substring(tmpLength);
        
    // parse <template_Definition>
    text["templateDefinition"] = parseTemplateDefinition(s);
    tmpLength = getRecursiveLength(text["templateDefinition"]);
    text["length"] += tmpLength;
    s = s.substring(tmpLength);

    // parse <Tparam>
    text["tparam"] = parseTparam(s);
    tmpLength = getRecursiveLength(text["tparam"]);
    text["length"] += tmpLength;
    s = s.substring(tmpLength);
    
    // verify if there is more string to parse
    if (text["length"] > 0) {
        text["next"] = parseText(s, textForm);
    } else {
        return null;
    }

    return text;
}

// <Tparam>
function parseTparam(s) {

    // parse <Tparam>
    var t = scan(s,{PSTART: true});
    if(!t.tokenvalue) return null;

    // final Object
    var TParam = {name: "tparam", length: 0};

    // parse PSTART
    TParam["length"] += t.tokenvalue.length;
    s = s.substr(t.tokenvalue.length);

    // parse PNAME
    t = scan(s, {PNAME: true})
    TParam[t.token] = t.tokenvalue;
    TParam["length"] += t.tokenvalue.length;
    s = s.substr(t.tokenvalue.length);

    // parse PEND
    t = scan(s, {PEND: true})
    TParam["length"] += t.tokenvalue.length;
    s = s.substr(t.tokenvalue.length);

    return TParam;
}

// calculate the total character count of object tree
function getRecursiveLength(obj) {
    if (!obj) return 0;

    var totalLength = 0;
    totalLength += obj["length"];
    if (obj["next"]) totalLength += getRecursiveLength(obj["next"]);

    return totalLength;
}

//////////// EVALUATOR ////////////

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
    s += evalInvoc(ast["templateInvocation"], env);

    // evaluate definition
    s += evalDef(ast["templateDefinition"], env);
    
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

/////////////////////////

var s = "aaa{:hello|hi|{{{hi}}}:} {{hello|heyo}}bbb"
var AST = parse(s)
var WML = evalWML(AST, createEnv(null))

console.log(WML)