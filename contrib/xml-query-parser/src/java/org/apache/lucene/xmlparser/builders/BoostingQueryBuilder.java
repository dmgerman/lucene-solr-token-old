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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BoostingQuery
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
name|Query
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
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|QueryBuilder
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
DECL|class|BoostingQueryBuilder
specifier|public
class|class
name|BoostingQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|factory
specifier|private
name|QueryBuilder
name|factory
decl_stmt|;
DECL|field|defaultBoost
name|float
name|defaultBoost
init|=
literal|0.01f
decl_stmt|;
DECL|method|BoostingQueryBuilder
specifier|public
name|BoostingQueryBuilder
parameter_list|(
name|QueryBuilder
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|Element
name|mainQueryElem
init|=
name|DOMUtils
operator|.
name|getChildByTagName
argument_list|(
name|e
argument_list|,
literal|"Query"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainQueryElem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"BoostingQuery missing a \"Query\" child element"
argument_list|)
throw|;
block|}
name|mainQueryElem
operator|=
name|DOMUtils
operator|.
name|getFirstChildElement
argument_list|(
name|mainQueryElem
argument_list|)
expr_stmt|;
if|if
condition|(
name|mainQueryElem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"BoostingQuery \"Query\" element missing a child element"
argument_list|)
throw|;
block|}
name|Query
name|mainQuery
init|=
name|factory
operator|.
name|getQuery
argument_list|(
name|mainQueryElem
argument_list|)
decl_stmt|;
name|Element
name|boostQueryElem
init|=
name|DOMUtils
operator|.
name|getChildByTagName
argument_list|(
name|e
argument_list|,
literal|"BoostQuery"
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|boostQueryElem
argument_list|,
literal|"boost"
argument_list|,
name|defaultBoost
argument_list|)
decl_stmt|;
if|if
condition|(
name|boostQueryElem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"BoostingQuery missing a \"BoostQuery\" child element"
argument_list|)
throw|;
block|}
name|boostQueryElem
operator|=
name|DOMUtils
operator|.
name|getFirstChildElement
argument_list|(
name|boostQueryElem
argument_list|)
expr_stmt|;
if|if
condition|(
name|boostQueryElem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"BoostingQuery \"BoostQuery\" element missing a child element"
argument_list|)
throw|;
block|}
name|Query
name|boostQuery
init|=
name|factory
operator|.
name|getQuery
argument_list|(
name|boostQueryElem
argument_list|)
decl_stmt|;
name|BoostingQuery
name|bq
init|=
operator|new
name|BoostingQuery
argument_list|(
name|mainQuery
argument_list|,
name|boostQuery
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|bq
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
name|bq
return|;
block|}
block|}
end_class
end_unit
