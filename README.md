# Converting a binary tree in place to an in-order doubly-linked list
I recently interviewed with a company that asked me this question in a coding interview. I had gotten the basic algorithm down but ran out of time to iron out the details, so I thought I'd iron those out here. (I did not get an offer.)

The question is basically the title: given the head of a binary tree roughly defined as:

~~~
class Node {
    Node left
    Node right
    int data
}
~~~

Convert the tree in-place to a doubly-linked list that represents its in-order traversal.

For example, given the tree:

        3
       /
      1
       \
        2

The linked list should look like:

    1 <=> 2 <=> 3

## Solution
My solution to this is the roughly 20 lines of code inside the method `convertToLinkedList()`. I documented how it works in detail in the code, but the basic algorithm is that it will recursively convert the current Node's left and right subtrees to doubly-linked lists and then insert itself between the tail of the left list and the head of the right list. The method returns a "meta" node that points to the resulting list's head and tail nodes via its `left` and `right` members respectively. Check out the code for details.

## Supporting code
Most of my time on this was spent trying to get the supporting code to work to test out the `convertToLinkedList()` method. The first thing I did was write the constructor that parses a string representation of a tree so that I could easily write different test cases without manually building the tree. As it turns out, parsing a string in the format of $DATA($LEFT)($RIGHT) to build a tree is another popular interview question. The methods involved with that are pretty well documented in the code.

Next I added some helper methods for debugging: `inOrderString()` and `linkedListString()` and overrode the `toString()` method. I then added a simple `insert()` method to facilitate generating random trees. Finally, I was going to use `maxDepth()` to see if I could programmatically create a nice tree output like I have above. I ended up giving up on that, but I kept the method in because why not? 

If I really wanted to pretty it up, I would turn the steps inside the `main()` method into unit tests, but I feel like that's a little overkill just to work out the details of an interesting interview question. I may get around to that to freshen up on how to crank those out quickly, though.

## Conclusion
In the end, even though I didn't do so well on this problem in the crunch of an interview, I feel like it's a pretty good question to ask. There are a couple different ways to go about this and the ideal solution can be worked out intuitively in my opinion.

I think I'll probably do a couple more of these for other interesting problems I get asked, mostly for my own benefit. I'll probably keep these repositories private while I'm actively interviewing and make them public when I'm not. 