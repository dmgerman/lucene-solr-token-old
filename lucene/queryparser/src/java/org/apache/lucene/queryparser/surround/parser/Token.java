begin_unit
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. Token.java Version 5.0 */
end_comment
begin_comment
comment|/* JavaCCOptions:TOKEN_EXTENDS=,KEEP_LINE_COL=null,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.surround.parser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|parser
package|;
end_package
begin_comment
comment|/**  * Describes the input token stream.  */
end_comment
begin_class
DECL|class|Token
specifier|public
class|class
name|Token
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
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
comment|/**    * An integer that describes the kind of this token.  This numbering    * system is determined by JavaCCParser, and a table of these numbers is    * stored in the file ...Constants.java.    */
DECL|field|kind
specifier|public
name|int
name|kind
decl_stmt|;
comment|/** The line number of the first character of this Token. */
DECL|field|beginLine
specifier|public
name|int
name|beginLine
decl_stmt|;
comment|/** The column number of the first character of this Token. */
DECL|field|beginColumn
specifier|public
name|int
name|beginColumn
decl_stmt|;
comment|/** The line number of the last character of this Token. */
DECL|field|endLine
specifier|public
name|int
name|endLine
decl_stmt|;
comment|/** The column number of the last character of this Token. */
DECL|field|endColumn
specifier|public
name|int
name|endColumn
decl_stmt|;
comment|/**    * The string image of the token.    */
DECL|field|image
specifier|public
name|String
name|image
decl_stmt|;
comment|/**    * A reference to the next regular (non-special) token from the input    * stream.  If this is the last token from the input stream, or if the    * token manager has not read tokens beyond this one, this field is    * set to null.  This is true only if this token is also a regular    * token.  Otherwise, see below for a description of the contents of    * this field.    */
DECL|field|next
specifier|public
name|Token
name|next
decl_stmt|;
comment|/**    * This field is used to access special tokens that occur prior to this    * token, but after the immediately preceding regular (non-special) token.    * If there are no such special tokens, this field is set to null.    * When there are more than one such special token, this field refers    * to the last of these special tokens, which in turn refers to the next    * previous special token through its specialToken field, and so on    * until the first special token (whose specialToken field is null).    * The next fields of special tokens refer to other special tokens that    * immediately follow it (without an intervening regular token).  If there    * is no such token, this field is null.    */
DECL|field|specialToken
specifier|public
name|Token
name|specialToken
decl_stmt|;
comment|/**    * An optional attribute value of the Token.    * Tokens which are not used as syntactic sugar will often contain    * meaningful values that will be used later on by the compiler or    * interpreter. This attribute value is often different from the image.    * Any subclass of Token that actually wants to return a non-null value can    * override this method as appropriate.    */
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * No-argument constructor    */
DECL|method|Token
specifier|public
name|Token
parameter_list|()
block|{}
comment|/**    * Constructs a new token for the specified Image.    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|kind
parameter_list|)
block|{
name|this
argument_list|(
name|kind
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new token for the specified Image and Kind.    */
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|kind
parameter_list|,
name|String
name|image
parameter_list|)
block|{
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
name|this
operator|.
name|image
operator|=
name|image
expr_stmt|;
block|}
comment|/**    * Returns the image.    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|image
return|;
block|}
comment|/**    * Returns a new Token object, by default. However, if you want, you    * can create and return subclass objects based on the value of ofKind.    * Simply add the cases to the switch for all those special cases.    * For example, if you have a subclass of Token called IDToken that    * you want to create if ofKind is ID, simply add something like :    *    *    case MyParserConstants.ID : return new IDToken(ofKind, image);    *    * to the following switch statement. Then you can cast matchedToken    * variable to the appropriate type and use sit in your lexical actions.    */
DECL|method|newToken
specifier|public
specifier|static
name|Token
name|newToken
parameter_list|(
name|int
name|ofKind
parameter_list|,
name|String
name|image
parameter_list|)
block|{
switch|switch
condition|(
name|ofKind
condition|)
block|{
default|default :
return|return
operator|new
name|Token
argument_list|(
name|ofKind
argument_list|,
name|image
argument_list|)
return|;
block|}
block|}
DECL|method|newToken
specifier|public
specifier|static
name|Token
name|newToken
parameter_list|(
name|int
name|ofKind
parameter_list|)
block|{
return|return
name|newToken
argument_list|(
name|ofKind
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|/* JavaCC - OriginalChecksum=f2df701e24da1cf2d025118ce6efdd2f (do not edit this line) */
end_comment
end_unit
