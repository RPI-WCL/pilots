/* Generated By:JJTree: Do not edit this line. ASTFeature.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package pilots.compiler.trainer.parser;

public
class ASTFeature extends SimpleNode {
  public ASTFeature(int id) {
    super(id);
  }

  public ASTFeature(TrainerParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(TrainerParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=03faf4700b24ed97d13853ea0b2eb7b9 (do not edit this line) */
