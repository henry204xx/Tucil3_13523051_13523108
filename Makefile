# Directories
SRC_DIR := src
BIN_DIR := bin

# All Java source files
SOURCES := $(shell find $(SRC_DIR) -name "*.java")

# Main class (adjust package if needed)
MAIN := Animator

# Compile all sources at once
all:
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SOURCES)

# Run the program
run:all
	java -cp $(BIN_DIR) $(MAIN)

# Clean compiled files
clean:
	rm -rf $(BIN_DIR)/*
