begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.cell
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|cell
package|;
end_package
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|extraction
operator|.
name|SolrContentHandler
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
name|extraction
operator|.
name|SolrContentHandlerFactory
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
name|IndexSchema
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
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import
begin_comment
comment|/**  * {@link SolrContentHandler} and associated factory that trims field values on output.  * This prevents exceptions on parsing integer fields inside Solr server.  */
end_comment
begin_class
DECL|class|TrimSolrContentHandlerFactory
specifier|public
class|class
name|TrimSolrContentHandlerFactory
extends|extends
name|SolrContentHandlerFactory
block|{
DECL|method|TrimSolrContentHandlerFactory
specifier|public
name|TrimSolrContentHandlerFactory
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|)
block|{
name|super
argument_list|(
name|dateFormats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSolrContentHandler
specifier|public
name|SolrContentHandler
name|createSolrContentHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
return|return
operator|new
name|TrimSolrContentHandler
argument_list|(
name|metadata
argument_list|,
name|params
argument_list|,
name|schema
argument_list|,
name|dateFormats
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|TrimSolrContentHandler
specifier|private
specifier|static
specifier|final
class|class
name|TrimSolrContentHandler
extends|extends
name|SolrContentHandler
block|{
DECL|method|TrimSolrContentHandler
specifier|public
name|TrimSolrContentHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|dateFormats
parameter_list|)
block|{
name|super
argument_list|(
name|metadata
argument_list|,
name|params
argument_list|,
name|schema
argument_list|,
name|dateFormats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transformValue
specifier|protected
name|String
name|transformValue
parameter_list|(
name|String
name|val
parameter_list|,
name|SchemaField
name|schemaField
parameter_list|)
block|{
return|return
name|super
operator|.
name|transformValue
argument_list|(
name|val
argument_list|,
name|schemaField
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit