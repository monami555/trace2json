# trace2json

Converts trace logs to JSON trees.

# Building
 
You need to have Maven installed. Build the project using:

```
mvn clean install
```

The jar will be in the /target folder.

# Running

Run:

```
java -jar trace2json.jar inputFile outputFile
```

Use `STDIN` or `STDOUT` to read from or print to stdout, e.g.:

```
java -jar trace2json.jar STDIN STDOUT
```
