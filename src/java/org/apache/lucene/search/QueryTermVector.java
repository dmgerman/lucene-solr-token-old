begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|IndexReader
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
name|TermFreqVector
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|QueryTermVector
specifier|public
class|class
name|QueryTermVector
implements|implements
name|TermFreqVector
block|{
DECL|field|terms
specifier|private
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
DECL|field|termFreqs
specifier|private
name|int
index|[]
name|termFreqs
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    *     * @param queryTerms The original list of terms from the query, can contain duplicates    */
DECL|method|QueryTermVector
specifier|public
name|QueryTermVector
parameter_list|(
name|String
index|[]
name|queryTerms
parameter_list|)
block|{
name|processTerms
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
block|}
DECL|method|QueryTermVector
specifier|public
name|QueryTermVector
parameter_list|(
name|String
name|queryString
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|queryString
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|Token
name|next
init|=
literal|null
decl_stmt|;
name|List
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|next
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|next
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|processTerms
argument_list|(
operator|(
name|String
index|[]
operator|)
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
block|}
DECL|method|processTerms
specifier|private
name|void
name|processTerms
parameter_list|(
name|String
index|[]
name|queryTerms
parameter_list|)
block|{
if|if
condition|(
name|queryTerms
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
name|Map
name|tmpSet
init|=
operator|new
name|HashMap
argument_list|(
name|queryTerms
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//filter out duplicates
name|List
name|tmpList
init|=
operator|new
name|ArrayList
argument_list|(
name|queryTerms
operator|.
name|length
argument_list|)
decl_stmt|;
name|List
name|tmpFreqs
init|=
operator|new
name|ArrayList
argument_list|(
name|queryTerms
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
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
name|queryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|queryTerms
index|[
name|i
index|]
decl_stmt|;
name|Integer
name|position
init|=
operator|(
name|Integer
operator|)
name|tmpSet
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|position
operator|==
literal|null
condition|)
block|{
name|tmpSet
operator|.
name|put
argument_list|(
name|term
argument_list|,
operator|new
name|Integer
argument_list|(
name|j
operator|++
argument_list|)
argument_list|)
expr_stmt|;
name|tmpList
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|tmpFreqs
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Integer
name|integer
init|=
operator|(
name|Integer
operator|)
name|tmpFreqs
operator|.
name|get
argument_list|(
name|position
operator|.
name|intValue
argument_list|()
argument_list|)
decl_stmt|;
name|tmpFreqs
operator|.
name|set
argument_list|(
name|position
operator|.
name|intValue
argument_list|()
argument_list|,
operator|new
name|Integer
argument_list|(
name|integer
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|terms
operator|=
operator|(
name|String
index|[]
operator|)
name|tmpList
operator|.
name|toArray
argument_list|(
name|terms
argument_list|)
expr_stmt|;
comment|//termFreqs = (int[])tmpFreqs.toArray(termFreqs);
name|termFreqs
operator|=
operator|new
name|int
index|[
name|tmpFreqs
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|tmpFreqs
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Integer
name|integer
init|=
operator|(
name|Integer
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|termFreqs
index|[
name|i
operator|++
index|]
operator|=
name|integer
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|termFreqs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|terms
operator|.
name|length
return|;
block|}
DECL|method|getTerms
specifier|public
name|String
index|[]
name|getTerms
parameter_list|()
block|{
return|return
name|terms
return|;
block|}
DECL|method|getTermFrequencies
specifier|public
name|int
index|[]
name|getTermFrequencies
parameter_list|()
block|{
return|return
name|termFreqs
return|;
block|}
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|term
parameter_list|)
block|{
name|int
name|res
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|terms
argument_list|,
name|term
argument_list|)
decl_stmt|;
return|return
name|res
operator|>=
literal|0
condition|?
name|res
else|:
operator|-
literal|1
return|;
block|}
DECL|method|indexesOf
specifier|public
name|int
index|[]
name|indexesOf
parameter_list|(
name|String
index|[]
name|terms
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|res
index|[]
init|=
operator|new
name|int
index|[
name|len
index|]
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|indexOf
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
