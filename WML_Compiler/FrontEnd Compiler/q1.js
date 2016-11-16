// CAMILO GARCIA 
// ID# 260657037
// q1

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