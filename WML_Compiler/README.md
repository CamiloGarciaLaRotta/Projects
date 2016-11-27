# Projects
## WML Language compiler

[Basic Online Compiler](https://camilogarcialarotta.github.io/Projects/WML_Compiler/index.html) for a flavor of the WML language 


###Language:

The pseudo WML has the following major semantic characteristics:  

-  Template definitions, invocations and closures can be nested
-  Language is by default statically scoped, to turn into dynamically scoped please see line XXX in WML.js
-  Anything outside template declaration and invocation is treated as plain HTML text
-  Expression and conditional templates (starting by #) don't require declaration before invocation
-  `#if` returns true if the string inputted is empty
-  `#ifeq` returns true if the 2 input strings are equal
-  `#expr`calls JavaScript's eval() on whatever was passed as an argument

|  __Action__              	|  __Syntax__           |  
|:---:	                    |:---:	                |
|   Template Definition	|   {: T_name \| T_param1 \| T_param2 \| ... \| T_paramN \| T_body  :}	|  
|   Template Invocation	|   {{ T_name \| T_arg1 \| T_arg2 \| ... \| T_argN \| T_body  }	|   
|   Calling a parameter inside the body	|   {{{ T_paramN }}}	|  
|   Template closure (can be anonymous)	|   {:\`T_name \| parameters \| body:}	|  
|   Conditional templates	|   {{#if \| condition \| then \| else }}  {{#ifeq \| A \| B \| then \| else }} 	|
|   Expression templates  |   {{#expr \| expression }} 	|
