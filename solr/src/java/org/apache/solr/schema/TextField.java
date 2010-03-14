begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|SortField
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
name|TermQuery
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
name|document
operator|.
name|Fieldable
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
name|TermAttribute
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
name|Analyzer
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
name|response
operator|.
name|TextResponseWriter
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
name|response
operator|.
name|XMLWriter
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
name|search
operator|.
name|QParser
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
name|ArrayList
import|;
end_import
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
begin_comment
comment|/**<code>TextField</code> is the basic type for configurable text analysis.  * Analyzers for field types using this implementation should be defined in the schema.  * @version $Id$  */
end_comment
begin_class
DECL|class|TextField
specifier|public
class|class
name|TextField
extends|extends
name|CompressableField
block|{
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|properties
operator||=
name|TOKENIZED
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|getVersion
argument_list|()
operator|>
literal|1.1f
condition|)
name|properties
operator|&=
operator|~
name|OMIT_TF_POSITIONS
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|reverse
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
return|return
name|parseFieldQuery
argument_list|(
name|parser
argument_list|,
name|getQueryAnalyzer
argument_list|()
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|externalVal
argument_list|)
return|;
block|}
DECL|method|parseFieldQuery
specifier|static
name|Query
name|parseFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|queryText
parameter_list|)
block|{
name|int
name|phraseSlop
init|=
literal|0
decl_stmt|;
name|boolean
name|enablePositionIncrements
init|=
literal|true
decl_stmt|;
comment|// most of the following code is taken from the Lucene QueryParser
comment|// Use the analyzer to get all the tokens, and then build a TermQuery,
comment|// PhraseQuery, or nothing based on the term count
name|TokenStream
name|source
decl_stmt|;
try|try
block|{
name|source
operator|=
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|queryText
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|source
operator|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|StringReader
argument_list|(
name|queryText
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CachingTokenFilter
name|buffer
init|=
operator|new
name|CachingTokenFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
literal|null
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
literal|null
decl_stmt|;
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// success==false if we hit an exception
block|}
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|buffer
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|buffer
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|positionCount
init|=
literal|0
decl_stmt|;
name|boolean
name|severalTokensAtSamePosition
init|=
literal|false
decl_stmt|;
name|boolean
name|hasMoreTokens
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|termAtt
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|hasMoreTokens
operator|=
name|buffer
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
while|while
condition|(
name|hasMoreTokens
condition|)
block|{
name|numTokens
operator|++
expr_stmt|;
name|int
name|positionIncrement
init|=
operator|(
name|posIncrAtt
operator|!=
literal|null
operator|)
condition|?
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|1
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
name|severalTokensAtSamePosition
operator|=
literal|true
expr_stmt|;
block|}
name|hasMoreTokens
operator|=
name|buffer
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
try|try
block|{
comment|// rewind the buffer stream
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// close original stream - all tokens buffered
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|numTokens
operator|==
literal|0
condition|)
return|return
literal|null
return|;
elseif|else
if|if
condition|(
name|numTokens
operator|==
literal|1
condition|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
comment|// return newTermQuery(new Term(field, term));
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|severalTokensAtSamePosition
condition|)
block|{
if|if
condition|(
name|positionCount
operator|==
literal|1
condition|)
block|{
comment|// no phrase query:
comment|// BooleanQuery q = newBooleanQuery(true);
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|term
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
comment|// Query currentQuery = newTermQuery(new Term(field, term));
name|Query
name|currentQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|currentQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
else|else
block|{
comment|// phrase query:
comment|// MultiPhraseQuery mpq = newMultiPhraseQuery();
name|MultiPhraseQuery
name|mpq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|mpq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|List
name|multiTerms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
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
operator|(
name|Term
index|[]
operator|)
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
operator|(
name|Term
index|[]
operator|)
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
name|term
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
operator|(
name|Term
index|[]
operator|)
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
operator|(
name|Term
index|[]
operator|)
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
block|}
else|else
block|{
comment|// PhraseQuery pq = newPhraseQuery();
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
literal|null
decl_stmt|;
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
name|term
operator|=
name|termAtt
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|position
operator|+=
name|positionIncrement
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pq
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
