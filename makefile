JC = javac

# Directories
MAIN_DIR = .
OUTPUT_DIR = bin
SRC_DIR = src
PARSER_DIR = parser
SCANNER_DIR = scanner

JAVA_FILES := $(wildcard $(MAIN_DIR)/*.java) \
              $(wildcard $(SRC_DIR)/*.java)


OBJ_FILES := $(patsubst %.java, $(OUTPUT_DIR)/%.class, $(JAVA_FILES))

all: $(OBJ_FILES)

# Compile
$(OUTPUT_DIR)/%.class: %.java
	$(JC) -d $(OUTPUT_DIR) $<

#run
run:
	java -cp $(OUTPUT_DIR) myrpal test_cases/$(file)

# print ast
ast:
	java -cp $(OUTPUT_DIR) myrpal test_cases/$(file) -ast

# print st
st:
	java -cp $(OUTPUT_DIR) myrpal test_cases/$(file) -st

# Clean build
clean:
	rm -rf $(OUTPUT_DIR)

.PHONY: all clean