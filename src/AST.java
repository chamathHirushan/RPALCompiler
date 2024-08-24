package src;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// AST class representing an Abstract Syntax Tree
public class AST {
  private ASTNode root; // Root node of the AST
  private Delta currentDelta; // Current delta
  private Delta rootDelta; // Root delta
  private int deltaIndex; // Index for deltas
  private boolean standardized; // Indicates whether the AST is standardized
  private ArrayDeque<PendingDelta> pendingDeltaQueue; // Queue for pending deltas

  // Constructor
  public AST(ASTNode node) {
    this.root = node; // Initialize root node
  }

  // Method to standardize the AST
  public void standardize() {
    standardize(root); // Call the standardize method starting from the root
    standardized = true; // Set standardized flag to true
  }

  // Recursive method to standardize the AST starting from a given node
  private void standardize(ASTNode node) {
    // Standardizing child nodes
    if (node.getChild() != null) {
      ASTNode childNode = node.getChild();
      while (childNode != null) {
        standardize(childNode);
        childNode = childNode.getSibling();
      }
    }

    // Standardizing the current node based on its type
    switch (node.getType()) {
      // Standardizing LET node
      case LET:
        // Extract components of LET node
        ASTNode equalNode = node.getChild();
        if (equalNode.getType() != ASTNodeType.EQUAL)
          throw new StandardizeException("LET/WHERE: left child is not EQUAL");
        ASTNode e = equalNode.getChild().getSibling();
        equalNode.getChild().setSibling(equalNode.getSibling());
        equalNode.setSibling(e);
        equalNode.setType(ASTNodeType.LAMBDA);
        node.setType(ASTNodeType.GAMMA);
        break;

      // Standardizing WHERE node
      case WHERE:
        equalNode = node.getChild().getSibling();
        node.getChild().setSibling(null);
        equalNode.setSibling(node.getChild());
        node.setChild(equalNode);
        node.setType(ASTNodeType.LET);
        standardize(node);
        break;

      // standardizing fcnform
      case FCNFORM:
        ASTNode childSibling = node.getChild().getSibling();
        node.getChild().setSibling(constructLambdaChain(childSibling));
        node.setType(ASTNodeType.EQUAL);
        break;

      // standardizing at
      case AT:
        ASTNode e1 = node.getChild();
        ASTNode n = e1.getSibling();
        ASTNode e2 = n.getSibling();
        ASTNode gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(n);
        n.setSibling(e1);
        e1.setSibling(null);
        gammaNode.setSibling(e2);
        node.setChild(gammaNode);
        node.setType(ASTNodeType.GAMMA);
        break;

      // standardizing within
      case WITHIN:
        if (node.getChild().getType() != ASTNodeType.EQUAL
            || node.getChild().getSibling().getType() != ASTNodeType.EQUAL)
          throw new StandardizeException("WITHIN: one of the children is not EQUAL");
        ASTNode x1 = node.getChild().getChild();
        e1 = x1.getSibling();
        ASTNode x2 = node.getChild().getSibling().getChild();
        e2 = x2.getSibling();
        ASTNode lambdaNode = new ASTNode();
        lambdaNode.setType(ASTNodeType.LAMBDA);
        x1.setSibling(e2);
        lambdaNode.setChild(x1);
        lambdaNode.setSibling(e1);
        gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(lambdaNode);
        x2.setSibling(gammaNode);
        node.setChild(x2);
        node.setType(ASTNodeType.EQUAL);
        break;

      // standardizing simultaneous definitions
      case SIMULTDEF:
        ASTNode commaNode = new ASTNode();
        commaNode.setType(ASTNodeType.COMMA);
        ASTNode tauNode = new ASTNode();
        tauNode.setType(ASTNodeType.TAU);
        ASTNode childNode = node.getChild();
        while (childNode != null) {
          processCommaAndTau(childNode, commaNode, tauNode);
          childNode = childNode.getSibling();
        }
        commaNode.setSibling(tauNode);
        node.setChild(commaNode);
        node.setType(ASTNodeType.EQUAL);
        break;

      // standardizing rec
      case REC:
        childNode = node.getChild();
        if (childNode.getType() != ASTNodeType.EQUAL)
          throw new StandardizeException("REC: child is not EQUAL");
        ASTNode x = childNode.getChild();
        lambdaNode = new ASTNode();
        lambdaNode.setType(ASTNodeType.LAMBDA);
        lambdaNode.setChild(x);
        ASTNode yStarNode = new ASTNode();
        yStarNode.setType(ASTNodeType.YSTAR);
        yStarNode.setSibling(lambdaNode);
        gammaNode = new ASTNode();
        gammaNode.setType(ASTNodeType.GAMMA);
        gammaNode.setChild(yStarNode);
        ASTNode xWithSiblingGamma = new ASTNode();
        xWithSiblingGamma.setChild(x.getChild());
        xWithSiblingGamma.setSibling(gammaNode);
        xWithSiblingGamma.setType(x.getType());
        xWithSiblingGamma.setValue(x.getValue());
        node.setChild(xWithSiblingGamma);
        node.setType(ASTNodeType.EQUAL);
        break;

      // standardizing lambda
      case LAMBDA:
        childSibling = node.getChild().getSibling();
        node.getChild().setSibling(constructLambdaChain(childSibling));
        break;

      default:
        // CSE Optimization Rules are applied to the rest of the Node types.
        break;
    }
  }

  // populating comma and tau nodes
  private void processCommaAndTau(ASTNode equalNode, ASTNode commaNode, ASTNode tauNode) {
    if (equalNode.getType() != ASTNodeType.EQUAL)
      throw new StandardizeException("SIMULTDEF: one of the children is not EQUAL");
    ASTNode x = equalNode.getChild();
    ASTNode e = x.getSibling();
    setChild(commaNode, x);
    setChild(tauNode, e);
  }

  private void setChild(ASTNode parentNode, ASTNode childNode) {
    if (parentNode.getChild() == null)
      parentNode.setChild(childNode);
    else {
      ASTNode lastSibling = parentNode.getChild();
      while (lastSibling.getSibling() != null)
        lastSibling = lastSibling.getSibling();
      lastSibling.setSibling(childNode);
    }
    childNode.setSibling(null);
  }

  private ASTNode constructLambdaChain(ASTNode node) {
    if (node.getSibling() == null)
      return node;
    ASTNode lambdaNode = new ASTNode();
    lambdaNode.setType(ASTNodeType.LAMBDA);
    lambdaNode.setChild(node);
    if (node.getSibling().getSibling() != null)
      node.setSibling(constructLambdaChain(node.getSibling()));
    return lambdaNode;
  }

  // Method to create deltas
  public Delta createDeltas() {
    pendingDeltaQueue = new ArrayDeque<PendingDelta>();
    deltaIndex = 0;
    currentDelta = createDelta(root);
    processPendingDeltaStack();
    return rootDelta;
  }

  // Method to create a delta starting from a given body node
  private Delta createDelta(ASTNode startBodyNode) {
    PendingDelta pendingDelta = new PendingDelta();
    pendingDelta.startNode = startBodyNode;
    pendingDelta.body = new Stack<ASTNode>();
    pendingDeltaQueue.add(pendingDelta);

    Delta d = new Delta();
    d.setBody(pendingDelta.body);
    d.setIndex(deltaIndex++);
    currentDelta = d;

    if (startBodyNode == root)
      rootDelta = currentDelta;

    return d;
  }

  // Method to process the pending delta stack
  private void processPendingDeltaStack() {
    while (!pendingDeltaQueue.isEmpty()) {
      PendingDelta pendingDelta = pendingDeltaQueue.pop();
      buildDeltaBody(pendingDelta.startNode, pendingDelta.body);
    }
  }

  private void buildDeltaBody(ASTNode node, Stack<ASTNode> body) {
    if (node.getType() == ASTNodeType.LAMBDA) {
      Delta d = createDelta(node.getChild().getSibling());
      if (node.getChild().getType() == ASTNodeType.COMMA) {
        ASTNode commaNode = node.getChild();
        ASTNode childNode = commaNode.getChild();
        while (childNode != null) {
          d.addBoundVars(childNode.getValue());
          childNode = childNode.getSibling();
        }
      } else
        d.addBoundVars(node.getChild().getValue());
      body.push(d);
      return;
    } else if (node.getType() == ASTNodeType.CONDITIONAL) {
      ASTNode conditionNode = node.getChild();
      ASTNode thenNode = conditionNode.getSibling();
      ASTNode elseNode = thenNode.getSibling();

      Beta betaNode = new Beta();

      buildDeltaBody(thenNode, betaNode.getThenPart());
      buildDeltaBody(elseNode, betaNode.getElsePart());

      body.push(betaNode);

      buildDeltaBody(conditionNode, body);

      return;
    }

    body.push(node);
    ASTNode childNode = node.getChild();
    while (childNode != null) {
      buildDeltaBody(childNode, body);
      childNode = childNode.getSibling();
    }
  }

  private class PendingDelta {
    Stack<ASTNode> body;
    ASTNode startNode;
  }

  public boolean isStandardized() {
    return standardized;
  }

  // Method to print the AST
  public void print() {
    preOrderPrint(root, "");
  }

  // Recursive method to print the AST in pre-order traversal
  private void preOrderPrint(ASTNode node, String printPrefix) {
    if (node == null)
      return;

    printASTNodeDetails(node, printPrefix);
    preOrderPrint(node.getChild(), printPrefix + ".");
    preOrderPrint(node.getSibling(), printPrefix);
  }

  private void printASTNodeDetails(ASTNode node, String printPrefix) {
    if (node.getType() == ASTNodeType.IDENTIFIER ||
        node.getType() == ASTNodeType.INTEGER) {
      System.out.printf(printPrefix + node.getType().getPrintName() + "\n", node.getValue());
    } else if (node.getType() == ASTNodeType.STRING)
      System.out.printf(printPrefix + node.getType().getPrintName() + "\n", node.getValue());
    else
      System.out.println(printPrefix + node.getType().getPrintName());
  }
}

class ASTNode {
  private ASTNodeType type;
  private String value;
  private ASTNode child;
  private ASTNode sibling;

  public String getName() {
    return type.name();
  }

  public ASTNodeType getType() {
    return type;
  }

  public ASTNode getChild() {
    return child;
  }

  public ASTNode getSibling() {
    return sibling;
  }

  public String getValue() {
    return value;
  }

  public void setType(ASTNodeType type) {
    this.type = type;
  }

  public void setChild(ASTNode child) {
    this.child = child;
  }

  public void setSibling(ASTNode sibling) {
    this.sibling = sibling;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ASTNode accept(Copier copier) {
    return copier.copy(this);
  }

}

// Contains the AST Node Types
enum ASTNodeType {
  IDENTIFIER("<ID:%s>"),
  STRING("<STR:'%s'>"),
  INTEGER("<INT:%s>"),
  LET("let"),
  LAMBDA("lambda"),
  WHERE("where"),
  WITHIN("within"),
  CONDITIONAL("->"),
  OR("or"),
  AND("&"),
  NOT("not"),
  GR("gr"),
  GE("ge"),
  LS("ls"),
  LE("le"),
  EQ("eq"),
  NE("ne"),
  PLUS("+"),
  MINUS("-"),
  NEG("neg"),
  MULT("*"),
  DIV("/"),
  EXP("**"),
  TRUE("<true>"),
  FALSE("<false>"),
  TAU("tau"),
  AUG("aug"),
  AT("@"),
  GAMMA("gamma"),
  NIL("<nil>"),
  DUMMY("<dummy>"),
  SIMULTDEF("and"),
  REC("rec"),
  EQUAL("="),
  FCNFORM("function_form"),
  PAREN("<()>"),
  COMMA(","),
  YSTAR("<Y*>"),
  BETA(""),
  DELTA(""),
  ETA(""),
  TUPLE("");

  private String printName;

  private ASTNodeType(String name) {
    printName = name;
  }

  public String getPrintName() {
    return printName;
  }
}

class Delta extends ASTNode {
  private List<String> boundVars;
  private Environment linkedEnvironment;
  private Stack<ASTNode> body;
  private int index;

  public Delta() {
    setType(ASTNodeType.DELTA);
    boundVars = new ArrayList<String>();
  }

  public Delta accept(Copier copier) {
    return copier.copy(this);
  }

  @Override
  public String getValue() {
    return "[lambda closure: " + boundVars.get(0) + ": " + index + "]";
  }

  public List<String> getBoundVars() {
    return boundVars;
  }

  public void addBoundVars(String boundVar) {
    boundVars.add(boundVar);
  }

  public void setBoundVars(List<String> boundVars) {
    this.boundVars = boundVars;
  }

  public Stack<ASTNode> getBody() {
    return body;
  }

  public void setBody(Stack<ASTNode> body) {
    this.body = body;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Environment getLinkedEnvironment() {
    return linkedEnvironment;
  }

  public void setLinkedEnvironment(Environment linkedEnvironment) {
    this.linkedEnvironment = linkedEnvironment;
  }
}