begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|component
operator|.
name|QueryElevationComponent
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
name|request
operator|.
name|SolrQueryRequest
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
name|FieldType
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
begin_comment
comment|/**  * Used to mark whether a document has been elevated or not  * @since solr 4.0  */
end_comment
begin_class
DECL|class|ElevatedMarkerFactory
specifier|public
class|class
name|ElevatedMarkerFactory
extends|extends
name|TransformerFactory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|DocTransformer
name|create
parameter_list|(
name|String
name|field
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|SchemaField
name|uniqueKeyField
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|String
name|idfield
init|=
name|uniqueKeyField
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|new
name|MarkTransformer
argument_list|(
name|field
argument_list|,
name|idfield
argument_list|,
name|uniqueKeyField
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|MarkTransformer
class|class
name|MarkTransformer
extends|extends
name|BaseEditorialTransformer
block|{
DECL|method|MarkTransformer
name|MarkTransformer
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|idFieldName
parameter_list|,
name|FieldType
name|ft
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|idFieldName
argument_list|,
name|ft
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIdSet
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getIdSet
parameter_list|()
block|{
return|return
operator|(
name|Set
argument_list|<
name|String
argument_list|>
operator|)
name|context
operator|.
name|getRequest
argument_list|()
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|QueryElevationComponent
operator|.
name|BOOSTED
argument_list|)
return|;
block|}
block|}
end_class
end_unit
