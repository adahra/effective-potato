# effective-potato
## [Wave function collapse](https://en.wikipedia.org/wiki/Wave_function_collapse#Use_in_procedural_generation)

### What does it do:
In short, it takes in a 2D array of integer values (Could be colors, tiles or whatever you want),
goes over every possible 3x3 patterns of values represented in the input array and stores the possibilities
in a collection of unique patterns.

Then from that collection, outputs a new 2D array of integers that adheres to the same rules of patterns. 
That's it. A relatively simple input can generate complex outputs. There are many use cases for WFC in procedural generation.

[Inspiration](https://github.com/heathensoft/WaveFunctionCollapse)
