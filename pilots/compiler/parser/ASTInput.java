/* Generated By:JJTree: Do not edit this line. ASTInput.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package pilots.compiler.parser;

public
class ASTInput extends SimpleNode {
  public ASTInput(int id) {
    super(id);
  }

  public ASTInput(PilotsParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PilotsParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=9815bdbb072bbeb0d07cb9186c3ff24e (do not edit this line) */
