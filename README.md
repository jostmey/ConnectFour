## Description

Play [Connect Four](https://en.wikipedia.org/wiki/Connect_Four) against the computer using this Java Applet. Right click on the mouse to drop a piece into the highlighted column. After watching your piece fall the computer will make its move. The difficulty setting can be adjusted to either easy, medium, or hard settings.

## Download

* Download: [zip](https://github.com/jostmey/ConnectFour/zipball/master)
* Git: `git clone https://github.com/jostmey/ConnectFour`

## Compile

Make sure a Java Developer Kit has first been installed such as Open JDK. Installation instructions for OpenJDK may be found [here](http://openjdk.java.net/install/) but be sure to install the **developer package**, not the runtime environment. The program has been tested on OpenJDK 7 but is expected to work on more recent versions.

To compile the code, set this as your working directory and run the following command.

`javac GameEngine.java InterfaceEngine.java`

## RUN

If your browser supports java applets then you may run the program simply by opening the filter `Connect4.html` in your browser. Alternatively, you may run the program from the command line. Set this as your working directory and run the following command.

`appletviewer Connect4.html`

## How It Works

The program searches every possible move several turns into the future looking for "traps". A trap is a condition in the game such that no matter what you do the computer can still win. The program will pick the move that offers the greatest possibility of laying a trap to catch you while it tries to avoid any traps you set for it. As the program searches for its best move, the algorithm used to score each move assumes that you will pick the optimal move. In this respect, the underlying algorithm resembles the [minimax alrogithm](https://en.wikipedia.org/wiki/Minimax).
