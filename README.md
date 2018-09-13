# Autocomplete
A Data Structures and Algorithms (CS201) assignment that utilizes a user's search query to return suggestions for words or phrases based on weight/likelihood.

- The Term class is a class that encapsulates a word/term and its corresponding weight, and includes several comparators to allow sorting in custom orders.
- BinarySearchAutocomplete utilizes binary search on a sorted array of Terms to find Terms with a given prefixes.
- TrieAutocomplete is a more efficient autocomplete algorithm that finds Terms with a given prefix by building a trie to store Terms.
