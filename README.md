## Info

`Client.java`: Main program, it's the client program, this is what all players are running

`Server.java`: Server program, ran separately from everything, players connect to it.

All other files: Custom classes.

To compile, use `mvn package`. This will create `smash-java-0.0.0-client.jar` and `smash-java-0.0.0-server.jar`. You can also just run the `build.sh` script.

You can also use the `run.sh` script to run both client and server simultaneously for testing.

Otherwise you can just run the JARs or use the VSCODE play button.

## Coding conventions
- Go into VSCode settings and set TABs as 2 SPACES
  - Use spaces and not tabs, this is important.
- Follow Java conventions (listen to what the java extentions have to say)
- Preferably put custom classes in their own files.
- Janky code (for example that doesnt follow these conventions) is totally fine, but please promise to clean it up after lol
- Please document code. Commenting is great, but documenting is significantly more important. Write a doc comment on top of every function preferably.


## Architecture, ideas, etc

### Idea 1
- Every time the player does something, the server is informed. (Send an Action packet)
- The server then returns the new game state. (does all calculations, including movements and attacks)
- The client renders it.
### Idea 2
- Server only synchronises a few things, nearly all game logic is handled by clients.