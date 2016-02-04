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
begin_class
DECL|class|ReqExclBulkScorer
specifier|final
class|class
name|ReqExclBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|field|req
specifier|private
specifier|final
name|BulkScorer
name|req
decl_stmt|;
DECL|field|excl
specifier|private
specifier|final
name|DocIdSetIterator
name|excl
decl_stmt|;
DECL|method|ReqExclBulkScorer
name|ReqExclBulkScorer
parameter_list|(
name|BulkScorer
name|req
parameter_list|,
name|DocIdSetIterator
name|excl
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|excl
operator|=
name|excl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|upTo
init|=
name|min
decl_stmt|;
name|int
name|exclDoc
init|=
name|excl
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|upTo
operator|<
name|max
condition|)
block|{
if|if
condition|(
name|exclDoc
operator|<
name|upTo
condition|)
block|{
name|exclDoc
operator|=
name|excl
operator|.
name|advance
argument_list|(
name|upTo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|exclDoc
operator|==
name|upTo
condition|)
block|{
comment|// upTo is excluded so we can consider that we scored up to upTo+1
name|upTo
operator|+=
literal|1
expr_stmt|;
name|exclDoc
operator|=
name|excl
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|upTo
operator|=
name|req
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|upTo
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|exclDoc
argument_list|,
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|upTo
operator|==
name|max
condition|)
block|{
name|upTo
operator|=
name|req
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|upTo
argument_list|,
name|upTo
argument_list|)
expr_stmt|;
block|}
return|return
name|upTo
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|req
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
end_class
end_unit
