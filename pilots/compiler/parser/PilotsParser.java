/* Generated By:JJTree&JavaCC: Do not edit this line. PilotsParser.java */
package pilots.compiler.parser;
import java.io.*;

public class PilotsParser/*@bgen(jjtree)*/implements PilotsParserTreeConstants, PilotsParserConstants {/*@bgen(jjtree)*/
  protected static JJTPilotsParserState jjtree = new JJTPilotsParserState();

/**************************************/
/********** START OF GRAMMAR **********/
/**************************************/
  static final public ASTPilots Pilots() throws ParseException {
 /*@bgen(jjtree) Pilots */
    ASTPilots jjtn000 = new ASTPilots(JJTPILOTS);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      jj_consume_token(PROGRAM_START);
      t = jj_consume_token(VAR);
                                jjtn000.jjtSetValue( t.image );
      jj_consume_token(41);
      jj_consume_token(INPUTS);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 41:
        jj_consume_token(41);
        break;
      default:
        jj_la1[1] = jj_gen;
        label_1:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case VAR:
            ;
            break;
          default:
            jj_la1[0] = jj_gen;
            break label_1;
          }
          Input();
        }
      }
      jj_consume_token(OUTPUTS);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 41:
        jj_consume_token(41);
        break;
      default:
        jj_la1[3] = jj_gen;
        label_2:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case VAR:
            ;
            break;
          default:
            jj_la1[2] = jj_gen;
            break label_2;
          }
          Output();
        }
      }
      jj_consume_token(ERRORS);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 41:
        jj_consume_token(41);
        break;
      default:
        jj_la1[5] = jj_gen;
        label_3:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case VAR:
            ;
            break;
          default:
            jj_la1[4] = jj_gen;
            break label_3;
          }
          Error();
        }
      }
      jj_consume_token(PROGRAM_END);
      jj_consume_token(41);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public void Input() throws ParseException {
 /*@bgen(jjtree) Input */
    ASTInput jjtn000 = new ASTInput(JJTINPUT);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String str;
    try {
      str = Vars();
      jjtn000.jjtSetValue( str );
      jj_consume_token(42);
      Dims();
      jj_consume_token(USING);
      Methods();
      jj_consume_token(41);
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Output() throws ParseException {
 /*@bgen(jjtree) Output */
    ASTOutput jjtn000 = new ASTOutput(JJTOUTPUT);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String str, exps, time;
    try {
      str = Vars();
      jj_consume_token(42);
      exps = Exps();
      jj_consume_token(AT);
      jj_consume_token(EVERY);
      time = Time();
      jj_consume_token(41);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      jjtn000.jjtSetValue( str + ":" + exps + ":" + time );
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Error() throws ParseException {
 /*@bgen(jjtree) Error */
    ASTError jjtn000 = new ASTError(JJTERROR);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String str, exps, time;
    try {
      str = Vars();
      jj_consume_token(42);
      exps = Exps();
      jj_consume_token(AT);
      jj_consume_token(EVERY);
      time = Time();
      jj_consume_token(41);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      jjtn000.jjtSetValue( str + ":" + exps + ":" + time );
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public String Vars() throws ParseException {
 /*@bgen(jjtree) Vars */
    ASTVars jjtn000 = new ASTVars(JJTVARS);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    String str;
    try {
      t = jj_consume_token(VAR);
                str = (String)t.image;
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[6] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        t = jj_consume_token(VAR);
                     str += "," + (String)t.image;
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      {if (true) return str;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public void Dims() throws ParseException {
              /*@bgen(jjtree) Dims */
  ASTDims jjtn000 = new ASTDims(JJTDIMS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(LPAR);
      Exps();
      jj_consume_token(RPAR);
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Method() throws ParseException {
 /*@bgen(jjtree) Method */
    ASTMethod jjtn000 = new ASTMethod(JJTMETHOD);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    String id, args;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CLOSEST:
        t = jj_consume_token(CLOSEST);
                     id = (String)t.image;
        break;
      case EUCLIDEAN:
        t = jj_consume_token(EUCLIDEAN);
                         id = (String)t.image;
        break;
      case INTERPOLATE:
        t = jj_consume_token(INTERPOLATE);
                           id = (String)t.image;
        break;
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jj_consume_token(LPAR);
      args = Exps();
      jj_consume_token(RPAR);
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      jjtn000.jjtSetValue( id + ":" + args );
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public void Methods() throws ParseException {
                 /*@bgen(jjtree) Methods */
  ASTMethods jjtn000 = new ASTMethods(JJTMETHODS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      Method();
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_5;
        }
        jj_consume_token(COMMA);
        Method();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  static final public String Time() throws ParseException {
 /*@bgen(jjtree) Time */
    ASTTime jjtn000 = new ASTTime(JJTTIME);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    String number;
    try {
      number = Number();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NSEC:
        t = jj_consume_token(NSEC);
                 jjtree.closeNodeScope(jjtn000, true);
                 jjtc000 = false;
                 {if (true) return number + ":" + t.image;}
        break;
      case USEC:
        t = jj_consume_token(USEC);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return number + ":" + t.image;}
        break;
      case MSEC:
        t = jj_consume_token(MSEC);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return number + ":" + t.image;}
        break;
      case SEC:
        t = jj_consume_token(SEC);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return number + ":" + t.image;}
        break;
      case MIN:
        t = jj_consume_token(MIN);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return number + ":" + t.image;}
        break;
      case HOUR:
        t = jj_consume_token(HOUR);
                    jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    {if (true) return number + ":" + t.image;}
        break;
      case DAY:
        t = jj_consume_token(DAY);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return number + ":" + t.image;}
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Exps() throws ParseException {
 /*@bgen(jjtree) Exps */
    ASTExps jjtn000 = new ASTExps(JJTEXPS);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String exp, temp;
    try {
      exp = Exp();
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_6;
        }
        jj_consume_token(COMMA);
        temp = Exp();
                                    exp += "," + temp;
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      {if (true) return exp;}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Exp() throws ParseException {
 /*@bgen(jjtree) Exp */
    ASTExp jjtn000 = new ASTExp(JJTEXP);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String func, exps, exp, exp2, value, temp;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SQRT:
      case SIN:
      case COS:
      case TAN:
      case ARCSIN:
      case ARCCOS:
      case ARCTAN:
      case ABS:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
        func = Func();
        jj_consume_token(LPAR);
        exps = Exps();
        jj_consume_token(RPAR);
        exp2 = Exp2();
                                                        jjtree.closeNodeScope(jjtn000, true);
                                                        jjtc000 = false;
                                                        {if (true) return func + "(" + exps + ")" + exp2;}
        break;
      case LPAR:
        jj_consume_token(LPAR);
        temp = Exp();
        jj_consume_token(RPAR);
        exp2 = Exp2();
                                           jjtree.closeNodeScope(jjtn000, true);
                                           jjtc000 = false;
                                           {if (true) return "(" + temp + ")" + exp2;}
        break;
      case INTEGER:
      case REAL:
      case VAR:
        value = Value();
        exp2 = Exp2();
                                      jjtree.closeNodeScope(jjtn000, true);
                                      jjtc000 = false;
                                      {if (true) return value + exp2;}
        break;
      default:
        jj_la1[11] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Exp2() throws ParseException {
 /*@bgen(jjtree) Exp2 */
    ASTExp2 jjtn000 = new ASTExp2(JJTEXP2);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);String func, exp, exp2;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SQRT:
      case SIN:
      case COS:
      case TAN:
      case ARCSIN:
      case ARCCOS:
      case ARCTAN:
      case ABS:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
        func = Func();
        exp = Exp();
        exp2 = Exp2();
                                              jjtree.closeNodeScope(jjtn000, true);
                                              jjtc000 = false;
                                              {if (true) return func + exp + exp2;}
        break;
      default:
        jj_la1[12] = jj_gen;

           jjtree.closeNodeScope(jjtn000, true);
           jjtc000 = false;
           {if (true) return "";}
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Func() throws ParseException {
 /*@bgen(jjtree) Func */
    ASTFunc jjtn000 = new ASTFunc(JJTFUNC);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 43:
        t = jj_consume_token(43);
              jjtree.closeNodeScope(jjtn000, true);
              jjtc000 = false;
              {if (true) return t.image;}
        break;
      case 44:
        t = jj_consume_token(44);
                jjtree.closeNodeScope(jjtn000, true);
                jjtc000 = false;
                {if (true) return t.image;}
        break;
      case 45:
        t = jj_consume_token(45);
                jjtree.closeNodeScope(jjtn000, true);
                jjtc000 = false;
                {if (true) return t.image;}
        break;
      case 46:
        t = jj_consume_token(46);
                jjtree.closeNodeScope(jjtn000, true);
                jjtc000 = false;
                {if (true) return t.image;}
        break;
      case 47:
        t = jj_consume_token(47);
                jjtree.closeNodeScope(jjtn000, true);
                jjtc000 = false;
                {if (true) return t.image;}
        break;
      case SQRT:
        t = jj_consume_token(SQRT);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return t.image;}
        break;
      case SIN:
        t = jj_consume_token(SIN);
                  jjtree.closeNodeScope(jjtn000, true);
                  jjtc000 = false;
                  {if (true) return t.image;}
        break;
      case COS:
        t = jj_consume_token(COS);
                  jjtree.closeNodeScope(jjtn000, true);
                  jjtc000 = false;
                  {if (true) return t.image;}
        break;
      case TAN:
        t = jj_consume_token(TAN);
                  jjtree.closeNodeScope(jjtn000, true);
                  jjtc000 = false;
                  {if (true) return t.image;}
        break;
      case ARCSIN:
        t = jj_consume_token(ARCSIN);
                     jjtree.closeNodeScope(jjtn000, true);
                     jjtc000 = false;
                     {if (true) return t.image;}
        break;
      case ARCCOS:
        t = jj_consume_token(ARCCOS);
                     jjtree.closeNodeScope(jjtn000, true);
                     jjtc000 = false;
                     {if (true) return t.image;}
        break;
      case ARCTAN:
        t = jj_consume_token(ARCTAN);
                     jjtree.closeNodeScope(jjtn000, true);
                     jjtc000 = false;
                     {if (true) return t.image;}
        break;
      case ABS:
        t = jj_consume_token(ABS);
                  jjtree.closeNodeScope(jjtn000, true);
                  jjtc000 = false;
                  {if (true) return t.image;}
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Number() throws ParseException {
 /*@bgen(jjtree) Number */
    ASTNumber jjtn000 = new ASTNumber(JJTNUMBER);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
        t = jj_consume_token(INTEGER);
                    jjtree.closeNodeScope(jjtn000, true);
                    jjtc000 = false;
                    {if (true) return t.image;}
        break;
      case REAL:
        t = jj_consume_token(REAL);
                   jjtree.closeNodeScope(jjtn000, true);
                   jjtc000 = false;
                   {if (true) return t.image;}
        break;
      default:
        jj_la1[14] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static final public String Value() throws ParseException {
 /*@bgen(jjtree) Value */
    ASTValue jjtn000 = new ASTValue(JJTVALUE);
    boolean jjtc000 = true;
    jjtree.openNodeScope(jjtn000);Token t;
    String number;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case INTEGER:
      case REAL:
        number = Number();
                        jjtree.closeNodeScope(jjtn000, true);
                        jjtc000 = false;
                        {if (true) return number;}
        break;
      case VAR:
        t = jj_consume_token(VAR);
                  jjtn000.jjtSetValue( t.image );
                                                      jjtree.closeNodeScope(jjtn000, true);
                                                      jjtc000 = false;
                                                      {if (true) return t.image;}
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public PilotsParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[16];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xe000,0x0,0x7f0000,0x0,0x7f800000,0x7f800000,0x7f800000,0x0,0x0,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x100,0x200,0x100,0x200,0x100,0x200,0x8,0x0,0x8,0x0,0x8,0xf952,0xf800,0xf800,0x50,0x150,};
   }

  /** Constructor with InputStream. */
  public PilotsParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public PilotsParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new PilotsParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public PilotsParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new PilotsParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public PilotsParser(PilotsParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(PilotsParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[48];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 16; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 48; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}
