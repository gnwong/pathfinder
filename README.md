pathfinder
==========

Implementation of naive (but computationally efficient) pathfinding scheme in Java. 

------

##Use

A Makefile has been included in the repository. Use `make` to make the project, `make run` to run the project (after it has been made), `make jar` to compile and package the project in an executable .jar file, or `make clean` to remove all compiled files.

------

The model makes use of a `world` object which is represented by 2 dimensional byte map. Each byte is assigned a different terrain type, with each terrain type having different properties. For the sake of convenience, public boolean methods exist to query parameters for use in the pathfinding algorithm. The world object also has constructor methods which randomize terrain (if desired) by making use of the "brush" schema. 

The `path` object is created with endpoint (beginning / end) information. Methods exist to change the start and end points of the path, in addition to calculating an efficient path given the current world state. The trace method (calculating a path) may be called at any time.

Within the pass definition, the `ipos` object is defined. ipos acts as a 2-d coordinate point and has relevant / useful methods defined as expected.

