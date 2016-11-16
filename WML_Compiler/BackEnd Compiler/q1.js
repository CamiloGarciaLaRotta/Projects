// CAMILO GARCIA 
// ID# 260657037
// q1

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
