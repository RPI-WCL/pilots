/* Generated By:JJTree: Do not edit this line. ASTMapItem.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package pilots.compiler.trainer.parser;

public
class ASTMapItem extends SimpleNode {
  public ASTMapItem(int id) {
    super(id);
  }

  public ASTMapItem(TrainerParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TrainerParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=580ceeb8b553543b5f0e4c1a632003da (do not edit this line) */
