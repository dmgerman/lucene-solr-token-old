begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.grouping.term
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
name|term
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
name|AbstractSecondPassGroupingCollector
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
name|SearchGroup
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
name|SentinelIntSet
import|;
end_import
begin_comment
comment|/**  * Concrete implementation of {@link org.apache.lucene.search.grouping.AbstractSecondPassGroupingCollector} that groups based on  * field values and more specifically uses {@link org.apache.lucene.index.SortedDocValues}  * to collect grouped docs.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TermSecondPassGroupingCollector
specifier|public
class|class
name|TermSecondPassGroupingCollector
extends|extends
name|AbstractSecondPassGroupingCollector
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|groupField
specifier|private
specifier|final
name|String
name|groupField
decl_stmt|;
DECL|field|ordSet
specifier|private
specifier|final
name|SentinelIntSet
name|ordSet
decl_stmt|;
DECL|field|index
specifier|private
name|SortedDocValues
name|index
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|TermSecondPassGroupingCollector
specifier|public
name|TermSecondPassGroupingCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|groups
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|withinGroupSort
parameter_list|,
name|int
name|maxDocsPerGroup
parameter_list|,
name|boolean
name|getScores
parameter_list|,
name|boolean
name|getMaxScores
parameter_list|,
name|boolean
name|fillSortFields
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|groups
argument_list|,
name|groupSort
argument_list|,
name|withinGroupSort
argument_list|,
name|maxDocsPerGroup
argument_list|,
name|getScores
argument_list|,
name|getMaxScores
argument_list|,
name|fillSortFields
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupField
operator|=
name|groupField
expr_stmt|;
name|this
operator|.
name|ordSet
operator|=
operator|new
name|SentinelIntSet
argument_list|(
name|groupMap
operator|.
name|size
argument_list|()
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|super
operator|.
name|groupDocs
operator|=
operator|(
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
index|[]
operator|)
operator|new
name|SearchGroupDocs
index|[
name|ordSet
operator|.
name|keys
operator|.
name|length
index|]
expr_stmt|;
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
name|index
operator|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
comment|// Rebuild ordSet
name|ordSet
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|group
range|:
name|groupMap
operator|.
name|values
argument_list|()
control|)
block|{
comment|//      System.out.println("  group=" + (group.groupValue == null ? "null" : group.groupValue.utf8ToString()));
name|int
name|ord
init|=
name|group
operator|.
name|groupValue
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|index
operator|.
name|lookupTerm
argument_list|(
name|group
operator|.
name|groupValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|groupValue
operator|==
literal|null
operator|||
name|ord
operator|>=
literal|0
condition|)
block|{
name|groupDocs
index|[
name|ordSet
operator|.
name|put
argument_list|(
name|ord
argument_list|)
index|]
operator|=
name|group
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|retrieveGroup
specifier|protected
name|SearchGroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|retrieveGroup
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|slot
init|=
name|ordSet
operator|.
name|find
argument_list|(
name|index
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|slot
operator|>=
literal|0
condition|)
block|{
return|return
name|groupDocs
index|[
name|slot
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
