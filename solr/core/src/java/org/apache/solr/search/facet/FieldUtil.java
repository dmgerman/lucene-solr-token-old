begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
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
name|index
operator|.
name|SortedSetDocValues
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
name|TermsEnum
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
name|QParser
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
name|QueryContext
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
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/** @lucene.internal  * Porting helper... may be removed if it offers no value in the future.  */
end_comment
begin_class
DECL|class|FieldUtil
specifier|public
class|class
name|FieldUtil
block|{
comment|/** Simpler method that creates a request context and looks up the field for you */
DECL|method|getSortedDocValues
specifier|public
specifier|static
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|QueryContext
name|qContext
init|=
name|QueryContext
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
name|getSortedDocValues
argument_list|(
name|qContext
argument_list|,
name|sf
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getSortedDocValues
specifier|public
specifier|static
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|QueryContext
name|context
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|si
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getLeafReader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// if (!field.hasDocValues()&& (field.getType() instanceof StrField || field.getType() instanceof TextField)) {
comment|// }
return|return
name|si
operator|==
literal|null
condition|?
name|DocValues
operator|.
name|emptySorted
argument_list|()
else|:
name|si
return|;
block|}
DECL|method|getSortedSetDocValues
specifier|public
specifier|static
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|QueryContext
name|context
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|si
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getLeafReader
argument_list|()
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|si
operator|==
literal|null
condition|?
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
else|:
name|si
return|;
block|}
block|}
end_class
end_unit
