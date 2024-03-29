begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.grouping.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
operator|.
name|function
package|;
end_package
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|ValueSource
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
name|Sort
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
name|grouping
operator|.
name|AbstractFirstPassGroupingCollector
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
name|mutable
operator|.
name|MutableValue
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
begin_comment
comment|/**  * Concrete implementation of {@link AbstractFirstPassGroupingCollector} that groups based on  * {@link ValueSource} instances.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FunctionFirstPassGroupingCollector
specifier|public
class|class
name|FunctionFirstPassGroupingCollector
extends|extends
name|AbstractFirstPassGroupingCollector
argument_list|<
name|MutableValue
argument_list|>
block|{
DECL|field|groupByVS
specifier|private
specifier|final
name|ValueSource
name|groupByVS
decl_stmt|;
DECL|field|vsContext
specifier|private
specifier|final
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
decl_stmt|;
DECL|field|filler
specifier|private
name|FunctionValues
operator|.
name|ValueFiller
name|filler
decl_stmt|;
DECL|field|mval
specifier|private
name|MutableValue
name|mval
decl_stmt|;
comment|/**    * Creates a first pass collector.    *    * @param groupByVS  The {@link ValueSource} instance to group by    * @param vsContext  The ValueSource context    * @param groupSort  The {@link Sort} used to sort the    *                   groups.  The top sorted document within each group    *                   according to groupSort, determines how that group    *                   sorts against other groups.  This must be non-null,    *                   ie, if you want to groupSort by relevance use    *                   Sort.RELEVANCE.    * @param topNGroups How many top groups to keep.    * @throws IOException When I/O related errors occur    */
DECL|method|FunctionFirstPassGroupingCollector
specifier|public
name|FunctionFirstPassGroupingCollector
parameter_list|(
name|ValueSource
name|groupByVS
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|vsContext
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupByVS
operator|=
name|groupByVS
expr_stmt|;
name|this
operator|.
name|vsContext
operator|=
name|vsContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocGroupValue
specifier|protected
name|MutableValue
name|getDocGroupValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|filler
operator|.
name|fillValue
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|mval
return|;
block|}
annotation|@
name|Override
DECL|method|copyDocGroupValue
specifier|protected
name|MutableValue
name|copyDocGroupValue
parameter_list|(
name|MutableValue
name|groupValue
parameter_list|,
name|MutableValue
name|reuse
parameter_list|)
block|{
if|if
condition|(
name|reuse
operator|!=
literal|null
condition|)
block|{
name|reuse
operator|.
name|copy
argument_list|(
name|groupValue
argument_list|)
expr_stmt|;
return|return
name|reuse
return|;
block|}
return|return
name|groupValue
operator|.
name|duplicate
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doSetNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
name|FunctionValues
name|values
init|=
name|groupByVS
operator|.
name|getValues
argument_list|(
name|vsContext
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
name|filler
operator|=
name|values
operator|.
name|getValueFiller
argument_list|()
expr_stmt|;
name|mval
operator|=
name|filler
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
