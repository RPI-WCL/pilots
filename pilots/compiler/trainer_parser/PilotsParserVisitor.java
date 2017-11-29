/* Generated By:JavaCC: Do not edit this line. PilotsParserVisitor.java Version 6.0_1 */
package pilots.compiler.trainer_parser;

public interface PilotsParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTPROGRAM node, Object data);
  public Object visit(ASTCONSTANTS node, Object data);
  public Object visit(ASTData node, Object data);
  public Object visit(ASTDataItem node, Object data);
  public Object visit(ASTFile node, Object data);
  public Object visit(ASTModelUser node, Object data);
  public Object visit(ASTString node, Object data);
  public Object visit(ASTModel node, Object data);
  public Object visit(ASTSchema node, Object data);
  public Object visit(ASTPreprocess node, Object data);
  public Object visit(ASTFeatures node, Object data);
  public Object visit(ASTLabels node, Object data);
  public Object visit(ASTAlgorithm node, Object data);
  public Object visit(ASTTraining node, Object data);
  public Object visit(ASTMap node, Object data);
  public Object visit(ASTMapItem node, Object data);
  public Object visit(ASTPredicate node, Object data);
  public Object visit(ASTFuncExp node, Object data);
  public Object visit(ASTNumMap node, Object data);
  public Object visit(ASTNumMapItem node, Object data);
  public Object visit(ASTEQUATION node, Object data);
  public Object visit(ASTVARS node, Object data);
  public Object visit(ASTVAR node, Object data);
  public Object visit(ASTExps node, Object data);
  public Object visit(ASTExp node, Object data);
  public Object visit(ASTExp2 node, Object data);
  public Object visit(ASTFunc node, Object data);
  public Object visit(ASTNumber node, Object data);
  public Object visit(ASTValue node, Object data);
}
/* JavaCC - OriginalChecksum=9532b0801215a67f813068c901225bd0 (do not edit this line) */