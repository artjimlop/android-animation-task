android-animation-task
======================

Task made in order to learn the use of the animation API

Properties:

* The view consists of three coloured squares, randomly positioned; a black circle which is fixed in the bottom left and two buttons: “Add Shape” and “Suck In”.
* When the “Add Square” button is pressed you should add another square of random colour at a random position in the scene.
* A user should be able to reposition a square by dragging it to a new position.
* If a user finishes a drag with a square overlapping the black circle it should be “sucked in” to the circle and leave the scene.
* When the black hole button is pressed all squares should be “sucked in” to the centre point of the black circle and ultimately leave the scene.
* The “suck in” animation is at your discretion, but decreasing the size of the square while pulling it into the centre of the circle gives a good effect.
* A user can “fling” a square.
* A user can rotate a square with a rotation gesture.
* A user can resize a square with a pinch gesture.
* A user can double tap a square and it’s colour will change to another random colour.
* An initial layout of squares can be built from json (this is fine to just load from the assets directory of the project) in the form:
{‘squares’: [{‘x’: 120, ‘y’: 230, ‘colour’: #FF0000, ‘size’: 250}, {‘x’:56, ‘y’: 100, ‘colour’: #00FF00, ‘size’: 100}]}

