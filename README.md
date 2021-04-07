# Bplus-tree

## Implementation of Bplus tree data structure for handling dictionary pairs.

### Problem Statement

    To create a m-way B+ tree data structure to store dictionary pairs given in the form (key , value).

### Program Execution

    • Navigate to directory where program is located

    • Run “make” command

    • Execute “java bplustree <input_file_with_extension>”

    • Output will be stored in output_file.txt 

 
 
### Program Flow:

    i) Input file name is taken by command line arguments and instantiating the file pointers.

    ii) Will read input from the input file one line at once and process command given the file
    until the cursors reaches EOF.

    iii) If it’s initialize(m) then create a B+ plus tree with an order of m.

    iv) If it’s insert(key, value) then the pair will be inserted into the B+ tree.

    v) If it’s search(key) returns the value associated with pair in the B+ tree.

    vi) If it’s search(low key, high key) returns the list of values for which the key lies in the B+
    tree and the key lies in the given range.

    vii) If the cursor reaches EOF end the program.
