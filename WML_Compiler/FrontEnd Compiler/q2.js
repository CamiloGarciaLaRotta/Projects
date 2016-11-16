// CAMILO GARCIA 
// ID# 260657037
// q2

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
