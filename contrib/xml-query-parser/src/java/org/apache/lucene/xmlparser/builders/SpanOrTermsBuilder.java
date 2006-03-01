begin_unit
begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Analyzer
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
name|Token
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
name|TokenStream
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|spans
operator|.
name|SpanOrQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|xmlparser
operator|.
name|DOMUtils
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
name|xmlparser
operator|.
name|ParserException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_class
DECL|class|SpanOrTermsBuilder
specifier|public
class|class
name|SpanOrTermsBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
decl_stmt|;
comment|/**      * @param analyzer      */
DECL|method|SpanOrTermsBuilder
specifier|public
name|SpanOrTermsBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritanceOrFail
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|DOMUtils
operator|.
name|getNonBlankTextOrFail
argument_list|(
name|e
argument_list|)
decl_stmt|;
try|try
block|{
name|ArrayList
name|clausesList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
operator|new
name|StringReader
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
name|ts
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|token
operator|!=
literal|null
condition|)
block|{
name|SpanTermQuery
name|stq
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|clausesList
operator|.
name|add
argument_list|(
name|stq
argument_list|)
expr_stmt|;
name|token
operator|=
name|ts
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|SpanOrQuery
name|soq
init|=
operator|new
name|SpanOrQuery
argument_list|(
operator|(
name|SpanQuery
index|[]
operator|)
name|clausesList
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|clausesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|soq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|soq
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"IOException parsing value:"
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
