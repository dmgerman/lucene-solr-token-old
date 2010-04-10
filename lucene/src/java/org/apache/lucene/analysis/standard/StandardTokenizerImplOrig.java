begin_unit
begin_comment
comment|/* The following code was generated by JFlex 1.5.0-SNAPSHOT on 10.04.10 13:07 */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/*  WARNING: if you change StandardTokenizerImpl*.jflex and need to regenerate       the tokenizer, only use the trunk version of JFlex 1.5 at the moment!  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import
begin_comment
comment|/**  * This class is a scanner generated by   *<a href="http://www.jflex.de/">JFlex</a> 1.5.0-SNAPSHOT  * on 10.04.10 13:07 from the specification file  *<tt>C:/Users/Uwe Schindler/Projects/lucene/trunk-full1/src/java/org/apache/lucene/analysis/standard/StandardTokenizerImplOrig.jflex</tt>  */
end_comment
begin_class
DECL|class|StandardTokenizerImplOrig
class|class
name|StandardTokenizerImplOrig
implements|implements
name|StandardTokenizerInterface
block|{
comment|/** This character denotes the end of file */
DECL|field|YYEOF
specifier|public
specifier|static
specifier|final
name|int
name|YYEOF
init|=
operator|-
literal|1
decl_stmt|;
comment|/** initial size of the lookahead buffer */
DECL|field|ZZ_BUFFERSIZE
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_BUFFERSIZE
init|=
literal|16384
decl_stmt|;
comment|/** lexical states */
DECL|field|YYINITIAL
specifier|public
specifier|static
specifier|final
name|int
name|YYINITIAL
init|=
literal|0
decl_stmt|;
comment|/**    * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l    * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l    *                  at the beginning of a line    * l is of the form l = 2*k, k a non negative integer    */
DECL|field|ZZ_LEXSTATE
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_LEXSTATE
index|[]
init|=
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
comment|/**     * Translates characters to character classes    */
DECL|field|ZZ_CMAP_PACKED
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_CMAP_PACKED
init|=
literal|"\11\0\1\0\1\15\1\0\1\0\1\14\22\0\1\0\5\0\1\5"
operator|+
literal|"\1\3\4\0\1\11\1\7\1\4\1\11\12\2\6\0\1\6\32\12"
operator|+
literal|"\4\0\1\10\1\0\32\12\57\0\1\12\12\0\1\12\4\0\1\12"
operator|+
literal|"\5\0\27\12\1\0\37\12\1\0\u0128\12\2\0\22\12\34\0\136\12"
operator|+
literal|"\2\0\11\12\2\0\7\12\16\0\2\12\16\0\5\12\11\0\1\12"
operator|+
literal|"\213\0\1\12\13\0\1\12\1\0\3\12\1\0\1\12\1\0\24\12"
operator|+
literal|"\1\0\54\12\1\0\10\12\2\0\32\12\14\0\202\12\12\0\71\12"
operator|+
literal|"\2\0\2\12\2\0\2\12\3\0\46\12\2\0\2\12\67\0\46\12"
operator|+
literal|"\2\0\1\12\7\0\47\12\110\0\33\12\5\0\3\12\56\0\32\12"
operator|+
literal|"\5\0\13\12\25\0\12\2\7\0\143\12\1\0\1\12\17\0\2\12"
operator|+
literal|"\11\0\12\2\3\12\23\0\1\12\1\0\33\12\123\0\46\12\u015f\0"
operator|+
literal|"\65\12\3\0\1\12\22\0\1\12\7\0\12\12\4\0\12\2\25\0"
operator|+
literal|"\10\12\2\0\2\12\2\0\26\12\1\0\7\12\1\0\1\12\3\0"
operator|+
literal|"\4\12\42\0\2\12\1\0\3\12\4\0\12\2\2\12\23\0\6\12"
operator|+
literal|"\4\0\2\12\2\0\26\12\1\0\7\12\1\0\2\12\1\0\2\12"
operator|+
literal|"\1\0\2\12\37\0\4\12\1\0\1\12\7\0\12\2\2\0\3\12"
operator|+
literal|"\20\0\7\12\1\0\1\12\1\0\3\12\1\0\26\12\1\0\7\12"
operator|+
literal|"\1\0\2\12\1\0\5\12\3\0\1\12\22\0\1\12\17\0\1\12"
operator|+
literal|"\5\0\12\2\25\0\10\12\2\0\2\12\2\0\26\12\1\0\7\12"
operator|+
literal|"\1\0\2\12\2\0\4\12\3\0\1\12\36\0\2\12\1\0\3\12"
operator|+
literal|"\4\0\12\2\25\0\6\12\3\0\3\12\1\0\4\12\3\0\2\12"
operator|+
literal|"\1\0\1\12\1\0\2\12\3\0\2\12\3\0\3\12\3\0\10\12"
operator|+
literal|"\1\0\3\12\55\0\11\2\25\0\10\12\1\0\3\12\1\0\27\12"
operator|+
literal|"\1\0\12\12\1\0\5\12\46\0\2\12\4\0\12\2\25\0\10\12"
operator|+
literal|"\1\0\3\12\1\0\27\12\1\0\12\12\1\0\5\12\44\0\1\12"
operator|+
literal|"\1\0\2\12\4\0\12\2\25\0\10\12\1\0\3\12\1\0\27\12"
operator|+
literal|"\1\0\20\12\46\0\2\12\4\0\12\2\25\0\22\12\3\0\30\12"
operator|+
literal|"\1\0\11\12\1\0\1\12\2\0\7\12\71\0\1\1\60\12\1\1"
operator|+
literal|"\2\12\14\1\7\12\11\1\12\2\47\0\2\12\1\0\1\12\2\0"
operator|+
literal|"\2\12\1\0\1\12\2\0\1\12\6\0\4\12\1\0\7\12\1\0"
operator|+
literal|"\3\12\1\0\1\12\1\0\1\12\2\0\2\12\1\0\4\12\1\0"
operator|+
literal|"\2\12\11\0\1\12\2\0\5\12\1\0\1\12\11\0\12\2\2\0"
operator|+
literal|"\2\12\42\0\1\12\37\0\12\2\26\0\10\12\1\0\42\12\35\0"
operator|+
literal|"\4\12\164\0\42\12\1\0\5\12\1\0\2\12\25\0\12\2\6\0"
operator|+
literal|"\6\12\112\0\46\12\12\0\47\12\11\0\132\12\5\0\104\12\5\0"
operator|+
literal|"\122\12\6\0\7\12\1\0\77\12\1\0\1\12\1\0\4\12\2\0"
operator|+
literal|"\7\12\1\0\1\12\1\0\4\12\2\0\47\12\1\0\1\12\1\0"
operator|+
literal|"\4\12\2\0\37\12\1\0\1\12\1\0\4\12\2\0\7\12\1\0"
operator|+
literal|"\1\12\1\0\4\12\2\0\7\12\1\0\7\12\1\0\27\12\1\0"
operator|+
literal|"\37\12\1\0\1\12\1\0\4\12\2\0\7\12\1\0\47\12\1\0"
operator|+
literal|"\23\12\16\0\11\2\56\0\125\12\14\0\u026c\12\2\0\10\12\12\0"
operator|+
literal|"\32\12\5\0\113\12\225\0\64\12\54\0\12\2\46\0\12\2\6\0"
operator|+
literal|"\130\12\10\0\51\12\u0557\0\234\12\4\0\132\12\6\0\26\12\2\0"
operator|+
literal|"\6\12\2\0\46\12\2\0\6\12\2\0\10\12\1\0\1\12\1\0"
operator|+
literal|"\1\12\1\0\1\12\1\0\37\12\2\0\65\12\1\0\7\12\1\0"
operator|+
literal|"\1\12\3\0\3\12\1\0\7\12\3\0\4\12\2\0\6\12\4\0"
operator|+
literal|"\15\12\5\0\3\12\1\0\7\12\202\0\1\12\202\0\1\12\4\0"
operator|+
literal|"\1\12\2\0\12\12\1\0\1\12\3\0\5\12\6\0\1\12\1\0"
operator|+
literal|"\1\12\1\0\1\12\1\0\4\12\1\0\3\12\1\0\7\12\u0ecb\0"
operator|+
literal|"\2\12\52\0\5\12\12\0\1\13\124\13\10\13\2\13\2\13\132\13"
operator|+
literal|"\1\13\3\13\6\13\50\13\3\13\1\0\136\12\21\0\30\12\70\0"
operator|+
literal|"\20\13\u0100\0\200\13\200\0\u19b6\13\12\13\100\0\u51a6\13\132\13\u048d\12"
operator|+
literal|"\u0773\0\u2ba4\12\u215c\0\u012e\13\322\13\7\12\14\0\5\12\5\0\1\12"
operator|+
literal|"\1\0\12\12\1\0\15\12\1\0\5\12\1\0\1\12\1\0\2\12"
operator|+
literal|"\1\0\2\12\1\0\154\12\41\0\u016b\12\22\0\100\12\2\0\66\12"
operator|+
literal|"\50\0\14\12\164\0\3\12\1\0\1\12\1\0\207\12\23\0\12\2"
operator|+
literal|"\7\0\32\12\6\0\32\12\12\0\1\13\72\13\37\12\3\0\6\12"
operator|+
literal|"\2\0\6\12\2\0\6\12\2\0\3\12\43\0"
decl_stmt|;
comment|/**     * Translates characters to character classes    */
DECL|field|ZZ_CMAP
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|ZZ_CMAP
init|=
name|zzUnpackCMap
argument_list|(
name|ZZ_CMAP_PACKED
argument_list|)
decl_stmt|;
comment|/**     * Translates DFA states to action switch labels.    */
DECL|field|ZZ_ACTION
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ACTION
init|=
name|zzUnpackAction
argument_list|()
decl_stmt|;
DECL|field|ZZ_ACTION_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ACTION_PACKED_0
init|=
literal|"\1\0\1\1\3\2\1\3\1\1\13\0\1\2\3\4"
operator|+
literal|"\2\0\1\5\1\0\1\5\3\4\6\5\1\6\1\4"
operator|+
literal|"\2\7\1\10\1\0\1\10\3\0\2\10\1\11\1\12"
operator|+
literal|"\1\4"
decl_stmt|;
DECL|method|zzUnpackAction
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackAction
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|51
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackAction
argument_list|(
name|ZZ_ACTION_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackAction
specifier|private
specifier|static
name|int
name|zzUnpackAction
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/**     * Translates a state to a row index in the transition table    */
DECL|field|ZZ_ROWMAP
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ROWMAP
init|=
name|zzUnpackRowMap
argument_list|()
decl_stmt|;
DECL|field|ZZ_ROWMAP_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ROWMAP_PACKED_0
init|=
literal|"\0\0\0\16\0\34\0\52\0\70\0\16\0\106\0\124"
operator|+
literal|"\0\142\0\160\0\176\0\214\0\232\0\250\0\266\0\304"
operator|+
literal|"\0\322\0\340\0\356\0\374\0\u010a\0\u0118\0\u0126\0\u0134"
operator|+
literal|"\0\u0142\0\u0150\0\u015e\0\u016c\0\u017a\0\u0188\0\u0196\0\u01a4"
operator|+
literal|"\0\u01b2\0\u01c0\0\u01ce\0\u01dc\0\u01ea\0\u01f8\0\322\0\u0206"
operator|+
literal|"\0\u0214\0\u0222\0\u0230\0\u023e\0\u024c\0\u025a\0\124\0\214"
operator|+
literal|"\0\u0268\0\u0276\0\u0284"
decl_stmt|;
DECL|method|zzUnpackRowMap
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackRowMap
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|51
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackRowMap
argument_list|(
name|ZZ_ROWMAP_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackRowMap
specifier|private
specifier|static
name|int
name|zzUnpackRowMap
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|high
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
operator|<<
literal|16
decl_stmt|;
name|result
index|[
name|j
operator|++
index|]
operator|=
name|high
operator||
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|j
return|;
block|}
comment|/**     * The transition table of the DFA    */
DECL|field|ZZ_TRANS
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_TRANS
init|=
name|zzUnpackTrans
argument_list|()
decl_stmt|;
DECL|field|ZZ_TRANS_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_TRANS_PACKED_0
init|=
literal|"\1\2\1\3\1\4\7\2\1\5\1\6\1\7\1\2"
operator|+
literal|"\17\0\2\3\1\0\1\10\1\0\1\11\2\12\1\13"
operator|+
literal|"\1\3\4\0\1\3\1\4\1\0\1\14\1\0\1\11"
operator|+
literal|"\2\15\1\16\1\4\4\0\1\3\1\4\1\17\1\20"
operator|+
literal|"\1\21\1\22\2\12\1\13\1\23\20\0\1\2\1\0"
operator|+
literal|"\1\24\1\25\7\0\1\26\4\0\2\27\7\0\1\27"
operator|+
literal|"\4\0\1\30\1\31\7\0\1\32\5\0\1\33\7\0"
operator|+
literal|"\1\13\4\0\1\34\1\35\7\0\1\36\4\0\1\37"
operator|+
literal|"\1\40\7\0\1\41\4\0\1\42\1\43\7\0\1\44"
operator|+
literal|"\15\0\1\45\4\0\1\24\1\25\7\0\1\46\15\0"
operator|+
literal|"\1\47\4\0\2\27\7\0\1\50\4\0\1\3\1\4"
operator|+
literal|"\1\17\1\10\1\21\1\22\2\12\1\13\1\23\4\0"
operator|+
literal|"\2\24\1\0\1\51\1\0\1\11\2\52\1\0\1\24"
operator|+
literal|"\4\0\1\24\1\25\1\0\1\53\1\0\1\11\2\54"
operator|+
literal|"\1\55\1\25\4\0\1\24\1\25\1\0\1\51\1\0"
operator|+
literal|"\1\11\2\52\1\0\1\26\4\0\2\27\1\0\1\56"
operator|+
literal|"\2\0\1\56\2\0\1\27\4\0\2\30\1\0\1\52"
operator|+
literal|"\1\0\1\11\2\52\1\0\1\30\4\0\1\30\1\31"
operator|+
literal|"\1\0\1\54\1\0\1\11\2\54\1\55\1\31\4\0"
operator|+
literal|"\1\30\1\31\1\0\1\52\1\0\1\11\2\52\1\0"
operator|+
literal|"\1\32\5\0\1\33\1\0\1\55\2\0\3\55\1\33"
operator|+
literal|"\4\0\2\34\1\0\1\57\1\0\1\11\2\12\1\13"
operator|+
literal|"\1\34\4\0\1\34\1\35\1\0\1\60\1\0\1\11"
operator|+
literal|"\2\15\1\16\1\35\4\0\1\34\1\35\1\0\1\57"
operator|+
literal|"\1\0\1\11\2\12\1\13\1\36\4\0\2\37\1\0"
operator|+
literal|"\1\12\1\0\1\11\2\12\1\13\1\37\4\0\1\37"
operator|+
literal|"\1\40\1\0\1\15\1\0\1\11\2\15\1\16\1\40"
operator|+
literal|"\4\0\1\37\1\40\1\0\1\12\1\0\1\11\2\12"
operator|+
literal|"\1\13\1\41\4\0\2\42\1\0\1\13\2\0\3\13"
operator|+
literal|"\1\42\4\0\1\42\1\43\1\0\1\16\2\0\3\16"
operator|+
literal|"\1\43\4\0\1\42\1\43\1\0\1\13\2\0\3\13"
operator|+
literal|"\1\44\6\0\1\17\6\0\1\45\4\0\1\24\1\25"
operator|+
literal|"\1\0\1\61\1\0\1\11\2\52\1\0\1\26\4\0"
operator|+
literal|"\2\27\1\0\1\56\2\0\1\56\2\0\1\50\4\0"
operator|+
literal|"\2\24\7\0\1\24\4\0\2\30\7\0\1\30\4\0"
operator|+
literal|"\2\34\7\0\1\34\4\0\2\37\7\0\1\37\4\0"
operator|+
literal|"\2\42\7\0\1\42\4\0\2\62\7\0\1\62\4\0"
operator|+
literal|"\2\24\7\0\1\63\4\0\2\62\1\0\1\56\2\0"
operator|+
literal|"\1\56\2\0\1\62\4\0\2\24\1\0\1\61\1\0"
operator|+
literal|"\1\11\2\52\1\0\1\24\3\0"
decl_stmt|;
DECL|method|zzUnpackTrans
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackTrans
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|658
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackTrans
argument_list|(
name|ZZ_TRANS_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackTrans
specifier|private
specifier|static
name|int
name|zzUnpackTrans
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|value
operator|--
expr_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/* error codes */
DECL|field|ZZ_UNKNOWN_ERROR
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_UNKNOWN_ERROR
init|=
literal|0
decl_stmt|;
DECL|field|ZZ_NO_MATCH
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_NO_MATCH
init|=
literal|1
decl_stmt|;
DECL|field|ZZ_PUSHBACK_2BIG
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_PUSHBACK_2BIG
init|=
literal|2
decl_stmt|;
comment|/* error messages for the codes above */
DECL|field|ZZ_ERROR_MSG
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ERROR_MSG
index|[]
init|=
block|{
literal|"Unkown internal scanner error"
block|,
literal|"Error: could not match input"
block|,
literal|"Error: pushback value was too large"
block|}
decl_stmt|;
comment|/**    * ZZ_ATTRIBUTE[aState] contains the attributes of state<code>aState</code>    */
DECL|field|ZZ_ATTRIBUTE
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ATTRIBUTE
init|=
name|zzUnpackAttribute
argument_list|()
decl_stmt|;
DECL|field|ZZ_ATTRIBUTE_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ATTRIBUTE_PACKED_0
init|=
literal|"\1\0\1\11\3\1\1\11\1\1\13\0\4\1\2\0"
operator|+
literal|"\1\1\1\0\17\1\1\0\1\1\3\0\5\1"
decl_stmt|;
DECL|method|zzUnpackAttribute
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackAttribute
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|51
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackAttribute
argument_list|(
name|ZZ_ATTRIBUTE_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackAttribute
specifier|private
specifier|static
name|int
name|zzUnpackAttribute
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/** the input device */
DECL|field|zzReader
specifier|private
name|java
operator|.
name|io
operator|.
name|Reader
name|zzReader
decl_stmt|;
comment|/** the current state of the DFA */
DECL|field|zzState
specifier|private
name|int
name|zzState
decl_stmt|;
comment|/** the current lexical state */
DECL|field|zzLexicalState
specifier|private
name|int
name|zzLexicalState
init|=
name|YYINITIAL
decl_stmt|;
comment|/** this buffer contains the current text to be matched and is       the source of the yytext() string */
DECL|field|zzBuffer
specifier|private
name|char
name|zzBuffer
index|[]
init|=
operator|new
name|char
index|[
name|ZZ_BUFFERSIZE
index|]
decl_stmt|;
comment|/** the textposition at the last accepting state */
DECL|field|zzMarkedPos
specifier|private
name|int
name|zzMarkedPos
decl_stmt|;
comment|/** the current text position in the buffer */
DECL|field|zzCurrentPos
specifier|private
name|int
name|zzCurrentPos
decl_stmt|;
comment|/** startRead marks the beginning of the yytext() string in the buffer */
DECL|field|zzStartRead
specifier|private
name|int
name|zzStartRead
decl_stmt|;
comment|/** endRead marks the last character in the buffer, that has been read       from input */
DECL|field|zzEndRead
specifier|private
name|int
name|zzEndRead
decl_stmt|;
comment|/** number of newlines encountered up to the start of the matched text */
DECL|field|yyline
specifier|private
name|int
name|yyline
decl_stmt|;
comment|/** the number of characters up to the start of the matched text */
DECL|field|yychar
specifier|private
name|int
name|yychar
decl_stmt|;
comment|/**    * the number of characters from the last newline up to the start of the     * matched text    */
DECL|field|yycolumn
specifier|private
name|int
name|yycolumn
decl_stmt|;
comment|/**     * zzAtBOL == true<=> the scanner is currently at the beginning of a line    */
DECL|field|zzAtBOL
specifier|private
name|boolean
name|zzAtBOL
init|=
literal|true
decl_stmt|;
comment|/** zzAtEOF == true<=> the scanner is at the EOF */
DECL|field|zzAtEOF
specifier|private
name|boolean
name|zzAtEOF
decl_stmt|;
comment|/** denotes if the user-EOF-code has already been executed */
DECL|field|zzEOFDone
specifier|private
name|boolean
name|zzEOFDone
decl_stmt|;
comment|/* user code: */
DECL|field|ALPHANUM
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM
init|=
name|StandardTokenizer
operator|.
name|ALPHANUM
decl_stmt|;
DECL|field|APOSTROPHE
specifier|public
specifier|static
specifier|final
name|int
name|APOSTROPHE
init|=
name|StandardTokenizer
operator|.
name|APOSTROPHE
decl_stmt|;
DECL|field|ACRONYM
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM
init|=
name|StandardTokenizer
operator|.
name|ACRONYM
decl_stmt|;
DECL|field|COMPANY
specifier|public
specifier|static
specifier|final
name|int
name|COMPANY
init|=
name|StandardTokenizer
operator|.
name|COMPANY
decl_stmt|;
DECL|field|EMAIL
specifier|public
specifier|static
specifier|final
name|int
name|EMAIL
init|=
name|StandardTokenizer
operator|.
name|EMAIL
decl_stmt|;
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|int
name|HOST
init|=
name|StandardTokenizer
operator|.
name|HOST
decl_stmt|;
DECL|field|NUM
specifier|public
specifier|static
specifier|final
name|int
name|NUM
init|=
name|StandardTokenizer
operator|.
name|NUM
decl_stmt|;
DECL|field|CJ
specifier|public
specifier|static
specifier|final
name|int
name|CJ
init|=
name|StandardTokenizer
operator|.
name|CJ
decl_stmt|;
comment|/**  * @deprecated this solves a bug where HOSTs that end with '.' are identified  *             as ACRONYMs.  */
DECL|field|ACRONYM_DEP
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM_DEP
init|=
name|StandardTokenizer
operator|.
name|ACRONYM_DEP
decl_stmt|;
DECL|field|TOKEN_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPES
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
decl_stmt|;
DECL|method|yychar
specifier|public
specifier|final
name|int
name|yychar
parameter_list|()
block|{
return|return
name|yychar
return|;
block|}
comment|/**  * Fills CharTermAttribute with the current token text.  */
DECL|method|getText
specifier|public
specifier|final
name|void
name|getText
parameter_list|(
name|CharTermAttribute
name|t
parameter_list|)
block|{
name|t
operator|.
name|copyBuffer
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzMarkedPos
operator|-
name|zzStartRead
argument_list|)
expr_stmt|;
block|}
comment|/**  * Resets the Tokenizer to a new Reader.  */
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
comment|// reset to default buffer size, if buffer has grown
if|if
condition|(
name|zzBuffer
operator|.
name|length
operator|>
name|ZZ_BUFFERSIZE
condition|)
block|{
name|zzBuffer
operator|=
operator|new
name|char
index|[
name|ZZ_BUFFERSIZE
index|]
expr_stmt|;
block|}
name|yyreset
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new scanner    * There is also a java.io.InputStream version of this constructor.    *    * @param   in  the java.io.Reader to read input from.    */
DECL|method|StandardTokenizerImplOrig
name|StandardTokenizerImplOrig
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Reader
name|in
parameter_list|)
block|{
name|this
operator|.
name|zzReader
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Creates a new scanner.    * There is also java.io.Reader version of this constructor.    *    * @param   in  the java.io.Inputstream to read input from.    */
DECL|method|StandardTokenizerImplOrig
name|StandardTokenizerImplOrig
parameter_list|(
name|java
operator|.
name|io
operator|.
name|InputStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Unpacks the compressed character translation table.    *    * @param packed   the packed character translation table    * @return         the unpacked character translation table    */
DECL|method|zzUnpackCMap
specifier|private
specifier|static
name|char
index|[]
name|zzUnpackCMap
parameter_list|(
name|String
name|packed
parameter_list|)
block|{
name|char
index|[]
name|map
init|=
operator|new
name|char
index|[
literal|0x10000
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
literal|0
decl_stmt|;
comment|/* index in unpacked array */
while|while
condition|(
name|i
operator|<
literal|1154
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|char
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|map
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|map
return|;
block|}
comment|/**    * Refills the input buffer.    *    * @return<code>false</code>, iff there was new input.    *     * @exception   java.io.IOException  if any I/O-Error occurs    */
DECL|method|zzRefill
specifier|private
name|boolean
name|zzRefill
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
comment|/* first: make room (if you can) */
if|if
condition|(
name|zzStartRead
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzBuffer
argument_list|,
literal|0
argument_list|,
name|zzEndRead
operator|-
name|zzStartRead
argument_list|)
expr_stmt|;
comment|/* translate stored positions */
name|zzEndRead
operator|-=
name|zzStartRead
expr_stmt|;
name|zzCurrentPos
operator|-=
name|zzStartRead
expr_stmt|;
name|zzMarkedPos
operator|-=
name|zzStartRead
expr_stmt|;
name|zzStartRead
operator|=
literal|0
expr_stmt|;
block|}
comment|/* is the buffer big enough? */
if|if
condition|(
name|zzCurrentPos
operator|>=
name|zzBuffer
operator|.
name|length
condition|)
block|{
comment|/* if not: blow it up */
name|char
name|newBuffer
index|[]
init|=
operator|new
name|char
index|[
name|zzCurrentPos
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|zzBuffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|zzBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|zzBuffer
operator|=
name|newBuffer
expr_stmt|;
block|}
comment|/* finally: fill the buffer with new input */
name|int
name|numRead
init|=
name|zzReader
operator|.
name|read
argument_list|(
name|zzBuffer
argument_list|,
name|zzEndRead
argument_list|,
name|zzBuffer
operator|.
name|length
operator|-
name|zzEndRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|numRead
operator|>
literal|0
condition|)
block|{
name|zzEndRead
operator|+=
name|numRead
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// unlikely but not impossible: read 0 characters, but not at end of stream
if|if
condition|(
name|numRead
operator|==
literal|0
condition|)
block|{
name|int
name|c
init|=
name|zzReader
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|zzBuffer
index|[
name|zzEndRead
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|// numRead< 0
return|return
literal|true
return|;
block|}
comment|/**    * Closes the input stream.    */
DECL|method|yyclose
specifier|public
specifier|final
name|void
name|yyclose
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|zzAtEOF
operator|=
literal|true
expr_stmt|;
comment|/* indicate end of file */
name|zzEndRead
operator|=
name|zzStartRead
expr_stmt|;
comment|/* invalidate buffer    */
if|if
condition|(
name|zzReader
operator|!=
literal|null
condition|)
name|zzReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Resets the scanner to read from a new input stream.    * Does not close the old reader.    *    * All internal variables are reset, the old input stream     *<b>cannot</b> be reused (internal buffer is discarded and lost).    * Lexical state is set to<tt>ZZ_INITIAL</tt>.    *    * @param reader   the new input stream     */
DECL|method|yyreset
specifier|public
specifier|final
name|void
name|yyreset
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Reader
name|reader
parameter_list|)
block|{
name|zzReader
operator|=
name|reader
expr_stmt|;
name|zzAtBOL
operator|=
literal|true
expr_stmt|;
name|zzAtEOF
operator|=
literal|false
expr_stmt|;
name|zzEOFDone
operator|=
literal|false
expr_stmt|;
name|zzEndRead
operator|=
name|zzStartRead
operator|=
literal|0
expr_stmt|;
name|zzCurrentPos
operator|=
name|zzMarkedPos
operator|=
literal|0
expr_stmt|;
name|yyline
operator|=
name|yychar
operator|=
name|yycolumn
operator|=
literal|0
expr_stmt|;
name|zzLexicalState
operator|=
name|YYINITIAL
expr_stmt|;
block|}
comment|/**    * Returns the current lexical state.    */
DECL|method|yystate
specifier|public
specifier|final
name|int
name|yystate
parameter_list|()
block|{
return|return
name|zzLexicalState
return|;
block|}
comment|/**    * Enters a new lexical state    *    * @param newState the new lexical state    */
DECL|method|yybegin
specifier|public
specifier|final
name|void
name|yybegin
parameter_list|(
name|int
name|newState
parameter_list|)
block|{
name|zzLexicalState
operator|=
name|newState
expr_stmt|;
block|}
comment|/**    * Returns the text matched by the current regular expression.    */
DECL|method|yytext
specifier|public
specifier|final
name|String
name|yytext
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzMarkedPos
operator|-
name|zzStartRead
argument_list|)
return|;
block|}
comment|/**    * Returns the character at position<tt>pos</tt> from the     * matched text.     *     * It is equivalent to yytext().charAt(pos), but faster    *    * @param pos the position of the character to fetch.     *            A value from 0 to yylength()-1.    *    * @return the character at position pos    */
DECL|method|yycharat
specifier|public
specifier|final
name|char
name|yycharat
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|zzBuffer
index|[
name|zzStartRead
operator|+
name|pos
index|]
return|;
block|}
comment|/**    * Returns the length of the matched text region.    */
DECL|method|yylength
specifier|public
specifier|final
name|int
name|yylength
parameter_list|()
block|{
return|return
name|zzMarkedPos
operator|-
name|zzStartRead
return|;
block|}
comment|/**    * Reports an error that occured while scanning.    *    * In a wellformed scanner (no or only correct usage of     * yypushback(int) and a match-all fallback rule) this method     * will only be called with things that "Can't Possibly Happen".    * If this method is called, something is seriously wrong    * (e.g. a JFlex bug producing a faulty scanner etc.).    *    * Usual syntax/scanner level error handling should be done    * in error fallback rules.    *    * @param   errorCode  the code of the errormessage to display    */
DECL|method|zzScanError
specifier|private
name|void
name|zzScanError
parameter_list|(
name|int
name|errorCode
parameter_list|)
block|{
name|String
name|message
decl_stmt|;
try|try
block|{
name|message
operator|=
name|ZZ_ERROR_MSG
index|[
name|errorCode
index|]
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|message
operator|=
name|ZZ_ERROR_MSG
index|[
name|ZZ_UNKNOWN_ERROR
index|]
expr_stmt|;
block|}
throw|throw
operator|new
name|Error
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|/**    * Pushes the specified amount of characters back into the input stream.    *    * They will be read again by then next call of the scanning method    *    * @param number  the number of characters to be read again.    *                This number must not be greater than yylength()!    */
DECL|method|yypushback
specifier|public
name|void
name|yypushback
parameter_list|(
name|int
name|number
parameter_list|)
block|{
if|if
condition|(
name|number
operator|>
name|yylength
argument_list|()
condition|)
name|zzScanError
argument_list|(
name|ZZ_PUSHBACK_2BIG
argument_list|)
expr_stmt|;
name|zzMarkedPos
operator|-=
name|number
expr_stmt|;
block|}
comment|/**    * Resumes scanning until the next regular expression is matched,    * the end of input is encountered or an I/O-Error occurs.    *    * @return      the next token    * @exception   java.io.IOException  if any I/O-Error occurs    */
DECL|method|getNextToken
specifier|public
name|int
name|getNextToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|int
name|zzInput
decl_stmt|;
name|int
name|zzAction
decl_stmt|;
comment|// cached fields:
name|int
name|zzCurrentPosL
decl_stmt|;
name|int
name|zzMarkedPosL
decl_stmt|;
name|int
name|zzEndReadL
init|=
name|zzEndRead
decl_stmt|;
name|char
index|[]
name|zzBufferL
init|=
name|zzBuffer
decl_stmt|;
name|char
index|[]
name|zzCMapL
init|=
name|ZZ_CMAP
decl_stmt|;
name|int
index|[]
name|zzTransL
init|=
name|ZZ_TRANS
decl_stmt|;
name|int
index|[]
name|zzRowMapL
init|=
name|ZZ_ROWMAP
decl_stmt|;
name|int
index|[]
name|zzAttrL
init|=
name|ZZ_ATTRIBUTE
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|zzMarkedPosL
operator|=
name|zzMarkedPos
expr_stmt|;
name|yychar
operator|+=
name|zzMarkedPosL
operator|-
name|zzStartRead
expr_stmt|;
name|zzAction
operator|=
operator|-
literal|1
expr_stmt|;
name|zzCurrentPosL
operator|=
name|zzCurrentPos
operator|=
name|zzStartRead
operator|=
name|zzMarkedPosL
expr_stmt|;
name|zzState
operator|=
name|ZZ_LEXSTATE
index|[
name|zzLexicalState
index|]
expr_stmt|;
name|zzForAction
label|:
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|zzCurrentPosL
operator|<
name|zzEndReadL
condition|)
name|zzInput
operator|=
name|zzBufferL
index|[
name|zzCurrentPosL
operator|++
index|]
expr_stmt|;
elseif|else
if|if
condition|(
name|zzAtEOF
condition|)
block|{
name|zzInput
operator|=
name|YYEOF
expr_stmt|;
break|break
name|zzForAction
break|;
block|}
else|else
block|{
comment|// store back cached positions
name|zzCurrentPos
operator|=
name|zzCurrentPosL
expr_stmt|;
name|zzMarkedPos
operator|=
name|zzMarkedPosL
expr_stmt|;
name|boolean
name|eof
init|=
name|zzRefill
argument_list|()
decl_stmt|;
comment|// get translated positions and possibly new buffer
name|zzCurrentPosL
operator|=
name|zzCurrentPos
expr_stmt|;
name|zzMarkedPosL
operator|=
name|zzMarkedPos
expr_stmt|;
name|zzBufferL
operator|=
name|zzBuffer
expr_stmt|;
name|zzEndReadL
operator|=
name|zzEndRead
expr_stmt|;
if|if
condition|(
name|eof
condition|)
block|{
name|zzInput
operator|=
name|YYEOF
expr_stmt|;
break|break
name|zzForAction
break|;
block|}
else|else
block|{
name|zzInput
operator|=
name|zzBufferL
index|[
name|zzCurrentPosL
operator|++
index|]
expr_stmt|;
block|}
block|}
name|int
name|zzNext
init|=
name|zzTransL
index|[
name|zzRowMapL
index|[
name|zzState
index|]
operator|+
name|zzCMapL
index|[
name|zzInput
index|]
index|]
decl_stmt|;
if|if
condition|(
name|zzNext
operator|==
operator|-
literal|1
condition|)
break|break
name|zzForAction
break|;
name|zzState
operator|=
name|zzNext
expr_stmt|;
name|int
name|zzAttributes
init|=
name|zzAttrL
index|[
name|zzState
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|zzAttributes
operator|&
literal|1
operator|)
operator|==
literal|1
condition|)
block|{
name|zzAction
operator|=
name|zzState
expr_stmt|;
name|zzMarkedPosL
operator|=
name|zzCurrentPosL
expr_stmt|;
if|if
condition|(
operator|(
name|zzAttributes
operator|&
literal|8
operator|)
operator|==
literal|8
condition|)
break|break
name|zzForAction
break|;
block|}
block|}
block|}
comment|// store back cached position
name|zzMarkedPos
operator|=
name|zzMarkedPosL
expr_stmt|;
switch|switch
condition|(
name|zzAction
operator|<
literal|0
condition|?
name|zzAction
else|:
name|ZZ_ACTION
index|[
name|zzAction
index|]
condition|)
block|{
case|case
literal|5
case|:
block|{
return|return
name|NUM
return|;
block|}
case|case
literal|11
case|:
break|break;
case|case
literal|9
case|:
block|{
return|return
name|ACRONYM
return|;
block|}
case|case
literal|12
case|:
break|break;
case|case
literal|7
case|:
block|{
return|return
name|COMPANY
return|;
block|}
case|case
literal|13
case|:
break|break;
case|case
literal|10
case|:
block|{
return|return
name|EMAIL
return|;
block|}
case|case
literal|14
case|:
break|break;
case|case
literal|1
case|:
block|{
comment|/* ignore */
block|}
case|case
literal|15
case|:
break|break;
case|case
literal|6
case|:
block|{
return|return
name|APOSTROPHE
return|;
block|}
case|case
literal|16
case|:
break|break;
case|case
literal|3
case|:
block|{
return|return
name|CJ
return|;
block|}
case|case
literal|17
case|:
break|break;
case|case
literal|8
case|:
block|{
return|return
name|ACRONYM_DEP
return|;
block|}
case|case
literal|18
case|:
break|break;
case|case
literal|2
case|:
block|{
return|return
name|ALPHANUM
return|;
block|}
case|case
literal|19
case|:
break|break;
case|case
literal|4
case|:
block|{
return|return
name|HOST
return|;
block|}
case|case
literal|20
case|:
break|break;
default|default:
if|if
condition|(
name|zzInput
operator|==
name|YYEOF
operator|&&
name|zzStartRead
operator|==
name|zzCurrentPos
condition|)
block|{
name|zzAtEOF
operator|=
literal|true
expr_stmt|;
return|return
name|YYEOF
return|;
block|}
else|else
block|{
name|zzScanError
argument_list|(
name|ZZ_NO_MATCH
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
