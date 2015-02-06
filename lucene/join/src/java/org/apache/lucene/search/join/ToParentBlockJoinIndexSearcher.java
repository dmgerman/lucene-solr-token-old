begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|PostingsEnum
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
name|search
operator|.
name|Collector
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
begin_comment
comment|/**  * An {@link IndexSearcher} to use in conjunction with  * {@link ToParentBlockJoinCollector}.  */
end_comment
begin_class
DECL|class|ToParentBlockJoinIndexSearcher
specifier|public
class|class
name|ToParentBlockJoinIndexSearcher
extends|extends
name|IndexSearcher
block|{
comment|/** Creates a searcher searching the provided index. Search on individual    *  segments will be run in the provided {@link ExecutorService}.    * @see IndexSearcher#IndexSearcher(IndexReader, ExecutorService) */
DECL|method|ToParentBlockJoinIndexSearcher
specifier|public
name|ToParentBlockJoinIndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|ExecutorService
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a searcher searching the provided index.    * @see IndexSearcher#IndexSearcher(IndexReader) */
DECL|method|ToParentBlockJoinIndexSearcher
specifier|public
name|ToParentBlockJoinIndexSearcher
parameter_list|(
name|IndexReader
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|protected
name|void
name|search
parameter_list|(
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|leaves
control|)
block|{
comment|// search each subreader
comment|// we force the use of Scorer (not BulkScorer) to make sure
comment|// that the scorer passed to LeafCollector.setScorer supports
comment|// Scorer.getChildren
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|,
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
specifier|final
name|LeafCollector
name|leafCollector
init|=
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|scorer
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|scorer
operator|.
name|nextDoc
argument_list|()
control|)
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
block|}
block|}
end_class
end_unit
