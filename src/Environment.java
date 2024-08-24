package src;

import java.util.HashMap;
import java.util.Map;

// Environment class for managing variable bindings
public class Environment {
  private Environment parent; // Reference to the parent environment
  private Map<String, ASTNode> nameValues; // Map for storing variable bindings

  // Constructor to initialize the environment
  public Environment() {
    nameValues = new HashMap<String, ASTNode>(); // Initialize the map
  }

  // Setter for setting the parent environment
  public void setParent(Environment parent) {
    this.parent = parent;
  }

  // Getter for getting the parent environment
  public Environment getParent() {
    return parent;
  }

  // Method to look up a variable binding in the environment
  public ASTNode lookup(String key) {
    ASTNode returnVal = null;
    Map<String, ASTNode> map = nameValues; // Get the map of variable bindings

    returnVal = map.get(key); // Look up the key in the map

    // If the key is found, create a copy of the value using the Copier visitor
    if (returnVal != null)
      return returnVal.accept(new Copier());

    // If the key is not found in this environment, recursively search in the parent environment
    if (parent != null)
      return parent.lookup(key);
    else
      return null; // Return null if the key is not found in any environment
  }

  // Method to add a new variable binding to the environment
  public void addMapping(String key, ASTNode value) {
    nameValues.put(key, value); // Add the key-value pair to the map
  }
}
