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
name|IndexReader
import|;
end_import
begin_comment
comment|/**  * Wrapper for ({@link HitCollector}) implementations, which simply re-bases the  * incoming docID before calling {@link HitCollector#collect}.  *   * @deprecated Please migrate custom HitCollectors to the new {@link Collector}  *             class. This class will be removed when {@link HitCollector} is  *             removed.  */
end_comment
begin_class
DECL|class|HitCollectorWrapper
specifier|public
class|class
name|HitCollectorWrapper
extends|extends
name|Collector
block|{
DECL|field|collector
specifier|private
name|HitCollector
name|collector
decl_stmt|;
DECL|field|base
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
init|=
literal|null
decl_stmt|;
DECL|method|HitCollectorWrapper
specifier|public
name|HitCollectorWrapper
parameter_list|(
name|HitCollector
name|collector
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|base
operator|=
name|docBase
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
name|collector
operator|.
name|collect
argument_list|(
name|doc
operator|+
name|base
argument_list|,
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
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
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
