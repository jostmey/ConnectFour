sudo apt-get install openjdk-7-jdk

javac *.java -d bin/ 

appletviewer Connect4.html

Uses something like minimax algorithm. Searches for "traps" where no matter what move you make the computer can still win. Computer always makes move where it finds the highest density of traps it can set while trying to avoid any that you set for it.
