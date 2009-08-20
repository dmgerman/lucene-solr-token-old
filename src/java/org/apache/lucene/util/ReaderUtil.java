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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_comment
comment|/**  * Common util methods for dealing with {@link IndexReader}s.  *  */
end_comment
begin_class
DECL|class|ReaderUtil
specifier|public
class|class
name|ReaderUtil
block|{
comment|/**    * Gathers sub-readers from reader into a List.    *     * @param allSubReaders    * @param reader    */
DECL|method|gatherSubReaders
specifier|public
specifier|static
name|void
name|gatherSubReaders
parameter_list|(
name|List
name|allSubReaders
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|IndexReader
index|[]
name|subReaders
init|=
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|subReaders
operator|==
literal|null
condition|)
block|{
comment|// Add the reader itself, and do not recurse
name|allSubReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|gatherSubReaders
argument_list|(
name|allSubReaders
argument_list|,
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns sub IndexReader that contains the given document id.    *        * @param doc id of document    * @param reader parent reader    * @return sub reader of parent which contains the specified doc id    */
DECL|method|subReader
specifier|public
specifier|static
name|IndexReader
name|subReader
parameter_list|(
name|int
name|doc
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|List
name|subReadersList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ReaderUtil
operator|.
name|gatherSubReaders
argument_list|(
name|subReadersList
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|subReaders
init|=
operator|(
name|IndexReader
index|[]
operator|)
name|subReadersList
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|subReadersList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|int
index|[]
name|docStarts
init|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
index|]
decl_stmt|;
name|int
name|maxDoc
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docStarts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|subReaders
index|[
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|doc
argument_list|,
name|docStarts
argument_list|)
index|]
return|;
block|}
comment|/**    * Returns sub-reader subIndex from reader.    *     * @param reader parent reader    * @param subIndex index of desired sub reader    * @return the subreader at subINdex    */
DECL|method|subReader
specifier|public
specifier|static
name|IndexReader
name|subReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|subIndex
parameter_list|)
block|{
name|List
name|subReadersList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ReaderUtil
operator|.
name|gatherSubReaders
argument_list|(
name|subReadersList
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|IndexReader
index|[]
name|subReaders
init|=
operator|(
name|IndexReader
index|[]
operator|)
name|subReadersList
operator|.
name|toArray
argument_list|(
operator|new
name|IndexReader
index|[
name|subReadersList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
name|subReaders
index|[
name|subIndex
index|]
return|;
block|}
comment|/**    * Returns index of the searcher/reader for document<code>n</code> in the    * array used to construct this searcher/reader.    */
DECL|method|subIndex
specifier|public
specifier|static
name|int
name|subIndex
parameter_list|(
name|int
name|n
parameter_list|,
name|int
index|[]
name|docStarts
parameter_list|)
block|{
comment|// find
comment|// searcher/reader for doc n:
name|int
name|size
init|=
name|docStarts
operator|.
name|length
decl_stmt|;
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|size
operator|-
literal|1
decl_stmt|;
comment|// for first element less than n, return its index
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|docStarts
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|size
operator|&&
name|docStarts
index|[
name|mid
operator|+
literal|1
index|]
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
block|}
end_class
end_unit
