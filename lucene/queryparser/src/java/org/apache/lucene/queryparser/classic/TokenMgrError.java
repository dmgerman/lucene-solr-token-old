begin_unit
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. TokenMgrError.java Version 5.0 */
end_comment
begin_comment
comment|/* JavaCCOptions: */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.classic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
package|;
end_package
begin_comment
comment|/** Token Manager Error. */
end_comment
begin_class
DECL|class|TokenMgrError
specifier|public
class|class
name|TokenMgrError
extends|extends
name|Error
block|{
comment|/**    * The version identifier for this Serializable class.    * Increment only if the<i>serialized</i> form of the    * class changes.    */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
comment|/*    * Ordinals for various reasons why an Error of this type can be thrown.    */
comment|/**    * Lexical error occurred.    */
DECL|field|LEXICAL_ERROR
specifier|static
specifier|final
name|int
name|LEXICAL_ERROR
init|=
literal|0
decl_stmt|;
comment|/**    * An attempt was made to create a second instance of a static token manager.    */
DECL|field|STATIC_LEXER_ERROR
specifier|static
specifier|final
name|int
name|STATIC_LEXER_ERROR
init|=
literal|1
decl_stmt|;
comment|/**    * Tried to change to an invalid lexical state.    */
DECL|field|INVALID_LEXICAL_STATE
specifier|static
specifier|final
name|int
name|INVALID_LEXICAL_STATE
init|=
literal|2
decl_stmt|;
comment|/**    * Detected (and bailed out of) an infinite loop in the token manager.    */
DECL|field|LOOP_DETECTED
specifier|static
specifier|final
name|int
name|LOOP_DETECTED
init|=
literal|3
decl_stmt|;
comment|/**    * Indicates the reason why the exception is thrown. It will have    * one of the above 4 values.    */
DECL|field|errorCode
name|int
name|errorCode
decl_stmt|;
comment|/**    * Replaces unprintable characters by their escaped (or unicode escaped)    * equivalents in the given string    */
DECL|method|addEscapes
specifier|protected
specifier|static
specifier|final
name|String
name|addEscapes
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringBuffer
name|retval
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|char
name|ch
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
continue|continue;
case|case
literal|'\b'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\b"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\t'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\t"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\n'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\n"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\f'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\f"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\r'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\r"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\"'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\\""
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\''
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\\'"
argument_list|)
expr_stmt|;
continue|continue;
case|case
literal|'\\'
case|:
name|retval
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
continue|continue;
default|default:
if|if
condition|(
operator|(
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|)
operator|<
literal|0x20
operator|||
name|ch
operator|>
literal|0x7e
condition|)
block|{
name|String
name|s
init|=
literal|"0000"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|ch
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|retval
operator|.
name|append
argument_list|(
literal|"\\u"
operator|+
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|retval
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
block|}
return|return
name|retval
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns a detailed message for the Error when it is thrown by the    * token manager to indicate a lexical error.    * Parameters :    *    EOFSeen     : indicates if EOF caused the lexical error    *    curLexState : lexical state in which this error occurred    *    errorLine   : line number when the error occurred    *    errorColumn : column number when the error occurred    *    errorAfter  : prefix that was seen before this error occurred    *    curchar     : the offending character    * Note: You can customize the lexical error message by modifying this method.    */
DECL|method|LexicalError
specifier|protected
specifier|static
name|String
name|LexicalError
parameter_list|(
name|boolean
name|EOFSeen
parameter_list|,
name|int
name|lexState
parameter_list|,
name|int
name|errorLine
parameter_list|,
name|int
name|errorColumn
parameter_list|,
name|String
name|errorAfter
parameter_list|,
name|char
name|curChar
parameter_list|)
block|{
return|return
operator|(
literal|"Lexical error at line "
operator|+
name|errorLine
operator|+
literal|", column "
operator|+
name|errorColumn
operator|+
literal|".  Encountered: "
operator|+
operator|(
name|EOFSeen
condition|?
literal|"<EOF> "
else|:
operator|(
literal|"\""
operator|+
name|addEscapes
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|curChar
argument_list|)
argument_list|)
operator|+
literal|"\""
operator|)
operator|+
literal|" ("
operator|+
operator|(
name|int
operator|)
name|curChar
operator|+
literal|"), "
operator|)
operator|+
literal|"after : \""
operator|+
name|addEscapes
argument_list|(
name|errorAfter
argument_list|)
operator|+
literal|"\""
operator|)
return|;
block|}
comment|/**    * You can also modify the body of this method to customize your error messages.    * For example, cases like LOOP_DETECTED and INVALID_LEXICAL_STATE are not    * of end-users concern, so you can return something like :    *    *     "Internal Error : Please file a bug report .... "    *    * from this method for such cases in the release version of your parser.    */
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|super
operator|.
name|getMessage
argument_list|()
return|;
block|}
comment|/*    * Constructors of various flavors follow.    */
comment|/** No arg constructor. */
DECL|method|TokenMgrError
specifier|public
name|TokenMgrError
parameter_list|()
block|{   }
comment|/** Constructor with message and reason. */
DECL|method|TokenMgrError
specifier|public
name|TokenMgrError
parameter_list|(
name|String
name|message
parameter_list|,
name|int
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|errorCode
operator|=
name|reason
expr_stmt|;
block|}
comment|/** Full Constructor. */
DECL|method|TokenMgrError
specifier|public
name|TokenMgrError
parameter_list|(
name|boolean
name|EOFSeen
parameter_list|,
name|int
name|lexState
parameter_list|,
name|int
name|errorLine
parameter_list|,
name|int
name|errorColumn
parameter_list|,
name|String
name|errorAfter
parameter_list|,
name|char
name|curChar
parameter_list|,
name|int
name|reason
parameter_list|)
block|{
name|this
argument_list|(
name|LexicalError
argument_list|(
name|EOFSeen
argument_list|,
name|lexState
argument_list|,
name|errorLine
argument_list|,
name|errorColumn
argument_list|,
name|errorAfter
argument_list|,
name|curChar
argument_list|)
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|/* JavaCC - OriginalChecksum=f433e1a52b8eadbf12f3fbbbf87fd140 (do not edit this line) */
end_comment
end_unit
