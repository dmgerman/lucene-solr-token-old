begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.accumulator
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|accumulator
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
name|index
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
name|Scorer
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
begin_comment
comment|/**  * Abstract Collector that manages all StatsCollectors, Expressions and Facets.  */
end_comment
begin_class
DECL|class|ValueAccumulator
specifier|public
specifier|abstract
class|class
name|ValueAccumulator
extends|extends
name|Collector
block|{
comment|/**    * @param context The context to read documents from.    * @throws IOException if setting next reader fails    */
DECL|method|setNextReader
specifier|public
specifier|abstract
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Finalizes the statistics within each StatsCollector.    * Must be called before<code>export()</code>.    */
DECL|method|compute
specifier|public
specifier|abstract
name|void
name|compute
parameter_list|()
function_decl|;
DECL|method|export
specifier|public
specifier|abstract
name|NamedList
argument_list|<
name|?
argument_list|>
name|export
parameter_list|()
function_decl|;
DECL|method|postProcess
specifier|public
name|void
name|postProcess
parameter_list|()
throws|throws
name|IOException
block|{
comment|// NOP
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
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
comment|// NOP
block|}
block|}
end_class
end_unit