# SplitTimer
Barebones timer with global hotkeys and configurable splits.
## Build and Run
```
mvn clean package
java -jar shade/SplitTimer.jar
```
## Instructions
1. Load a JSON configuration file of the following format
```json
[
  {"name":  "SPLIT 1"},  
  {"name":  "SPLIT 2"},  
  {"name":  "SPLIT 3"},  
  {"name":  "SPLIT 4"}  
]
```

2. Keys
  - Spacebar: start and split
  - R: stop and reset