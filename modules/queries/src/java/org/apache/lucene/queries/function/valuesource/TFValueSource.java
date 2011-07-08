begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
operator|.
name|valuesource
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|*
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
operator|.
name|AtomicReaderContext
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
name|queries
operator|.
name|function
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|FloatDocValues
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|Similarity
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
name|TFIDFSimilarity
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
name|BytesRef
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
name|util
operator|.
name|Map
import|;
end_import
begin_class
DECL|class|TFValueSource
specifier|public
class|class
name|TFValueSource
extends|extends
name|TermFreqValueSource
block|{
DECL|method|TFValueSource
specifier|public
name|TFValueSource
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|val
parameter_list|,
name|String
name|indexedField
parameter_list|,
name|BytesRef
name|indexedBytes
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|indexedField
argument_list|,
name|indexedBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"tf"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|readerContext
operator|.
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|Similarity
name|sim
init|=
operator|(
operator|(
name|IndexSearcher
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"searcher"
argument_list|)
operator|)
operator|.
name|getSimilarityProvider
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|sim
operator|instanceof
name|TFIDFSimilarity
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"requires a TFIDFSimilarity (such as DefaultSimilarity)"
argument_list|)
throw|;
block|}
specifier|final
name|TFIDFSimilarity
name|similarity
init|=
operator|(
name|TFIDFSimilarity
operator|)
name|sim
decl_stmt|;
return|return
operator|new
name|FloatDocValues
argument_list|(
name|this
argument_list|)
block|{
name|DocsEnum
name|docs
decl_stmt|;
name|int
name|atDoc
decl_stmt|;
name|int
name|lastDocRequested
init|=
operator|-
literal|1
decl_stmt|;
block|{
name|reset
parameter_list|()
constructor_decl|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
comment|// no one should call us for deleted docs?
name|docs
operator|=
name|terms
operator|==
literal|null
condition|?
literal|null
else|:
name|terms
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|indexedBytes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
operator|new
name|DocsEnum
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
block|}
expr_stmt|;
block|}
name|atDoc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|doc
operator|<
name|lastDocRequested
condition|)
block|{
comment|// out-of-order access.... reset
name|reset
argument_list|()
expr_stmt|;
block|}
name|lastDocRequested
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|atDoc
operator|<
name|doc
condition|)
block|{
name|atDoc
operator|=
name|docs
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|atDoc
operator|>
name|doc
condition|)
block|{
comment|// term doesn't match this document... either because we hit the
comment|// end, or because the next doc is after this doc.
return|return
name|similarity
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// a match!
return|return
name|similarity
operator|.
name|tf
argument_list|(
name|docs
operator|.
name|freq
argument_list|()
argument_list|)
return|;
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
literal|"caught exception in function "
operator|+
name|description
argument_list|()
operator|+
literal|" : doc="
operator|+
name|doc
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
