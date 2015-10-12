begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|IndexReaderContext
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
name|LeafReader
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|SortedDocValues
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
name|ConstantScoreQuery
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
name|DocIdSet
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
name|LeafCollector
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
name|Scorer
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
name|Weight
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
name|BitDocIdSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|CharsRefBuilder
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
name|FixedBitSet
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
name|params
operator|.
name|SolrParams
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|IndexSchema
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
name|schema
operator|.
name|StrField
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
import|;
end_import
begin_comment
comment|/** * syntax fq={!hash workers=11 worker=4 keys=field1,field2} * */
end_comment
begin_class
DECL|class|HashQParserPlugin
specifier|public
class|class
name|HashQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"hash"
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{    }
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|HashQParser
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|request
argument_list|)
return|;
block|}
DECL|class|HashQParser
specifier|private
class|class
name|HashQParser
extends|extends
name|QParser
block|{
DECL|method|HashQParser
specifier|public
name|HashQParser
parameter_list|(
name|String
name|query
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
block|{
name|int
name|workers
init|=
name|localParams
operator|.
name|getInt
argument_list|(
literal|"workers"
argument_list|)
decl_stmt|;
name|int
name|worker
init|=
name|localParams
operator|.
name|getInt
argument_list|(
literal|"worker"
argument_list|)
decl_stmt|;
name|String
name|keys
init|=
name|params
operator|.
name|get
argument_list|(
literal|"partitionKeys"
argument_list|)
decl_stmt|;
return|return
operator|new
name|HashQuery
argument_list|(
name|keys
argument_list|,
name|workers
argument_list|,
name|worker
argument_list|)
return|;
block|}
block|}
DECL|class|HashQuery
specifier|private
class|class
name|HashQuery
extends|extends
name|ExtendedQueryBase
implements|implements
name|PostFilter
block|{
DECL|field|keysParam
specifier|private
name|String
name|keysParam
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|worker
specifier|private
name|int
name|worker
decl_stmt|;
DECL|method|getCache
specifier|public
name|boolean
name|getCache
parameter_list|()
block|{
if|if
condition|(
name|getCost
argument_list|()
operator|>
literal|99
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getCache
argument_list|()
return|;
block|}
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|keysParam
operator|.
name|hashCode
argument_list|()
operator|+
name|workers
operator|+
name|worker
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|HashQuery
name|h
init|=
operator|(
name|HashQuery
operator|)
name|o
decl_stmt|;
return|return
name|keysParam
operator|.
name|equals
argument_list|(
name|h
operator|.
name|keysParam
argument_list|)
operator|&&
name|workers
operator|==
name|h
operator|.
name|workers
operator|&&
name|worker
operator|==
name|h
operator|.
name|worker
return|;
block|}
DECL|method|HashQuery
specifier|public
name|HashQuery
parameter_list|(
name|String
name|keysParam
parameter_list|,
name|int
name|workers
parameter_list|,
name|int
name|worker
parameter_list|)
block|{
name|this
operator|.
name|keysParam
operator|=
name|keysParam
expr_stmt|;
name|this
operator|.
name|workers
operator|=
name|workers
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|keys
init|=
name|keysParam
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|solrIndexSearcher
init|=
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
decl_stmt|;
name|IndexReaderContext
name|context
init|=
name|solrIndexSearcher
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|context
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|FixedBitSet
index|[]
name|fixedBitSets
init|=
operator|new
name|FixedBitSet
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
try|try
block|{
name|SegmentPartitioner
name|segmentPartitioner
init|=
operator|new
name|SegmentPartitioner
argument_list|(
name|leaf
argument_list|,
name|worker
argument_list|,
name|workers
argument_list|,
name|keys
argument_list|,
name|solrIndexSearcher
argument_list|)
decl_stmt|;
name|segmentPartitioner
operator|.
name|run
argument_list|()
expr_stmt|;
name|fixedBitSets
index|[
name|segmentPartitioner
operator|.
name|context
operator|.
name|ord
index|]
operator|=
name|segmentPartitioner
operator|.
name|docs
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|ConstantScoreQuery
name|constantScoreQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|BitsFilter
argument_list|(
name|fixedBitSets
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|rewrite
argument_list|(
name|constantScoreQuery
argument_list|)
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|class|BitsFilter
specifier|public
class|class
name|BitsFilter
extends|extends
name|Filter
block|{
DECL|field|bitSets
specifier|private
name|FixedBitSet
index|[]
name|bitSets
decl_stmt|;
DECL|method|BitsFilter
specifier|public
name|BitsFilter
parameter_list|(
name|FixedBitSet
index|[]
name|bitSets
parameter_list|)
block|{
name|this
operator|.
name|bitSets
operator|=
name|bitSets
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|bits
parameter_list|)
block|{
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|bitSets
index|[
name|context
operator|.
name|ord
index|]
argument_list|)
argument_list|,
name|bits
argument_list|)
return|;
block|}
block|}
DECL|class|SegmentPartitioner
class|class
name|SegmentPartitioner
implements|implements
name|Runnable
block|{
DECL|field|context
specifier|public
name|LeafReaderContext
name|context
decl_stmt|;
DECL|field|worker
specifier|private
name|int
name|worker
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|k
specifier|private
name|HashKey
name|k
decl_stmt|;
DECL|field|docs
specifier|public
name|FixedBitSet
name|docs
decl_stmt|;
DECL|method|SegmentPartitioner
specifier|public
name|SegmentPartitioner
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|worker
parameter_list|,
name|int
name|workers
parameter_list|,
name|String
index|[]
name|keys
parameter_list|,
name|SolrIndexSearcher
name|solrIndexSearcher
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
name|this
operator|.
name|workers
operator|=
name|workers
expr_stmt|;
name|HashKey
index|[]
name|hashKeys
init|=
operator|new
name|HashKey
index|[
name|keys
operator|.
name|length
index|]
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|solrIndexSearcher
operator|.
name|getSchema
argument_list|()
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|keys
index|[
name|i
index|]
decl_stmt|;
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getField
argument_list|(
name|key
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|HashKey
name|h
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|StrField
condition|)
block|{
name|h
operator|=
operator|new
name|BytesHash
argument_list|(
name|key
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|h
operator|=
operator|new
name|NumericHash
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|hashKeys
index|[
name|i
index|]
operator|=
name|h
expr_stmt|;
block|}
name|k
operator|=
operator|(
name|hashKeys
operator|.
name|length
operator|>
literal|1
operator|)
condition|?
operator|new
name|CompositeHash
argument_list|(
name|hashKeys
argument_list|)
else|:
name|hashKeys
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
try|try
block|{
name|k
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|docs
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
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
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|k
operator|.
name|hashCode
argument_list|(
name|i
argument_list|)
operator|&
literal|0x7FFFFFFF
operator|)
operator|%
name|workers
operator|==
name|worker
condition|)
block|{
name|docs
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getFilterCollector
specifier|public
name|DelegatingCollector
name|getFilterCollector
parameter_list|(
name|IndexSearcher
name|indexSearcher
parameter_list|)
block|{
name|String
index|[]
name|keys
init|=
name|keysParam
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|HashKey
index|[]
name|hashKeys
init|=
operator|new
name|HashKey
index|[
name|keys
operator|.
name|length
index|]
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
operator|(
name|SolrIndexSearcher
operator|)
name|indexSearcher
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|keys
index|[
name|i
index|]
decl_stmt|;
name|FieldType
name|ft
init|=
name|schema
operator|.
name|getField
argument_list|(
name|key
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|HashKey
name|h
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|StrField
condition|)
block|{
name|h
operator|=
operator|new
name|BytesHash
argument_list|(
name|key
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|h
operator|=
operator|new
name|NumericHash
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|hashKeys
index|[
name|i
index|]
operator|=
name|h
expr_stmt|;
block|}
name|HashKey
name|k
init|=
operator|(
name|hashKeys
operator|.
name|length
operator|>
literal|1
operator|)
condition|?
operator|new
name|CompositeHash
argument_list|(
name|hashKeys
argument_list|)
else|:
name|hashKeys
index|[
literal|0
index|]
decl_stmt|;
return|return
operator|new
name|HashCollector
argument_list|(
name|k
argument_list|,
name|workers
argument_list|,
name|worker
argument_list|)
return|;
block|}
block|}
DECL|class|HashCollector
specifier|private
class|class
name|HashCollector
extends|extends
name|DelegatingCollector
block|{
DECL|field|worker
specifier|private
name|int
name|worker
decl_stmt|;
DECL|field|workers
specifier|private
name|int
name|workers
decl_stmt|;
DECL|field|hashKey
specifier|private
name|HashKey
name|hashKey
decl_stmt|;
DECL|field|leafCollector
specifier|private
name|LeafCollector
name|leafCollector
decl_stmt|;
DECL|method|HashCollector
specifier|public
name|HashCollector
parameter_list|(
name|HashKey
name|hashKey
parameter_list|,
name|int
name|workers
parameter_list|,
name|int
name|worker
parameter_list|)
block|{
name|this
operator|.
name|hashKey
operator|=
name|hashKey
expr_stmt|;
name|this
operator|.
name|workers
operator|=
name|workers
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNextReader
specifier|public
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|hashKey
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|leafCollector
operator|=
name|delegate
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|hashKey
operator|.
name|hashCode
argument_list|(
name|doc
argument_list|)
operator|&
literal|0x7FFFFFFF
operator|)
operator|%
name|workers
operator|==
name|worker
condition|)
block|{
name|leafCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|interface|HashKey
specifier|private
interface|interface
name|HashKey
block|{
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|hashCode
specifier|public
name|long
name|hashCode
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
DECL|class|BytesHash
specifier|private
class|class
name|BytesHash
implements|implements
name|HashKey
block|{
DECL|field|values
specifier|private
name|SortedDocValues
name|values
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|fieldType
specifier|private
name|FieldType
name|fieldType
decl_stmt|;
DECL|field|charsRefBuilder
specifier|private
name|CharsRefBuilder
name|charsRefBuilder
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|method|BytesHash
specifier|public
name|BytesHash
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldType
name|fieldType
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|long
name|hashCode
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|BytesRef
name|ref
init|=
name|values
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|this
operator|.
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|ref
argument_list|,
name|charsRefBuilder
argument_list|)
expr_stmt|;
name|CharsRef
name|charsRef
init|=
name|charsRefBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|charsRef
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
DECL|class|NumericHash
specifier|private
class|class
name|NumericHash
implements|implements
name|HashKey
block|{
DECL|field|values
specifier|private
name|NumericDocValues
name|values
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|NumericHash
specifier|public
name|NumericHash
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|long
name|hashCode
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|long
name|l
init|=
name|values
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Longs
operator|.
name|hashCode
argument_list|(
name|l
argument_list|)
return|;
block|}
block|}
DECL|class|ZeroHash
specifier|private
class|class
name|ZeroHash
implements|implements
name|HashKey
block|{
DECL|method|hashCode
specifier|public
name|long
name|hashCode
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{      }
block|}
DECL|class|CompositeHash
specifier|private
class|class
name|CompositeHash
implements|implements
name|HashKey
block|{
DECL|field|key1
specifier|private
name|HashKey
name|key1
decl_stmt|;
DECL|field|key2
specifier|private
name|HashKey
name|key2
decl_stmt|;
DECL|field|key3
specifier|private
name|HashKey
name|key3
decl_stmt|;
DECL|field|key4
specifier|private
name|HashKey
name|key4
decl_stmt|;
DECL|method|CompositeHash
specifier|public
name|CompositeHash
parameter_list|(
name|HashKey
index|[]
name|hashKeys
parameter_list|)
block|{
name|key1
operator|=
name|hashKeys
index|[
literal|0
index|]
expr_stmt|;
name|key2
operator|=
name|hashKeys
index|[
literal|1
index|]
expr_stmt|;
name|key3
operator|=
operator|(
name|hashKeys
operator|.
name|length
operator|>
literal|2
operator|)
condition|?
name|hashKeys
index|[
literal|2
index|]
else|:
operator|new
name|ZeroHash
argument_list|()
expr_stmt|;
name|key4
operator|=
operator|(
name|hashKeys
operator|.
name|length
operator|>
literal|3
operator|)
condition|?
name|hashKeys
index|[
literal|3
index|]
else|:
operator|new
name|ZeroHash
argument_list|()
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|key1
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|key2
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|key3
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|key4
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|long
name|hashCode
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|key1
operator|.
name|hashCode
argument_list|(
name|doc
argument_list|)
operator|+
name|key2
operator|.
name|hashCode
argument_list|(
name|doc
argument_list|)
operator|+
name|key3
operator|.
name|hashCode
argument_list|(
name|doc
argument_list|)
operator|+
name|key4
operator|.
name|hashCode
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
