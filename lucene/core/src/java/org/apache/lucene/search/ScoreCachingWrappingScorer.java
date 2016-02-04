begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * A {@link Scorer} which wraps another scorer and caches the score of the  * current document. Successive calls to {@link #score()} will return the same  * result and will not invoke the wrapped Scorer's score() method, unless the  * current document has changed.<br>  * This class might be useful due to the changes done to the {@link Collector}  * interface, in which the score is not computed for a document by default, only  * if the collector requests it. Some collectors may need to use the score in  * several places, however all they have in hand is a {@link Scorer} object, and  * might end up computing the score of a document more than once.  */
end_comment
begin_class
DECL|class|ScoreCachingWrappingScorer
specifier|public
class|class
name|ScoreCachingWrappingScorer
extends|extends
name|FilterScorer
block|{
DECL|field|curDoc
specifier|private
name|int
name|curDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|curScore
specifier|private
name|float
name|curScore
decl_stmt|;
comment|/** Creates a new instance by wrapping the given scorer. */
DECL|method|ScoreCachingWrappingScorer
specifier|public
name|ScoreCachingWrappingScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|super
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|in
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|curDoc
condition|)
block|{
name|curScore
operator|=
name|in
operator|.
name|score
argument_list|()
expr_stmt|;
name|curDoc
operator|=
name|doc
expr_stmt|;
block|}
return|return
name|curScore
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|in
argument_list|,
literal|"CACHED"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
