begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|CachingTokenFilter
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|TermToBytesRefAttribute
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
name|BooleanClause
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
name|BooleanQuery
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
name|search
operator|.
name|SynonymQuery
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
name|TermQuery
import|;
end_import
begin_comment
comment|/**  * Creates queries from the {@link Analyzer} chain.  *<p>  * Example usage:  *<pre class="prettyprint">  *   QueryBuilder builder = new QueryBuilder(analyzer);  *   Query a = builder.createBooleanQuery("body", "just a test");  *   Query b = builder.createPhraseQuery("body", "another test");  *   Query c = builder.createMinShouldMatchQuery("body", "another test", 0.5f);  *</pre>  *<p>  * This can also be used as a subclass for query parsers to make it easier  * to interact with the analysis chain. Factory methods such as {@code newTermQuery}   * are provided so that the generated queries can be customized.  */
end_comment
begin_class
DECL|class|QueryBuilder
specifier|public
class|class
name|QueryBuilder
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
init|=
literal|true
decl_stmt|;
comment|/** Creates a new QueryBuilder using the given analyzer. */
DECL|method|QueryBuilder
specifier|public
name|QueryBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**     * Creates a boolean query from the query text.    *<p>    * This is equivalent to {@code createBooleanQuery(field, queryText, Occur.SHOULD)}    * @param field field name    * @param queryText text to be passed to the analyzer    * @return {@code TermQuery} or {@code BooleanQuery}, based on the analysis    *         of {@code queryText}    */
DECL|method|createBooleanQuery
specifier|public
name|Query
name|createBooleanQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
block|{
return|return
name|createBooleanQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
return|;
block|}
comment|/**     * Creates a boolean query from the query text.    *<p>    * @param field field name    * @param queryText text to be passed to the analyzer    * @param operator operator used for clauses between analyzer tokens.    * @return {@code TermQuery} or {@code BooleanQuery}, based on the analysis     *         of {@code queryText}    */
DECL|method|createBooleanQuery
specifier|public
name|Query
name|createBooleanQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|operator
parameter_list|)
block|{
if|if
condition|(
name|operator
operator|!=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
operator|&&
name|operator
operator|!=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid operator: only SHOULD or MUST are allowed"
argument_list|)
throw|;
block|}
return|return
name|createFieldQuery
argument_list|(
name|analyzer
argument_list|,
name|operator
argument_list|,
name|field
argument_list|,
name|queryText
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**     * Creates a phrase query from the query text.    *<p>    * This is equivalent to {@code createPhraseQuery(field, queryText, 0)}    * @param field field name    * @param queryText text to be passed to the analyzer    * @return {@code TermQuery}, {@code BooleanQuery}, {@code PhraseQuery}, or    *         {@code MultiPhraseQuery}, based on the analysis of {@code queryText}    */
DECL|method|createPhraseQuery
specifier|public
name|Query
name|createPhraseQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
block|{
return|return
name|createPhraseQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**     * Creates a phrase query from the query text.    *<p>    * @param field field name    * @param queryText text to be passed to the analyzer    * @param phraseSlop number of other words permitted between words in query phrase    * @return {@code TermQuery}, {@code BooleanQuery}, {@code PhraseQuery}, or    *         {@code MultiPhraseQuery}, based on the analysis of {@code queryText}    */
DECL|method|createPhraseQuery
specifier|public
name|Query
name|createPhraseQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|int
name|phraseSlop
parameter_list|)
block|{
return|return
name|createFieldQuery
argument_list|(
name|analyzer
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|,
name|field
argument_list|,
name|queryText
argument_list|,
literal|true
argument_list|,
name|phraseSlop
argument_list|)
return|;
block|}
comment|/**     * Creates a minimum-should-match query from the query text.    *<p>    * @param field field name    * @param queryText text to be passed to the analyzer    * @param fraction of query terms {@code [0..1]} that should match     * @return {@code TermQuery} or {@code BooleanQuery}, based on the analysis     *         of {@code queryText}    */
DECL|method|createMinShouldMatchQuery
specifier|public
name|Query
name|createMinShouldMatchQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|float
name|fraction
parameter_list|)
block|{
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|fraction
argument_list|)
operator|||
name|fraction
argument_list|<
literal|0
operator|||
name|fraction
argument_list|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fraction should be>= 0 and<= 1"
argument_list|)
throw|;
block|}
comment|// TODO: wierd that BQ equals/rewrite/scorer doesn't handle this?
if|if
condition|(
name|fraction
operator|==
literal|1
condition|)
block|{
return|return
name|createBooleanQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
return|;
block|}
name|Query
name|query
init|=
name|createFieldQuery
argument_list|(
name|analyzer
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|,
name|field
argument_list|,
name|queryText
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setDisableCoord
argument_list|(
name|bq
operator|.
name|isCoordDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
call|(
name|int
call|)
argument_list|(
name|fraction
operator|*
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
comment|/**     * Returns the analyzer.     * @see #setAnalyzer(Analyzer)    */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**     * Sets the analyzer used to tokenize text.    */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**    * Returns true if position increments are enabled.    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
comment|/**    * Set to<code>true</code> to enable position increments in result query.    *<p>    * When set, result phrase and multi-phrase queries will    * be aware of position increments.    * Useful when e.g. a StopFilter increases the position increment of    * the token that follows an omitted token.    *<p>    * Default: true.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enable
expr_stmt|;
block|}
comment|/**    * Creates a query from the analysis chain.    *<p>    * Expert: this is more useful for subclasses such as queryparsers.     * If using this class directly, just use {@link #createBooleanQuery(String, String)}    * and {@link #createPhraseQuery(String, String)}    * @param analyzer analyzer used for this query    * @param operator default boolean operator used for this query    * @param field field to create queries against    * @param queryText text to be passed to the analysis chain    * @param quoted true if phrases should be generated when terms occur at more than one position    * @param phraseSlop slop factor for phrase/multiphrase queries    */
DECL|method|createFieldQuery
specifier|protected
specifier|final
name|Query
name|createFieldQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|operator
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|,
name|boolean
name|quoted
parameter_list|,
name|int
name|phraseSlop
parameter_list|)
block|{
assert|assert
name|operator
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
operator|||
name|operator
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
assert|;
comment|// Use the analyzer to get all the tokens, and then build an appropriate
comment|// query based on the analysis chain.
try|try
init|(
name|TokenStream
name|source
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
init|;
name|CachingTokenFilter
name|stream
operator|=
operator|new
name|CachingTokenFilter
argument_list|(
name|source
argument_list|)
init|)
block|{
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|termAtt
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// phase 1: read through the stream and assess the situation:
comment|// counting the number of tokens/positions and marking if we have any synonyms.
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
name|int
name|positionCount
init|=
literal|0
decl_stmt|;
name|boolean
name|hasSynonyms
init|=
literal|false
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|numTokens
operator|++
expr_stmt|;
name|int
name|positionIncrement
init|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|positionIncrement
operator|!=
literal|0
condition|)
block|{
name|positionCount
operator|+=
name|positionIncrement
expr_stmt|;
block|}
else|else
block|{
name|hasSynonyms
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// phase 2: based on token count, presence of synonyms, and options
comment|// formulate a single term, boolean, or phrase.
if|if
condition|(
name|numTokens
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|numTokens
operator|==
literal|1
condition|)
block|{
comment|// single term
return|return
name|analyzeTerm
argument_list|(
name|field
argument_list|,
name|stream
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|quoted
operator|&&
name|positionCount
operator|>
literal|1
condition|)
block|{
comment|// phrase
if|if
condition|(
name|hasSynonyms
condition|)
block|{
comment|// complex phrase with synonyms
return|return
name|analyzeMultiPhrase
argument_list|(
name|field
argument_list|,
name|stream
argument_list|,
name|phraseSlop
argument_list|)
return|;
block|}
else|else
block|{
comment|// simple phrase
return|return
name|analyzePhrase
argument_list|(
name|field
argument_list|,
name|stream
argument_list|,
name|phraseSlop
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// boolean
if|if
condition|(
name|positionCount
operator|==
literal|1
condition|)
block|{
comment|// only one position, with synonyms
return|return
name|analyzeBoolean
argument_list|(
name|field
argument_list|,
name|stream
argument_list|)
return|;
block|}
else|else
block|{
comment|// complex case: multiple positions
return|return
name|analyzeMultiBoolean
argument_list|(
name|field
argument_list|,
name|stream
argument_list|,
name|operator
argument_list|)
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error analyzing query text"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**     * Creates simple term query from the cached tokenstream contents     */
DECL|method|analyzeTerm
specifier|private
name|Query
name|analyzeTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
name|newTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * Creates simple boolean query from the cached tokenstream contents     */
DECL|method|analyzeBoolean
specifier|private
name|Query
name|analyzeBoolean
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newSynonymQuery
argument_list|(
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|BooleanQuery
operator|.
name|Builder
name|q
parameter_list|,
name|List
argument_list|<
name|Term
argument_list|>
name|current
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|operator
parameter_list|)
block|{
if|if
condition|(
name|current
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|current
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|q
operator|.
name|add
argument_list|(
name|newTermQuery
argument_list|(
name|current
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|operator
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|q
operator|.
name|add
argument_list|(
name|newSynonymQuery
argument_list|(
name|current
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|current
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
name|operator
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Creates complex boolean query from the cached tokenstream contents     */
DECL|method|analyzeMultiBoolean
specifier|private
name|Query
name|analyzeMultiBoolean
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|operator
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanQuery
operator|.
name|Builder
name|q
init|=
name|newBooleanQuery
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|currentQuery
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|add
argument_list|(
name|q
argument_list|,
name|currentQuery
argument_list|,
name|operator
argument_list|)
expr_stmt|;
name|currentQuery
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|currentQuery
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|add
argument_list|(
name|q
argument_list|,
name|currentQuery
argument_list|,
name|operator
argument_list|)
expr_stmt|;
return|return
name|q
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**     * Creates simple phrase query from the cached tokenstream contents     */
DECL|method|analyzePhrase
specifier|private
name|Query
name|analyzePhrase
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|int
name|slop
parameter_list|)
throws|throws
name|IOException
block|{
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|position
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|position
operator|+=
literal|1
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**     * Creates complex phrase query from the cached tokenstream contents     */
DECL|method|analyzeMultiPhrase
specifier|private
name|Query
name|analyzeMultiPhrase
parameter_list|(
name|String
name|field
parameter_list|,
name|TokenStream
name|stream
parameter_list|,
name|int
name|slop
parameter_list|)
throws|throws
name|IOException
block|{
name|MultiPhraseQuery
name|mpq
init|=
name|newMultiPhraseQuery
argument_list|()
decl_stmt|;
name|mpq
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|stream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|multiTerms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|int
name|positionIncrement
init|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|positionIncrement
operator|>
literal|0
operator|&&
name|multiTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|multiTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|position
operator|+=
name|positionIncrement
expr_stmt|;
name|multiTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|termAtt
operator|.
name|getBytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|mpq
return|;
block|}
comment|/**    * Builds a new BooleanQuery instance.    *<p>    * This is intended for subclasses that wish to customize the generated queries.    * @return new BooleanQuery instance    */
DECL|method|newBooleanQuery
specifier|protected
name|BooleanQuery
operator|.
name|Builder
name|newBooleanQuery
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Builds a new SynonymQuery instance.    *<p>    * This is intended for subclasses that wish to customize the generated queries.    * @return new Query instance    */
DECL|method|newSynonymQuery
specifier|protected
name|Query
name|newSynonymQuery
parameter_list|(
name|Term
name|terms
index|[]
parameter_list|)
block|{
return|return
operator|new
name|SynonymQuery
argument_list|(
name|terms
argument_list|)
return|;
block|}
comment|/**    * Builds a new TermQuery instance.    *<p>    * This is intended for subclasses that wish to customize the generated queries.    * @param term term    * @return new TermQuery instance    */
DECL|method|newTermQuery
specifier|protected
name|Query
name|newTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
comment|/**    * Builds a new MultiPhraseQuery instance.    *<p>    * This is intended for subclasses that wish to customize the generated queries.    * @return new MultiPhraseQuery instance    */
DECL|method|newMultiPhraseQuery
specifier|protected
name|MultiPhraseQuery
name|newMultiPhraseQuery
parameter_list|()
block|{
return|return
operator|new
name|MultiPhraseQuery
argument_list|()
return|;
block|}
block|}
end_class
end_unit
