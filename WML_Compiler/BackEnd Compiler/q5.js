// CAMILO GARCIA 
// ID# 260657037
// q5


// demanded helper methods

// next churchill #
var succ = "{:succ|n|f|x|{{ {{{f}}}|{{ {{{n}}}|{{{f}}}|{{{x}}} }} }} :}"

// is churchill # == 0
var isZero = "{:isZero|n|{{ {{{n}}}|{:`|x|FALSE:}|TRUE }} :}"

// add 2 churchill # 
var plus = "{:plus|m|n|f|x| {{ {{{n}}}|{{{f}}}|{{ {{{m}}}|{{{f}}}|{{{x}}} }} }} :}"

// given an integer n, return churchill # n
var makeN = "{:makeN|n|{:`|f|x|{{makeN_helper| {{{n}}} | {:`|f|x|{{{x}}}:} | {{{f}}} | {{{x}}} }} :} :}"
var makeN_helper = "{:makeN_helper|n|LambdaNum|f|x|{{#ifeq|{{{n}}}|0|{{{LambdaNum}}}|{{ makeN_helper|{{dec|{{{n}}} }}|{{succ|{{{LambdaNum}}}|{{{f}}}|{{{x}}} }}  }} }} :}"


// helper methods to solve prefix sum

// decrement an integer by 1
var dec = "{:dec|n|{{#expr|{{{n}}}-1}}:}"

// increment an integer y 1
var inc = "{:inc|x|{{#expr|{{{x}}}+1}}:}"

// zero churchill #
var zero =  "{:`|f|x|{{{x}}}:}"

var prefix_sum = "{:prefix_sum|n| {{helper_prefix|{{{n}}}|{{zero}}}} :}"
var helper_prefix = "{:helper_prefix|n|LambdaNum|{{#ifeq|n|0|{{{result}}}|{{#expr|{{{result}}}+{{helper_prefix|{{dec|{{{n}}} }}|{{succ|{{{LambdaNum}}} }} }} }} }} :}"