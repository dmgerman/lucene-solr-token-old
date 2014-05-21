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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|DocsEnum
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
name|VirtualMethod
import|;
end_import
begin_comment
comment|/** Wraps a Scorer with additional checks */
end_comment
begin_class
DECL|class|AssertingBulkScorer
specifier|public
class|class
name|AssertingBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|field|SCORE_COLLECTOR
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|BulkScorer
argument_list|>
name|SCORE_COLLECTOR
init|=
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|BulkScorer
operator|.
name|class
argument_list|,
literal|"score"
argument_list|,
name|LeafCollector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SCORE_COLLECTOR_RANGE
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|BulkScorer
argument_list|>
name|SCORE_COLLECTOR_RANGE
init|=
operator|new
name|VirtualMethod
argument_list|<>
argument_list|(
name|BulkScorer
operator|.
name|class
argument_list|,
literal|"score"
argument_list|,
name|LeafCollector
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|wrap
specifier|public
specifier|static
name|BulkScorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|BulkScorer
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingBulkScorer
condition|)
block|{
return|return
name|other
return|;
block|}
return|return
operator|new
name|AssertingBulkScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|)
return|;
block|}
DECL|method|shouldWrap
specifier|public
specifier|static
name|boolean
name|shouldWrap
parameter_list|(
name|BulkScorer
name|inScorer
parameter_list|)
block|{
return|return
name|SCORE_COLLECTOR
operator|.
name|isOverriddenAsOf
argument_list|(
name|inScorer
operator|.
name|getClass
argument_list|()
argument_list|)
operator|||
name|SCORE_COLLECTOR_RANGE
operator|.
name|isOverriddenAsOf
argument_list|(
name|inScorer
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|BulkScorer
name|in
decl_stmt|;
DECL|method|AssertingBulkScorer
specifier|private
name|AssertingBulkScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|BulkScorer
name|in
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|getIn
specifier|public
name|BulkScorer
name|getIn
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|boolean
name|remaining
init|=
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|remaining
assert|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|max
argument_list|)
return|;
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
literal|"AssertingBulkScorer("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
