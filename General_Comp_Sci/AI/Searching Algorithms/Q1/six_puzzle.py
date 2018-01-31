"""
ECSE 424 Artificial Intelligence - Assignment #1
Camilo Garcia La Rotta  #260657037
"""
import copy       # for 2D list cloning
import itertools  # tie breaker for whenever 2 priorities are equal because dicts cant be ordered
import bisect     # insert into sorted list
from queue import PriorityQueue

"""
given action = [(1,2),(0,2)] means:
switch the EMPTY tile at (1,2) with the tile at (0,2)
if the switching is valid return the new state
if the switching is invalid return False
VALID: vertical/horizontal && adjacent && first parameter is empty tile
INVALID: diagonal || non adjacent || first parameter is not empty tile
"""

##########################################################################
# Classes
##########################################################################
class Problem:
    """Contains the definition of the step costs and the goal"""
    def __init__(self, goal, step_cost=1):
        self.goal_state = goal
        self.step_cost = step_cost

    def is_goal_state(self, state):
        """check if the input state is the goal state of the problem"""
        return state == self.goal_state

    def get_step_cost(self, state, action):
        """calculate the cost of applying an action to a given state"""
        return self.step_cost

##########################################################################
# Search Algorithms
##########################################################################
def bfs(prob, initial_node):
    """The only weight in the priority queue is the number of the tile"""
    if prob.is_goal_state(initial_node):
        return prob.gen_solution(initial_node)

    counter = itertools.count()
    frontier = PriorityQueue()
    frontier.put((0, next(counter), initial_node))
    explored = []

    while frontier:
        n = frontier.get()[2]
        explored.append(n['state'])

        for action in get_actions(n['state']):
            child_n = gen_child_node(prob, n, action)
            if (child_n['state'] not in explored and
                    not Q_contains_state(child_n['state'], frontier.queue)):
                if prob.is_goal_state(child_n['state']):
                    return gen_solution(child_n)

                # add child state to the priority Q
                # priority given by: cost of action and moving lower numbers
                row, col = action[0]
                priority = child_n['state'][row][col]

                frontier.put((priority, next(counter), child_n))

    # if we reach here, then no solution was found
    return False

def ucs(prob, initial_node):
    """Priority Queue weight = number of tile to move + path cost"""
    if prob.is_goal_state(initial_node):
        return prob.gen_solution(initial_node)

    counter = itertools.count()
    frontier = PriorityQueue()
    frontier.put((0, next(counter), initial_node))
    explored = []

    while frontier:
        n = frontier.get()[2]
        explored.append(n['state'])

        for action in get_actions(n['state']):
            child_n = gen_child_node(prob, n, action)
            if (child_n['state'] not in explored and
                    not Q_contains_state(child_n['state'], frontier.queue)):
                if prob.is_goal_state(child_n['state']):
                    return gen_solution(child_n)

                # add child state to the priority Q
                # priority given by: cost of action and moving lower numbers
                row, col = action[0]
                priority = child_n['state'][row][col] + child_n['path_cost']

                frontier.put((priority, next(counter), child_n))
            else:
                # choose the frontier duplicate state with lowest cost
                if child_n['state'] not in explored:
                    # meaning duplicate is in frontier
                    frontier = replace_with_min(child_n, frontier)

    # if we reach here, then no solution was found
    return False

def dfs(prob, initial_node):
    """The only weight in the priority queue is the number of the tile"""
    if prob.is_goal_state(initial_node):
        return prob.gen_solution(initial_node)

    frontier = [initial_node]
    explored = []

    while frontier:
        n = frontier.pop()
        explored.append(n['state'])

        # temporary frontier for each expanded node 
        # nodes here will be sorted by increasing tile number
        # so as to ensure ties with lower numbers are expanded first
        tmp_frontier = []

        for action in get_actions(n['state']):
            child_n = gen_child_node(prob, n, action)
            if (child_n['state'] not in explored and
                    not contains_state(child_n['state'], frontier)):
                if prob.is_goal_state(child_n['state']):
                    return gen_solution(child_n)

                row, col = action[0]
                priority = child_n['state'][row][col]
                tmp_frontier.append((child_n, priority))

        tmp_frontier.sort(key=lambda n: n[1])
        frontier.extend([n[0] for n in tmp_frontier])

    # if we reach here, then no solution was found
    return False

def recursive_ids(prob, node, max_depth):
    '''Helper function for ids'''
    if prob.is_goal_state(node): return prob.gen_solution(node)
        
    frontier   = [(node,0)]
    explored   = []
    curr_depth = 0

    while frontier:
        n, d = frontier.pop()
        explored.append(n['state'])

        if d >= max_depth: continue

        for action in get_actions(n['state']):
            child_n = gen_child_node(prob, n, action)
            if (child_n['state'] not in explored and
                    not contains_state(child_n['state'], [f[0] for f in frontier])):
                if prob.is_goal_state(child_n['state']):
                    gen_solution(child_n)
                    return True

                frontier.append((child_n, d+1))

    # if we reach here, then no solution was found
    return False

def ids(prob, initial_node, max_depth):
    '''Iterative deepening search'''
    for depth in range(max_depth):
        if recursive_ids(prob, initial_node, depth):
            print('Solution found at depth {}'.format(depth))
            return
    print('No solution found until depth {}'.format(max_depth))
    return

##########################################################################
# Helper Methods
##########################################################################
def replace_with_min(node, Q):
    """replace in Q if it exists a node with the same state but higher path_cost"""
    new_Q = PriorityQueue()
    for priority, counter, n in Q.queue:
        if node['state'] == n['state']:
            if node['path_cost'] < n['path_cost']:
                n = node

        new_Q.put((priority, counter, n))

    return new_Q

def Q_contains_state(state, Q):
    """does a Q contain a node with the same state as the input state"""
    return any(n for n in Q if n[2]['state'] == state)

def contains_state(state, l):
    """does a list contain a node with the same state as the input state"""
    return any(n for n in l if n['state'] == state)

def get_index(matrix, value):
    """given an item, return the index in a 2D matrix"""
    for i, x in enumerate(matrix):
        for _ in x:
            if value in x:
                return (i, x.index(value))

def get_zero_idx(state):
    """wrapper method to return the idx of the empty tile"""
    return get_index(state, 0)

def get_actions(state):
    """given a state generate all valid actions possible"""
    row_t0, col_t0 = get_zero_idx(state)
    zero_idx = (row_t0, col_t0)
    actions = []

    # tile can always move up/down
    actions.append([zero_idx, ((row_t0 + 1) % 2, col_t0)])

    # depending on where it is it can move left/right
    if col_t0 != 0:
        actions.append([zero_idx, (row_t0, col_t0 - 1)])
    if col_t0 != 2:
        actions.append([zero_idx, (row_t0, col_t0 + 1)])

    return actions

def gen_child_node(prob, parent, action):
    """return the child node resulting from the application of an action on a parent state"""
    return {
        'state': apply(parent['state'], action),
        'parent': parent,
        'action': action,
        'path_cost': (parent['path_cost'] + prob.get_step_cost(parent['state'], action))
    }

def gen_solution(node):
    """given a node, generate sequence of nodes (actions) that lead to that node"""
    steps = []
    while node['parent']:
        steps.append(node['state'])
        node = node['parent']

    # append initial state
    steps.append(node['state'])
    steps.reverse()
    print('Solution found in {} steps'.format(len(steps)))
    for state in steps:
        print_state(state)
        print()

def apply(state, action):
    """return a new state resulting from applying action to state"""
    new_state = copy.deepcopy(state)

    row_t1, col_t1 = action[0]
    row_t2, col_t2 = action[1]

    t1 = new_state[row_t1][col_t1]
    new_state[row_t1][col_t1] = new_state[row_t2][col_t2]
    new_state[row_t2][col_t2] = t1

    return new_state

def print_state(state):
    """pretty print gameboard"""
    for row in state:
        print('{}'.format(row))

##########################################################################
# Main
##########################################################################
def main():
    """Find path to goal state through multiple search algorithms"""
    goal_state = [[0, 1, 2], [5, 4, 3]]

    initial_node = {
        'state': [[1, 4, 2], [5, 3, 0]],
        'parent': None,
        'action': None,
        'path_cost': 0
    }

    prob = Problem(goal_state)

    print('\nBFS:')
    bfs(prob, initial_node)
    print('\nUCS:')
    ucs(prob, initial_node)
    print('\nDFS:')
    dfs(prob, initial_node)
    print('\nIDS:')
    ids(prob, initial_node, 10)

if __name__ == '__main__':
    main()
