/* Generated By:JJTree: Do not edit this line. ASTModelUser.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package pilots.compiler.trainer_parser;

public
class ASTModelUser extends SimpleNode {
  public ASTModelUser(int id) {
    super(id);
  }

  public ASTModelUser(PilotsParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PilotsParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=b79fb3e0f3eb3da1b04e690a47f5a8a1 (do not edit this line) */
