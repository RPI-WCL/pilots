/* Generated By:JJTree: Do not edit this line. ASTVARS.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package pilots.compiler.trainer_parser;

public
class ASTVARS extends SimpleNode {
  public ASTVARS(int id) {
    super(id);
  }

  public ASTVARS(PilotsParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(PilotsParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7c0084bfa5fbe00ada65de1aae16ebc2 (do not edit this line) */