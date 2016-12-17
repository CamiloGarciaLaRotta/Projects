// scan, parse, evaluate and output WML code
function compile() {
    var inputText = document.getElementById('input').value;
    var AST = parse(inputText)
    var WML = evalWML(AST, createEnv(null))
	document.getElementById('output').innerHTML = WML;
}

// avoid the need to copy/paste the given examples of test.txt
function addTests() {
    var tests = 
        "Tests to demonstrate the capabilities of the <font color=\"green\">WML Compiler</font>\n" +
        "<hr><hr>\n" +
        "1. Identity function <font color=\"green\">&#62;</font> {:I|x|{{{x}}}:}{{I|identity!}}\n" +
        "<br>\n" +
        "2. Nested function <font color=\"green\">&#62;</font> {:exclamation|!!!:} {:greet|how|who|{{{how}}} {{{who}}}:} {{greet|Hi|me{{exclamation}} }}\n" +
        "<br>\n" + 
        "3. Closures (named and anonymous) <font color=\"green\">&#62;</font> {:anonClosure|{:`|anon:}:}{:namedClosure|{:`randomName|named:}:} {{ {{namedClosure}} }} and {{ {{anonClosure}} }}\n" +
        "<br>\n" +
        "4. #if, #ifeq, #expr <font color=\"green\">&#62;</font> {:count|start|stop|separator|\n" +
        "{{#ifeq|{{#expr|{{{start}}}<={{{stop}}} }}|true|\n" +
        "{{{start}}}{{#if|{{{separator}}}|{{{separator}}}|}} {{count|{{#expr|{{{start}}}+1}}|{{{stop}}}|{{{separator}}} }} }}:}\n" +
        "{{count|1|10|.}}\n" +
        "<br>\n" +
        "5. Scoping <font color=\"green\">&#62;</font> {:scope|static:} {:showscope|{{scope}}:} {:testScope|{:scope|dynamic:}{{showscope}}:} {{testScope}}\n" +
        "<br>\n" +
        "6. Obliged factorial example <font color=\"green\">&#62;</font> {:fact|n|{{#ifeq|{{#expr|{{{n}}}==0}}|true|1|{{#expr|{{{n}}}*{{fact|{{#expr|{{{n}}}-1}} }} }} }}:} {{fact|6}}\n" +
        "<br>\n"
    document.getElementById('input').innerHTML = tests;        
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
    var outer = {name: "outer", OUTERTEXT: null, templateInvocation: null, 
                templateDefinition: null, length: 0};

    // temporary length of child objects
    var tmpLength;

    // parse OUTERTEXT
    var t = scan(s, {OUTERTEXT: true});
    outer[t.token] = t.tokenvalue;  
    tmpLength = (t.tokenvalue) ? t.tokenvalue.length : 0;   
    if (tmpLength == 0) {
        // parse <template_Invocation>
        outer["templateInvocation"] = parseTemplateInvocation(s);
        tmpLength = getRecursiveLength(outer["templateInvocation"]);
        if (tmpLength == 0) {
            // parse <template_Definition>
            outer["templateDefinition"] = parseTemplateDefinition(s);
            tmpLength = getRecursiveLength(outer["templateDefinition"]);
        }
    }

    // update string length
    outer["length"] += tmpLength;
    s = s.substr(tmpLength);

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

    trimSpaces(TemplateDef["dtext"]);
    trimSpaces(TemplateDef["dparams"]);

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

    trimSpaces(TemplateInvo["itext"]);
    trimSpaces(TemplateInvo["targs"]);

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
    var text = {templateInvocation: null, templateDefinition: null, tparam: null, length: 0};
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
    if (tmpLength == 0) {
        // parse <template_Invocation>
        text["templateInvocation"] = parseTemplateInvocation(s);
        tmpLength = getRecursiveLength(text["templateInvocation"]);
        if (tmpLength == 0) {
            // parse <template_Definition>
            text["templateDefinition"] = parseTemplateDefinition(s);
            tmpLength = getRecursiveLength(text["templateDefinition"]);
            if (tmpLength == 0) {
                    // parse <Tparam>
                    text["tparam"] = parseTparam(s);
                    tmpLength = getRecursiveLength(text["tparam"]);
            }
        }
    }

    // update string length
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

// <tparams>
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

// recursively remove spaces at the beginnign and end of text
function trimSpaces(obj) {
    if (!obj) return;

    switch (obj["name"]) {
        case "dtext": 
            if (obj["INNERDTEXT"]) obj["INNERDTEXT"] = obj["INNERDTEXT"].replace(/^\s+|\s+$/g, "");
            break;
        case "itext": 
            if (obj["INNERTEXT"]) obj["INNERTEXT"] = obj["INNERTEXT"].replace(/^\s+|\s+$/g, "");
            break;
        case "dparams":
            trimSpaces(obj["dtext"]);
            break;
        case "targs":
            trimSpaces(obj["itext"]);
            break;
    }

    if (obj["next"]) trimSpaces(obj["next"]);
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
         var binding = lookup(ast.tparam.PNAME, env);
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

// NB in order to work with closures, we stringify the template definition and its environment
// this is achieved with the Professor's code to serialize the template

//////////// PROFESSOR'S CODE /////////////

// Convert a closure (template binding) into a serialized string.
// This is assumed to be an object with fields params, body, env.
function stringify(b) {
    // We'll need to keep track of all environments seen.  This
    // variable maps environment names to environments.
    var envs = {};
    // A function to gather all environments referenced.
    // to convert environment references into references to their
    // names.
    function collectEnvs(env) {
        // Record the env, unless we've already done so.
        if (envs[env.name])
            return;
        envs[env.name] = env;
        // Now go through the bindings and look for more env references.
        for (var b in env.bindings) {
            var c = env.bindings[b];
            if (c!==null && typeof(c)==="object") {
                if ("env" in c) {
                    collectEnvs(c.env);
                }
            }
        }
        if (env.parent!==null)
            collectEnvs(env.parent);
    }
    // Ok, first step gather all the environments.
    collectEnvs(b.env);
    // This is the actual structure we will serialize.
    var thunk = { envs:envs ,
                  binding:b
                };
    // And serialize it.  Here we use a feature of JSON.stringify, which lets us
    // examine the current key:value pair being serialized, and override the
    // value.  We do this to convert environment references to environment names,
    // in order to avoid circular references, which JSON.stringify cannot handle.
    var s = JSON.stringify(thunk,function(key,value) {
        if ((key=='env' || key=='parent') && typeof(value)==='object' && value!==null && ("name" in value)) {
            return value.name;
        }
        return value;
    });
    return s;
}

// Convert a serialized closure back into an appropriate structure.
function unstringify(s) {
    var envs;
    // A function to convert environment names back to objects (well, pointers).
    function restoreEnvs(env) {
        // Indicate that we're already restoring this environmnet.
        env.unrestored = false;
        // Fixup parent pointer.
        if (env.parent!==null && typeof(env.parent)==='number') {
            env.parent = envs[env.parent];
            // And if parent is unrestored, recursively restore it.
            if (env.parent.unrestored)
                restoreEnvs(env.parent);
        }
        // Now, go through all the bindings.
        for (var b in env.bindings) {
            var c = env.bindings[b];
            // If we have a template binding, with an unrestored env field
            if (c!==null && typeof(c)==='object' && c.env!==null && typeof(c.env)==='number') {
                // Restore the env pointer.
                c.env = envs[c.env];
                // And if that env is not restored, fix it too.
                if (c.env.unrestored)
                    restoreEnvs(c.env);
            }
        }
    }
    var thunk;
    try {
        thunk = JSON.parse(s);
        // Some validation that it is a thunk, and not random text.
        if (typeof(thunk)!=='object' ||
            !("binding" in thunk) ||
            !("envs" in thunk))
            return null;

        // Pull out our set of environments.
        envs = thunk.envs;
        // Mark them all as unrestored.
        for (var e in envs) {
            envs[e].unrestored = true;
        }
        // Now, recursively, fixup env pointers, starting from
        // the binding env.
        thunk.binding.env = envs[thunk.binding.env];
        restoreEnvs(thunk.binding.env);
        // And return the binding that started it all.
        return thunk.binding;
    } catch(e) {
        // A failure in unparsing it somehow.
        return null;
    }
}


//////////// TESTING ////////////

var s = "{:wrap|x|{{ {{{x}}} }}:} {{wrap|{:`|HEY:}}}"
var AST = parse(s)

console.log(JSON.stringify(AST,null,2))

var WML = evalWML(AST, createEnv(null))

console.log(WML)