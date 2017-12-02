class Node
    attr_accessor :number, :addition, :multiplication

    def initialize(number, addition = nil, multiplication = nil)
        @number = number
        @addition = addition
        @multiplication = multiplication
    end

    # because multiplication has precedence over addition,
    # we will traverse the multiplication branches dfs then the additions branches
    def self.recursive_evaluate(node)
        result = node.number
        result *= recursive_evaluate(node.multiplication) if node.multiplication
        result += recursive_evaluate(node.addition) if node.addition

        return result
    end
end

#       5
#      /
#     4
#    / \
#   2   3 
#
# 5 + 4 * 3 + 2
Two = Node.new(2)
Three = Node.new(3)
Four = Node.new(4,Two,Three)
Five = Node.new(5,Four)

prompt = "The evaluation at node %s equals %d \n"
printf(prompt, 'Two', Node.recursive_evaluate(Two))
printf(prompt, 'Four', Node.recursive_evaluate(Four))
printf(prompt, 'Five', Node.recursive_evaluate(Five))




