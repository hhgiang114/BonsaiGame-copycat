# Software Practice Project 2 (Group project)
 Reimplementation of a boardgame Bonsai as a Kotlin application using [BoardGameWork](https://github.com/tudo-aqua/bgw).


## Rules 
Based on the original board game: https://www.dvgiochi.com/giochi/bonsai/download/Bonsai_ENG-Rules_WEB.pdf
Only multi-player play needed. 

## Program Requirements
The program being developed should control the game flow and ensure compliance with the game rules. Additional features not directly related to the game rules should be implemented:

* The order of players should be freely configurable and randomized before the game starts.
* The target tiles should be freely configurable before the game starts.
* The game should be playable in two independent modes (a mixture of the two modes is not intended):
* Players select their actions one after the other on the same screen (hotseat mode).
* Players play against each other via a network using the BGW-Net module.
* A game should be able to be paused and saved to continue at a later time (even after restarting the program). This feature should be disabled for network games.
* Simulated players ("bots") should be available. There should be a simple test bot that, for example, randomly selects one of the possible moves, as well as a "real" one for the bot tournament that takes place at the end. It should also be possible to play purely bot games (i.e., without a human player). To allow viewers to watch and follow the moves, the simulation speed should be adjustable.
Bots are not allowed to exploit any advantages over a human player during play.
No bot move should take longer than 10 seconds.
* To allow for studying and testing different strategies, the game should have an undo and redo function. Moves should be reversible up until the game starts. This feature should be disabled for network games.

## Group Work (8 members)
* During the course of 3 week, group meetings and the practical work take place daily. 
* Procedure of our group project using the Waterfall Model with the phases of requirement analysis, design, implementation (coding), and testing, deployment


