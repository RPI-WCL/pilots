/* TrainerParserTokenManager.java */
/* Generated By:JJTree&JavaCC: Do not edit this line. TrainerParserTokenManager.java */
package pilots.compiler.trainer.parser;

/** Token Manager. */
public class TrainerParserTokenManager implements TrainerParserConstants {
  static int commentNesting;

  /** Debug output. */
  public  java.io.PrintStream debugStream = System.out;
  /** Set debug output. */
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0){
   switch (pos)
   {
      case 0:
         if ((active0 & 0x3000000000000L) != 0L)
            return 14;
         if ((active0 & 0x7fffffc00L) != 0L)
         {
            jjmatchedKind = 43;
            return 1;
         }
         return -1;
      case 1:
         if ((active0 & 0x6fffffc00L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 1;
            return 1;
         }
         if ((active0 & 0x100000000L) != 0L)
            return 1;
         return -1;
      case 2:
         if ((active0 & 0x38bffc00L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 2;
            return 1;
         }
         if ((active0 & 0x6c7400000L) != 0L)
            return 1;
         return -1;
      case 3:
         if ((active0 & 0x3fcc00L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 3;
            return 1;
         }
         if ((active0 & 0x38803000L) != 0L)
            return 1;
         return -1;
      case 4:
         if ((active0 & 0x1f8c00L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 4;
            return 1;
         }
         if ((active0 & 0x204000L) != 0L)
            return 1;
         return -1;
      case 5:
         if ((active0 & 0x1d8c00L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 5;
            return 1;
         }
         if ((active0 & 0x20000L) != 0L)
            return 1;
         return -1;
      case 6:
         if ((active0 & 0x1d8800L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 6;
            return 1;
         }
         if ((active0 & 0x400L) != 0L)
            return 1;
         return -1;
      case 7:
         if ((active0 & 0x1c0800L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 7;
            return 1;
         }
         if ((active0 & 0x18000L) != 0L)
            return 1;
         return -1;
      case 8:
         if ((active0 & 0xc0000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 8;
            return 1;
         }
         if ((active0 & 0x100800L) != 0L)
            return 1;
         return -1;
      case 9:
         if ((active0 & 0xc0000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 9;
            return 1;
         }
         return -1;
      case 10:
         if ((active0 & 0x40000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 10;
            return 1;
         }
         if ((active0 & 0x80000L) != 0L)
            return 1;
         return -1;
      case 11:
         if ((active0 & 0x40000L) != 0L)
         {
            jjmatchedKind = 43;
            jjmatchedPos = 11;
            return 1;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0){
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private int jjMoveStringLiteralDfa0_0(){
   switch(curChar)
   {
      case 13:
         jjmatchedKind = 4;
         return jjMoveStringLiteralDfa1_0(0x20L);
      case 33:
         return jjMoveStringLiteralDfa1_0(0x200000000000000L);
      case 40:
         return jjStopAtPos(0, 36);
      case 41:
         return jjStopAtPos(0, 37);
      case 42:
         return jjStopAtPos(0, 50);
      case 43:
         return jjStartNfaWithStates_0(0, 48, 14);
      case 44:
         return jjStopAtPos(0, 38);
      case 45:
         return jjStartNfaWithStates_0(0, 49, 14);
      case 47:
         jjmatchedKind = 51;
         return jjMoveStringLiteralDfa1_0(0x40L);
      case 58:
         return jjStopAtPos(0, 46);
      case 59:
         return jjStopAtPos(0, 45);
      case 60:
         jjmatchedKind = 55;
         return jjMoveStringLiteralDfa1_0(0x100000000000000L);
      case 61:
         jjmatchedKind = 47;
         return jjMoveStringLiteralDfa1_0(0x400000000000000L);
      case 62:
         jjmatchedKind = 53;
         return jjMoveStringLiteralDfa1_0(0x40000000000000L);
      case 94:
         return jjStopAtPos(0, 52);
      case 97:
         return jjMoveStringLiteralDfa1_0(0xf8100000L);
      case 99:
         return jjMoveStringLiteralDfa1_0(0x2000800L);
      case 100:
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 101:
         return jjMoveStringLiteralDfa1_0(0x400000L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x12000L);
      case 108:
         return jjMoveStringLiteralDfa1_0(0x20000L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 110:
         return jjMoveStringLiteralDfa1_0(0x400000000L);
      case 111:
         return jjMoveStringLiteralDfa1_0(0x100000000L);
      case 115:
         return jjMoveStringLiteralDfa1_0(0x1808000L);
      case 116:
         return jjMoveStringLiteralDfa1_0(0x40c0400L);
      case 117:
         return jjMoveStringLiteralDfa1_0(0x200000L);
      case 120:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private int jjMoveStringLiteralDfa1_0(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 10:
         if ((active0 & 0x20L) != 0L)
            return jjStopAtPos(1, 5);
         break;
      case 42:
         if ((active0 & 0x40L) != 0L)
            return jjStopAtPos(1, 6);
         break;
      case 61:
         if ((active0 & 0x40000000000000L) != 0L)
            return jjStopAtPos(1, 54);
         else if ((active0 & 0x100000000000000L) != 0L)
            return jjStopAtPos(1, 56);
         else if ((active0 & 0x200000000000000L) != 0L)
            return jjStopAtPos(1, 57);
         else if ((active0 & 0x400000000000000L) != 0L)
            return jjStopAtPos(1, 58);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x4021000L);
      case 98:
         return jjMoveStringLiteralDfa2_0(active0, 0x40000000L);
      case 99:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000000L);
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0xd8000L);
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x1002000L);
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x100000L);
      case 110:
         return jjMoveStringLiteralDfa2_0(active0, 0x80400000L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x602004800L);
      case 113:
         return jjMoveStringLiteralDfa2_0(active0, 0x800000L);
      case 114:
         if ((active0 & 0x100000000L) != 0L)
            return jjStartNfaWithStates_0(1, 32, 1);
         return jjMoveStringLiteralDfa2_0(active0, 0x400L);
      case 115:
         return jjMoveStringLiteralDfa2_0(active0, 0x8200000L);
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x20000000L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private int jjMoveStringLiteralDfa2_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x20010400L);
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000L);
      case 100:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(2, 22, 1);
         else if ((active0 & 0x80000000L) != 0L)
            return jjStartNfaWithStates_0(2, 31, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x4000L);
      case 103:
         return jjMoveStringLiteralDfa3_0(active0, 0x100000L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x8200000L);
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000L);
      case 110:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(2, 24, 1);
         else if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(2, 26, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x800L);
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
      case 113:
         return jjMoveStringLiteralDfa3_0(active0, 0x8000L);
      case 114:
         if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(2, 33, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x800000L);
      case 115:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(2, 25, 1);
         else if ((active0 & 0x40000000L) != 0L)
            return jjStartNfaWithStates_0(2, 30, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0xc0000L);
      case 116:
         if ((active0 & 0x400000000L) != 0L)
            return jjStartNfaWithStates_0(2, 34, 1);
         return jjMoveStringLiteralDfa3_0(active0, 0x1000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private int jjMoveStringLiteralDfa3_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 97:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(3, 12, 1);
         break;
      case 101:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(3, 13, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x24000L);
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x400L);
      case 110:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(3, 27, 1);
         else if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(3, 29, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x200000L);
      case 111:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000L);
      case 115:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(3, 28, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      case 116:
         if ((active0 & 0x800000L) != 0L)
            return jjStartNfaWithStates_0(3, 23, 1);
         return jjMoveStringLiteralDfa4_0(active0, 0xd0000L);
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x8000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private int jjMoveStringLiteralDfa4_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 95:
         return jjMoveStringLiteralDfa5_0(active0, 0xc0000L);
      case 101:
         return jjMoveStringLiteralDfa5_0(active0, 0x8000L);
      case 103:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(4, 21, 1);
         break;
      case 108:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(4, 14, 1);
         return jjMoveStringLiteralDfa5_0(active0, 0x20000L);
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x400L);
      case 114:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
      case 116:
         return jjMoveStringLiteralDfa5_0(active0, 0x800L);
      case 117:
         return jjMoveStringLiteralDfa5_0(active0, 0x10000L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private int jjMoveStringLiteralDfa5_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x800L);
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x400L);
      case 102:
         return jjMoveStringLiteralDfa6_0(active0, 0x40000L);
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x100000L);
      case 108:
         return jjMoveStringLiteralDfa6_0(active0, 0x80000L);
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x8000L);
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x10000L);
      case 115:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(5, 17, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private int jjMoveStringLiteralDfa6_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x80000L);
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x8000L);
      case 101:
         return jjMoveStringLiteralDfa7_0(active0, 0x50000L);
      case 110:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      case 114:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(6, 10, 1);
         break;
      case 116:
         return jjMoveStringLiteralDfa7_0(active0, 0x100000L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private int jjMoveStringLiteralDfa7_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa8_0(active0, 0x40000L);
      case 98:
         return jjMoveStringLiteralDfa8_0(active0, 0x80000L);
      case 101:
         if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(7, 15, 1);
         break;
      case 104:
         return jjMoveStringLiteralDfa8_0(active0, 0x100000L);
      case 115:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(7, 16, 1);
         break;
      case 116:
         return jjMoveStringLiteralDfa8_0(active0, 0x800L);
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private int jjMoveStringLiteralDfa8_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(6, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa9_0(active0, 0x80000L);
      case 109:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(8, 20, 1);
         break;
      case 115:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(8, 11, 1);
         break;
      case 116:
         return jjMoveStringLiteralDfa9_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(7, active0);
}
private int jjMoveStringLiteralDfa9_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(7, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(8, active0);
      return 9;
   }
   switch(curChar)
   {
      case 108:
         return jjMoveStringLiteralDfa10_0(active0, 0x80000L);
      case 117:
         return jjMoveStringLiteralDfa10_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(8, active0);
}
private int jjMoveStringLiteralDfa10_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(8, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(9, active0);
      return 10;
   }
   switch(curChar)
   {
      case 114:
         return jjMoveStringLiteralDfa11_0(active0, 0x40000L);
      case 115:
         if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(10, 19, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(9, active0);
}
private int jjMoveStringLiteralDfa11_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(9, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(10, active0);
      return 11;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa12_0(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_0(10, active0);
}
private int jjMoveStringLiteralDfa12_0(long old0, long active0){
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(10, old0);
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(11, active0);
      return 12;
   }
   switch(curChar)
   {
      case 115:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(12, 18, 1);
         break;
      default :
         break;
   }
   return jjStartNfa_0(11, active0);
}
private int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private int jjMoveNfa_0(int startState, int curPos)
{
   int startsAt = 0;
   jjnewStateCnt = 14;
   int i = 1;
   jjstateSet[0] = startState;
   int kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 39)
                        kind = 39;
                     { jjCheckNAddStates(0, 2); }
                  }
                  else if ((0x280000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(6, 7); }
                  else if (curChar == 34)
                     { jjCheckNAddTwoStates(3, 4); }
                  else if (curChar == 46)
                  {
                     if (kind > 43)
                        kind = 43;
                     { jjCheckNAdd(1); }
                  }
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(7, 8); }
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 39)
                        kind = 39;
                     { jjCheckNAdd(6); }
                  }
                  break;
               case 1:
                  if ((0x3ff400000000000L & l) == 0L)
                     break;
                  if (kind > 43)
                     kind = 43;
                  { jjCheckNAdd(1); }
                  break;
               case 2:
                  if (curChar == 34)
                     { jjCheckNAddTwoStates(3, 4); }
                  break;
               case 3:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     { jjCheckNAddTwoStates(3, 4); }
                  break;
               case 4:
                  if (curChar == 34 && kind > 44)
                     kind = 44;
                  break;
               case 5:
                  if ((0x280000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(6, 7); }
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 39)
                     kind = 39;
                  { jjCheckNAdd(6); }
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) != 0L)
                     { jjCheckNAddTwoStates(7, 8); }
                  break;
               case 8:
                  if (curChar != 46)
                     break;
                  if (kind > 41)
                     kind = 41;
                  { jjCheckNAddTwoStates(9, 10); }
                  break;
               case 9:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 41)
                     kind = 41;
                  { jjCheckNAddTwoStates(9, 10); }
                  break;
               case 11:
                  if ((0x280000000000L & l) != 0L)
                     { jjCheckNAdd(12); }
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 41)
                     kind = 41;
                  { jjCheckNAdd(12); }
                  break;
               case 13:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 39)
                     kind = 39;
                  { jjCheckNAddStates(0, 2); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 0:
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 43)
                     kind = 43;
                  { jjCheckNAdd(1); }
                  break;
               case 3:
                  { jjAddStates(3, 4); }
                  break;
               case 10:
                  if ((0x2000000020L & l) != 0L)
                     { jjAddStates(5, 6); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         do
         {
            switch(jjstateSet[--i])
            {
               case 3:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     { jjAddStates(3, 4); }
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 14 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private int jjMoveStringLiteralDfa0_1(){
   switch(curChar)
   {
      case 42:
         return jjMoveStringLiteralDfa1_1(0x200L);
      case 47:
         return jjMoveStringLiteralDfa1_1(0x100L);
      default :
         return 1;
   }
}
private int jjMoveStringLiteralDfa1_1(long active0){
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 42:
         if ((active0 & 0x100L) != 0L)
            return jjStopAtPos(1, 8);
         break;
      case 47:
         if ((active0 & 0x200L) != 0L)
            return jjStopAtPos(1, 9);
         break;
      default :
         return 2;
   }
   return 2;
}

/** Token literal values. */
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, 
"\164\162\141\151\156\145\162", "\143\157\156\163\164\141\156\164\163", "\144\141\164\141", 
"\146\151\154\145", "\155\157\144\145\154", "\163\145\161\165\145\156\143\145", 
"\146\145\141\164\165\162\145\163", "\154\141\142\145\154\163", 
"\164\145\163\164\137\146\145\141\164\165\162\145\163", "\164\145\163\164\137\154\141\142\145\154\163", 
"\141\154\147\157\162\151\164\150\155", "\165\163\151\156\147", "\145\156\144", "\163\161\162\164", "\163\151\156", 
"\143\157\163", "\164\141\156", "\141\163\151\156", "\141\143\157\163", "\141\164\141\156", 
"\141\142\163", "\141\156\144", "\157\162", "\170\157\162", "\156\157\164", null, "\50", 
"\51", "\54", null, null, null, null, null, null, "\73", "\72", "\75", "\53", "\55", 
"\52", "\57", "\136", "\76", "\76\75", "\74", "\74\75", "\41\75", "\75\75", };
protected Token jjFillToken()
{
   final Token t;
   final String curTokenImage;
   final int beginLine;
   final int endLine;
   final int beginColumn;
   final int endColumn;
   String im = jjstrLiteralImages[jjmatchedKind];
   curTokenImage = (im == null) ? input_stream.GetImage() : im;
   beginLine = input_stream.getBeginLine();
   beginColumn = input_stream.getBeginColumn();
   endLine = input_stream.getEndLine();
   endColumn = input_stream.getEndColumn();
   t = Token.newToken(jjmatchedKind, curTokenImage);

   t.beginLine = beginLine;
   t.endLine = endLine;
   t.beginColumn = beginColumn;
   t.endColumn = endColumn;

   return t;
}
static final int[] jjnextStates = {
   6, 7, 8, 3, 4, 11, 12, 
};

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

/** Get the next Token. */
public Token getNextToken() 
{
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {
   try
   {
      curChar = input_stream.BeginToken();
   }
   catch(Exception e)
   {
      jjmatchedKind = 0;
      jjmatchedPos = -1;
      matchedToken = jjFillToken();
      return matchedToken;
   }
   image = jjimage;
   image.setLength(0);
   jjimageLen = 0;

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         try { input_stream.backup(0);
            while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
               curChar = input_stream.BeginToken();
         }
         catch (java.io.IOException e1) { continue EOFLoop; }
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         break;
       case 1:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         if (jjmatchedPos == 0 && jjmatchedKind > 7)
         {
            jjmatchedKind = 7;
         }
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           SkipLexicalActions(null);
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
        jjimageLen += jjmatchedPos + 1;
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

void SkipLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      case 6 :
         image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
         commentNesting=1;
         break;
      case 8 :
         image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
             commentNesting++;
         break;
      case 9 :
         image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
             commentNesting--; if (commentNesting==0) SwitchTo(DEFAULT);
         break;
      default :
         break;
   }
}
void MoreLexicalActions()
{
   jjimageLen += (lengthOfMatch = jjmatchedPos + 1);
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
void TokenLexicalActions(Token matchedToken)
{
   switch(jjmatchedKind)
   {
      default :
         break;
   }
}
private void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}

private void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}

    /** Constructor. */
    public TrainerParserTokenManager(SimpleCharStream stream){

      if (SimpleCharStream.staticFlag)
            throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");

    input_stream = stream;
  }

  /** Constructor. */
  public TrainerParserTokenManager (SimpleCharStream stream, int lexState){
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Reinitialise parser. */
  
  public void ReInit(SimpleCharStream stream)
  {


    jjmatchedPos =
    jjnewStateCnt =
    0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private void ReInitRounds()
  {
    int i;
    jjround = 0x80000001;
    for (i = 14; i-- > 0;)
      jjrounds[i] = 0x80000000;
  }

  /** Reinitialise parser. */
  public void ReInit(SimpleCharStream stream, int lexState)
  
  {
    ReInit(stream);
    SwitchTo(lexState);
  }

  /** Switch to specified lex state. */
  public void SwitchTo(int lexState)
  {
    if (lexState >= 2 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
    else
      curLexState = lexState;
  }


/** Lexer state names. */
public static final String[] lexStateNames = {
   "DEFAULT",
   "IN_COMMENT",
};

/** Lex State array. */
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
   -1, -1, -1, -1, -1, -1, -1, -1, -1, 
};
static final long[] jjtoToken = {
   0x7fffaf7fffffc01L, 
};
static final long[] jjtoSkip = {
   0x37eL, 
};
static final long[] jjtoSpecial = {
   0x0L, 
};
static final long[] jjtoMore = {
   0x80L, 
};
    protected SimpleCharStream  input_stream;

    private final int[] jjrounds = new int[14];
    private final int[] jjstateSet = new int[2 * 14];
    private final StringBuilder jjimage = new StringBuilder();
    private StringBuilder image = jjimage;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
}
