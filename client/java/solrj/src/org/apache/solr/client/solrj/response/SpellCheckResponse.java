begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Encapsulates responses from SpellCheckComponent  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SpellCheckResponse
specifier|public
class|class
name|SpellCheckResponse
block|{
DECL|field|correctlySpelled
specifier|private
name|boolean
name|correctlySpelled
decl_stmt|;
DECL|field|collation
specifier|private
name|String
name|collation
decl_stmt|;
DECL|field|suggestions
specifier|private
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestions
init|=
operator|new
name|ArrayList
argument_list|<
name|Suggestion
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|suggestionMap
name|Map
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
name|suggestionMap
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SpellCheckResponse
specifier|public
name|SpellCheckResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|spellInfo
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|sugg
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|spellInfo
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sugg
operator|==
literal|null
condition|)
block|{
name|correctlySpelled
operator|=
literal|true
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sugg
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|sugg
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"correctlySpelled"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|correctlySpelled
operator|=
operator|(
name|Boolean
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"collation"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|collation
operator|=
operator|(
name|String
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Suggestion
name|s
init|=
operator|new
name|Suggestion
argument_list|(
name|n
argument_list|,
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|suggestionMap
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isCorrectlySpelled
specifier|public
name|boolean
name|isCorrectlySpelled
parameter_list|()
block|{
return|return
name|correctlySpelled
return|;
block|}
DECL|method|getSuggestions
specifier|public
name|List
argument_list|<
name|Suggestion
argument_list|>
name|getSuggestions
parameter_list|()
block|{
return|return
name|suggestions
return|;
block|}
DECL|method|getSuggestionMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
name|getSuggestionMap
parameter_list|()
block|{
return|return
name|suggestionMap
return|;
block|}
DECL|method|getSuggestion
specifier|public
name|Suggestion
name|getSuggestion
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|suggestionMap
operator|.
name|get
argument_list|(
name|token
argument_list|)
return|;
block|}
DECL|method|getFirstSuggestion
specifier|public
name|String
name|getFirstSuggestion
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|Suggestion
name|s
init|=
name|suggestionMap
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
return|return
name|s
operator|.
name|getSuggestions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|s
operator|.
name|getSuggestions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getCollatedResult
specifier|public
name|String
name|getCollatedResult
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
DECL|class|Suggestion
specifier|public
specifier|static
class|class
name|Suggestion
block|{
DECL|field|token
specifier|private
name|String
name|token
decl_stmt|;
DECL|field|numFound
specifier|private
name|int
name|numFound
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|field|originalFrequency
specifier|private
name|int
name|originalFrequency
decl_stmt|;
DECL|field|suggestions
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|suggestions
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|suggestionFrequencies
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|suggestionFrequencies
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Suggestion
specifier|public
name|Suggestion
parameter_list|(
name|String
name|token
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|suggestion
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|suggestion
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|suggestion
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"numFound"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|numFound
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"startOffset"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|startOffset
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"endOffset"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|endOffset
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"origFreq"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|originalFrequency
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"suggestion"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|Object
name|o
init|=
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|o
decl_stmt|;
name|suggestions
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|SimpleOrderedMap
condition|)
block|{
name|SimpleOrderedMap
name|map
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|o
decl_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"word"
argument_list|)
argument_list|)
expr_stmt|;
name|suggestionFrequencies
operator|.
name|add
argument_list|(
operator|(
name|Integer
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"frequency"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getToken
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
DECL|method|getNumFound
specifier|public
name|int
name|getNumFound
parameter_list|()
block|{
return|return
name|numFound
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
DECL|method|getOriginalFrequency
specifier|public
name|int
name|getOriginalFrequency
parameter_list|()
block|{
return|return
name|originalFrequency
return|;
block|}
DECL|method|getSuggestions
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSuggestions
parameter_list|()
block|{
return|return
name|suggestions
return|;
block|}
DECL|method|getSuggestionFrequencies
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getSuggestionFrequencies
parameter_list|()
block|{
return|return
name|suggestionFrequencies
return|;
block|}
block|}
block|}
end_class
end_unit
