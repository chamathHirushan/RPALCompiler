package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// Copier class for creating deep copies of AST nodes
public class Copier {

  // Gives a copy of a Beta node
  public Beta copy(Beta beta) {
    Beta copy = new Beta();
    // Copying child and sibling nodes recursively
    if (beta.getChild() != null)
      copy.setChild(beta.getChild().accept(this));
    if (beta.getSibling() != null)
      copy.setSibling(beta.getSibling().accept(this));
    copy.setType(beta.getType());
    copy.setValue(beta.getValue());

    // Copying then-part
    Stack<ASTNode> thenBodyCopy = new Stack<ASTNode>();
    for (ASTNode thenBodyElement : beta.getThenPart()) {
      thenBodyCopy.add(thenBodyElement.accept(this));
    }
    copy.setThenPart(thenBodyCopy);

    // Copying else-part
    Stack<ASTNode> elseBodyCopy = new Stack<ASTNode>();
    for (ASTNode elseBodyElement : beta.getElsePart()) {
      elseBodyCopy.add(elseBodyElement.accept(this));
    }
    copy.setElsePart(elseBodyCopy);

    return copy;
  }

  // Gives a copy of a Delta node
  public Delta copy(Delta delta) {
    Delta copy = new Delta();
    // Copying child and sibling nodes recursively
    if (delta.getChild() != null)
      copy.setChild(delta.getChild().accept(this));
    if (delta.getSibling() != null)
      copy.setSibling(delta.getSibling().accept(this));
    copy.setType(delta.getType());
    copy.setValue(delta.getValue());
    copy.setIndex(delta.getIndex());

    // Copying body elements
    Stack<ASTNode> bodyCopy = new Stack<ASTNode>();
    for (ASTNode bodyElement : delta.getBody()) {
      bodyCopy.add(bodyElement.accept(this));
    }
    copy.setBody(bodyCopy);

    // Copying bound variables list
    List<String> boundVarsCopy = new ArrayList<String>();
    boundVarsCopy.addAll(delta.getBoundVars());
    copy.setBoundVars(boundVarsCopy);

    // Copying linked environment
    copy.setLinkedEnvironment(delta.getLinkedEnvironment());

    return copy;
  }

  // Gives a copy of an ASTNode
  public ASTNode copy(ASTNode astNode) {
    ASTNode copy = new ASTNode();
    // Copying child and sibling nodes recursively
    if (astNode.getChild() != null)
      copy.setChild(astNode.getChild().accept(this));
    if (astNode.getSibling() != null)
      copy.setSibling(astNode.getSibling().accept(this));
    copy.setType(astNode.getType());
    copy.setValue(astNode.getValue());
    return copy;
  }

  // Gives a copy of an Eta node
  public Eta copy(Eta eta) {
    Eta copy = new Eta();
    // Copying child and sibling nodes recursively
    if (eta.getChild() != null)
      copy.setChild(eta.getChild().accept(this));
    if (eta.getSibling() != null)
      copy.setSibling(eta.getSibling().accept(this));
    copy.setType(eta.getType());
    copy.setValue(eta.getValue());

    // Copying associated Delta node
    copy.setDelta(eta.getDelta().accept(this));

    return copy;
  }

  // Gives a copy of a Tuple node
  public Tuple copy(Tuple tuple) {
    Tuple copy = new Tuple();
    // Copying child and sibling nodes recursively
    if (tuple.getChild() != null)
      copy.setChild(tuple.getChild().accept(this));
    if (tuple.getSibling() != null)
      copy.setSibling(tuple.getSibling().accept(this));
    copy.setType(tuple.getType());
    copy.setValue(tuple.getValue());
    return copy;
  }
}

// Class representing Beta nodes used for evaluating conditionals
class Beta extends ASTNode {
  private Stack<ASTNode> thenPart;
  private Stack<ASTNode> elsePart;

  public Beta() {
    setType(ASTNodeType.BETA);
    thenPart = new Stack<ASTNode>();
    elsePart = new Stack<ASTNode>();
  }

  public Stack<ASTNode> getThenPart() {
    return thenPart;
  }

  public Stack<ASTNode> getElsePart() {
    return elsePart;
  }

  public void setThenPart(Stack<ASTNode> thenPart) {
    this.thenPart = thenPart;
  }

  public void setElsePart(Stack<ASTNode> elsePart) {
    this.elsePart = elsePart;
  }

  // Accept method for visitor pattern
  public Beta accept(Copier copier) {
    return copier.copy(this);
  }
}

// Class representing Eta nodes
class Eta extends ASTNode {
  private Delta delta;

  public Eta() {
    setType(ASTNodeType.ETA);
  }

  // Overridden method to provide value information
  @Override
  public String getValue() {
    return "[eta closure: " + delta.getBoundVars().get(0) + ": " + delta.getIndex() + "]";
  }

  public Delta getDelta() {
    return delta;
  }

  public void setDelta(Delta delta) {
    this.delta = delta;
  }

  // Accept method for visitor pattern
  public Eta accept(Copier copier) {
    return copier.copy(this);
  }
}

// Class representing Tuple nodes
class Tuple extends ASTNode {

  public Tuple() {
    setType(ASTNodeType.TUPLE);
  }

  // Overridden method to provide value information
  @Override
  public String getValue() {
    ASTNode childNode = getChild();
    if (childNode == null)
      return "nil";

    String printValue = "(";
    while (childNode.getSibling() != null) {
      printValue += childNode.getValue() + ", ";
      childNode = childNode.getSibling();
    }
    printValue += childNode.getValue() + ")";
    return printValue;
  }

  // Accept method for visitor pattern
  public Tuple accept(Copier copier) {
    return copier.copy(this);
  }
}
