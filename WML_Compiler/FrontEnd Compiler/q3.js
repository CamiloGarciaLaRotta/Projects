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
