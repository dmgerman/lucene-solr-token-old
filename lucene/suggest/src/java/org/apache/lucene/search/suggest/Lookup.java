begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|search
operator|.
name|spell
operator|.
name|Dictionary
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
name|spell
operator|.
name|TermFreqIterator
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
name|util
operator|.
name|BytesRefIterator
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
name|util
operator|.
name|PriorityQueue
import|;
end_import
begin_comment
comment|/**  * Simple Lookup interface for {@link CharSequence} suggestions.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lookup
specifier|public
specifier|abstract
class|class
name|Lookup
block|{
comment|/**    * Result of a lookup.    */
DECL|class|LookupResult
specifier|public
specifier|static
specifier|final
class|class
name|LookupResult
implements|implements
name|Comparable
argument_list|<
name|LookupResult
argument_list|>
block|{
DECL|field|key
specifier|public
specifier|final
name|CharSequence
name|key
decl_stmt|;
DECL|field|value
specifier|public
specifier|final
name|long
name|value
decl_stmt|;
DECL|method|LookupResult
specifier|public
name|LookupResult
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|key
operator|+
literal|"/"
operator|+
name|value
return|;
block|}
comment|/** Compare alphabetically. */
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|LookupResult
name|o
parameter_list|)
block|{
return|return
name|CHARSEQUENCE_COMPARATOR
operator|.
name|compare
argument_list|(
name|key
argument_list|,
name|o
operator|.
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * A simple char-by-char comparator for {@link CharSequence}    */
DECL|field|CHARSEQUENCE_COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|CharSequence
argument_list|>
name|CHARSEQUENCE_COMPARATOR
init|=
operator|new
name|CharSequenceComparator
argument_list|()
decl_stmt|;
DECL|class|CharSequenceComparator
specifier|private
specifier|static
class|class
name|CharSequenceComparator
implements|implements
name|Comparator
argument_list|<
name|CharSequence
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|CharSequence
name|o1
parameter_list|,
name|CharSequence
name|o2
parameter_list|)
block|{
specifier|final
name|int
name|l1
init|=
name|o1
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|l2
init|=
name|o2
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|aStop
init|=
name|Math
operator|.
name|min
argument_list|(
name|l1
argument_list|,
name|l2
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
name|aStop
condition|;
name|i
operator|++
control|)
block|{
name|int
name|diff
init|=
name|o1
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
name|o2
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
comment|// One is a prefix of the other, or, they are equal:
return|return
name|l1
operator|-
name|l2
return|;
block|}
block|}
comment|/**    * A {@link PriorityQueue} collecting a fixed size of high priority {@link LookupResult}    */
DECL|class|LookupPriorityQueue
specifier|public
specifier|static
specifier|final
class|class
name|LookupPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|LookupResult
argument_list|>
block|{
comment|// TODO: should we move this out of the interface into a utility class?
DECL|method|LookupPriorityQueue
specifier|public
name|LookupPriorityQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|LookupResult
name|a
parameter_list|,
name|LookupResult
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
return|;
block|}
comment|/**      * Returns the top N results in descending order.      * @return the top N results in descending order.      */
DECL|method|getResults
specifier|public
name|LookupResult
index|[]
name|getResults
parameter_list|()
block|{
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
name|LookupResult
index|[]
name|res
init|=
operator|new
name|LookupResult
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|res
index|[
name|i
index|]
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
comment|/** Build lookup from a dictionary. Some implementations may require sorted    * or unsorted keys from the dictionary's iterator - use    * {@link SortedTermFreqIteratorWrapper} or    * {@link UnsortedTermFreqIteratorWrapper} in such case.    */
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|Dictionary
name|dict
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRefIterator
name|it
init|=
name|dict
operator|.
name|getWordsIterator
argument_list|()
decl_stmt|;
name|TermFreqIterator
name|tfit
decl_stmt|;
if|if
condition|(
name|it
operator|instanceof
name|TermFreqIterator
condition|)
block|{
name|tfit
operator|=
operator|(
name|TermFreqIterator
operator|)
name|it
expr_stmt|;
block|}
else|else
block|{
name|tfit
operator|=
operator|new
name|TermFreqIterator
operator|.
name|TermFreqIteratorWrapper
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
name|build
argument_list|(
name|tfit
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds up a new internal {@link Lookup} representation based on the given {@link TermFreqIterator}.    * The implementation might re-sort the data internally.    */
DECL|method|build
specifier|public
specifier|abstract
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Look up a key and return possible completion for this key.    * @param key lookup key. Depending on the implementation this may be    * a prefix, misspelling, or even infix.    * @param onlyMorePopular return only more popular results    * @param num maximum number of results to return    * @return a list of possible completions, with their relative weight (e.g. popularity)    */
DECL|method|lookup
specifier|public
specifier|abstract
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|boolean
name|onlyMorePopular
parameter_list|,
name|int
name|num
parameter_list|)
function_decl|;
comment|/**    * Persist the constructed lookup data to a directory. Optional operation.    * @param output {@link OutputStream} to write the data to.    * @return true if successful, false if unsuccessful or not supported.    * @throws IOException when fatal IO error occurs.    */
DECL|method|store
specifier|public
specifier|abstract
name|boolean
name|store
parameter_list|(
name|OutputStream
name|output
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Discard current lookup data and load it from a previously saved copy.    * Optional operation.    * @param input the {@link InputStream} to load the lookup data.    * @return true if completed successfully, false if unsuccessful or not supported.    * @throws IOException when fatal IO error occurs.    */
DECL|method|load
specifier|public
specifier|abstract
name|boolean
name|load
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
