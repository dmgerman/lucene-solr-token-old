begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
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
name|MergeState
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
name|DocValue
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
name|Bits
import|;
end_import
begin_comment
comment|/**  * Abstract API that consumes {@link DocValue}s.  * {@link DocValuesConsumer} are always associated with a specific field and  * segments. Concrete implementations of this API write the given  * {@link DocValue} into a implementation specific format depending on  * the fields meta-data.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesConsumer
specifier|public
specifier|abstract
class|class
name|DocValuesConsumer
block|{
comment|/**    * Adds the given {@link DocValue} instance to this    * {@link DocValuesConsumer}    *     * @param docID    *          the document ID to add the value for. The docID must always    *          increase or be<tt>0</tt> if it is the first call to this method.    * @param docValue    *          the value to add    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|DocValue
name|docValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called when the consumer of this API is doc with adding    * {@link DocValue} to this {@link DocValuesConsumer}    *     * @param docCount    *          the total number of documents in this {@link DocValuesConsumer}.    *          Must be greater than or equal the last given docID to    *          {@link #add(int, DocValue)}.    * @throws IOException    */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Merges the given {@link org.apache.lucene.index.MergeState} into    * this {@link DocValuesConsumer}.    *     * @param mergeState    *          the state to merge    * @param docValues docValues array containing one instance per reader (    *          {@link org.apache.lucene.index.MergeState#readers}) or<code>null</code> if the reader has    *          no {@link DocValues} instance.    * @throws IOException    *           if an {@link IOException} occurs    */
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|DocValues
index|[]
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|mergeState
operator|!=
literal|null
assert|;
name|boolean
name|hasMerged
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|readerIDX
init|=
literal|0
init|;
name|readerIDX
operator|<
name|mergeState
operator|.
name|readers
operator|.
name|size
argument_list|()
condition|;
name|readerIDX
operator|++
control|)
block|{
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIDX
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValues
index|[
name|readerIDX
index|]
operator|!=
literal|null
condition|)
block|{
name|hasMerged
operator|=
literal|true
expr_stmt|;
name|merge
argument_list|(
operator|new
name|SingleSubMergeState
argument_list|(
name|docValues
index|[
name|readerIDX
index|]
argument_list|,
name|mergeState
operator|.
name|docBase
index|[
name|readerIDX
index|]
argument_list|,
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|reader
operator|.
name|liveDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// only finish if no exception is thrown!
if|if
condition|(
name|hasMerged
condition|)
block|{
name|finish
argument_list|(
name|mergeState
operator|.
name|mergedDocCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Merges the given {@link SingleSubMergeState} into this {@link DocValuesConsumer}.    *     * @param mergeState    *          the {@link SingleSubMergeState} to merge    * @throws IOException    *           if an {@link IOException} occurs    */
comment|// TODO: can't we have a default implementation here that merges naively with our apis?
comment|// this is how stored fields and term vectors work. its a pain to have to impl merging
comment|// (should be an optimization to override it)
DECL|method|merge
specifier|protected
specifier|abstract
name|void
name|merge
parameter_list|(
name|SingleSubMergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Specialized auxiliary MergeState is necessary since we don't want to    * exploit internals up to the codecs consumer. An instance of this class is    * created for each merged low level {@link IndexReader} we are merging to    * support low level bulk copies.    */
DECL|class|SingleSubMergeState
specifier|public
specifier|static
class|class
name|SingleSubMergeState
block|{
comment|/**      * the source reader for this MergeState - merged values should be read from      * this instance      */
DECL|field|reader
specifier|public
specifier|final
name|DocValues
name|reader
decl_stmt|;
comment|/** the absolute docBase for this MergeState within the resulting segment */
DECL|field|docBase
specifier|public
specifier|final
name|int
name|docBase
decl_stmt|;
comment|/** the number of documents in this MergeState */
DECL|field|docCount
specifier|public
specifier|final
name|int
name|docCount
decl_stmt|;
comment|/** the not deleted bits for this MergeState */
DECL|field|liveDocs
specifier|public
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|method|SingleSubMergeState
specifier|public
name|SingleSubMergeState
parameter_list|(
name|DocValues
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Bits
name|liveDocs
parameter_list|)
block|{
assert|assert
name|reader
operator|!=
literal|null
assert|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
