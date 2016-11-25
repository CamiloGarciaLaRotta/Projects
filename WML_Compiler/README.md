# Projects
## WML Language compiler

Back and Front end part of a basic compiler for WML language with certain additions  


###Language:

The pseudo WML has the following major semantic characteristics:  

|  __Token__              	|  __Symbol__           |  
|:---:	                    |:---:	                |
|   Anything outside template declaration and invocation is treated as plain HTML text	|   ---	|   
|   Template Definition	|   {: T_name \| T_param1 \| T_param2 \| ... \| T_paramN \| T_body  :}	|  
|   Template Invocation	|   {{ T_name \| T_arg1 \| T_arg2 \| ... \| T_argN \| T_body  }	|   
|   Calling a parameter inside the body	|   {{{ T_paramN }}}	|  
|   Template closure	|   Anonymous : {:`\| parameters \| body :} Named : {:`T_name \| parameters \| body:}	|  
