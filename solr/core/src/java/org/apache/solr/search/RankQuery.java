begin_unit
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements. See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License. You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|TopDocsCollector
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
name|Query
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
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  *<b>Note: This API is experimental and may change in non backward-compatible ways in the future</b>  **/
end_comment
begin_class
DECL|class|RankQuery
specifier|public
specifier|abstract
class|class
name|RankQuery
extends|extends
name|ExtendedQueryBase
block|{
DECL|method|getTopDocsCollector
specifier|public
specifier|abstract
name|TopDocsCollector
name|getTopDocsCollector
parameter_list|(
name|int
name|len
parameter_list|,
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getMergeStrategy
specifier|public
specifier|abstract
name|MergeStrategy
name|getMergeStrategy
parameter_list|()
function_decl|;
DECL|method|wrap
specifier|public
specifier|abstract
name|RankQuery
name|wrap
parameter_list|(
name|Query
name|mainQuery
parameter_list|)
function_decl|;
block|}
end_class
end_unit
