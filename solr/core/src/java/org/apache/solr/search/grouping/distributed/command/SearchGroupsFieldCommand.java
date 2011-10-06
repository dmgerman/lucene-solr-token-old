begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.command
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|search
operator|.
name|grouping
operator|.
name|term
operator|.
name|TermFirstPassGroupingCollector
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
name|solr
operator|.
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|grouping
operator|.
name|Command
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
name|Arrays
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
name|List
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|SearchGroupsFieldCommand
specifier|public
class|class
name|SearchGroupsFieldCommand
implements|implements
name|Command
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|field
specifier|private
name|SchemaField
name|field
decl_stmt|;
DECL|field|groupSort
specifier|private
name|Sort
name|groupSort
decl_stmt|;
DECL|field|topNGroups
specifier|private
name|Integer
name|topNGroups
decl_stmt|;
DECL|method|setField
specifier|public
name|Builder
name|setField
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setGroupSort
specifier|public
name|Builder
name|setGroupSort
parameter_list|(
name|Sort
name|groupSort
parameter_list|)
block|{
name|this
operator|.
name|groupSort
operator|=
name|groupSort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTopNGroups
specifier|public
name|Builder
name|setTopNGroups
parameter_list|(
name|int
name|topNGroups
parameter_list|)
block|{
name|this
operator|.
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|SearchGroupsFieldCommand
name|build
parameter_list|()
block|{
if|if
condition|(
name|field
operator|==
literal|null
operator|||
name|groupSort
operator|==
literal|null
operator|||
name|topNGroups
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All fields must be set"
argument_list|)
throw|;
block|}
return|return
operator|new
name|SearchGroupsFieldCommand
argument_list|(
name|field
argument_list|,
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
return|;
block|}
block|}
DECL|field|field
specifier|private
specifier|final
name|SchemaField
name|field
decl_stmt|;
DECL|field|groupSort
specifier|private
specifier|final
name|Sort
name|groupSort
decl_stmt|;
DECL|field|topNGroups
specifier|private
specifier|final
name|int
name|topNGroups
decl_stmt|;
DECL|field|collector
specifier|private
name|TermFirstPassGroupingCollector
name|collector
decl_stmt|;
DECL|method|SearchGroupsFieldCommand
specifier|private
name|SearchGroupsFieldCommand
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|topNGroups
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
name|groupSort
operator|=
name|groupSort
expr_stmt|;
name|this
operator|.
name|topNGroups
operator|=
name|topNGroups
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|List
argument_list|<
name|Collector
argument_list|>
name|create
parameter_list|()
throws|throws
name|IOException
block|{
name|collector
operator|=
operator|new
name|TermFirstPassGroupingCollector
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|groupSort
argument_list|,
name|topNGroups
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|(
name|Collector
operator|)
name|collector
argument_list|)
return|;
block|}
DECL|method|result
specifier|public
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|result
parameter_list|()
block|{
return|return
name|collector
operator|.
name|getTopGroups
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getSortWithinGroup
specifier|public
name|Sort
name|getSortWithinGroup
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getGroupSort
specifier|public
name|Sort
name|getGroupSort
parameter_list|()
block|{
return|return
name|groupSort
return|;
block|}
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|field
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class
end_unit
