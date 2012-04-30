begin_unit
begin_package
DECL|package|org.apache.solr.handler.dataimport.config
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|config
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|dataimport
operator|.
name|DataImporter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *<p>  * Mapping for data-config.xml  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|DIHConfiguration
specifier|public
class|class
name|DIHConfiguration
block|{
comment|// TODO - remove from here and add it to entity
DECL|field|deleteQuery
specifier|private
specifier|final
name|String
name|deleteQuery
decl_stmt|;
DECL|field|entities
specifier|private
specifier|final
name|List
argument_list|<
name|Entity
argument_list|>
name|entities
decl_stmt|;
DECL|field|onImportStart
specifier|private
specifier|final
name|String
name|onImportStart
decl_stmt|;
DECL|field|onImportEnd
specifier|private
specifier|final
name|String
name|onImportEnd
decl_stmt|;
DECL|field|functions
specifier|private
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|functions
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|dataSources
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
name|dataSources
decl_stmt|;
DECL|method|DIHConfiguration
specifier|public
name|DIHConfiguration
parameter_list|(
name|Element
name|element
parameter_list|,
name|DataImporter
name|di
parameter_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|functions
parameter_list|,
name|Script
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
name|dataSources
parameter_list|)
block|{
name|this
operator|.
name|deleteQuery
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
literal|"deleteQuery"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|onImportStart
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
literal|"onImportStart"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|onImportEnd
operator|=
name|ConfigParseUtil
operator|.
name|getStringAttribute
argument_list|(
name|element
argument_list|,
literal|"onImportEnd"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Entity
argument_list|>
name|modEntities
init|=
operator|new
name|ArrayList
argument_list|<
name|Entity
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Element
argument_list|>
name|l
init|=
name|ConfigParseUtil
operator|.
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"entity"
argument_list|)
decl_stmt|;
name|boolean
name|docRootFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Element
name|e
range|:
name|l
control|)
block|{
name|Entity
name|entity
init|=
operator|new
name|Entity
argument_list|(
name|docRootFound
argument_list|,
name|e
argument_list|,
name|di
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|EntityField
argument_list|>
name|fields
init|=
name|ConfigParseUtil
operator|.
name|gatherAllFields
argument_list|(
name|di
argument_list|,
name|entity
argument_list|)
decl_stmt|;
name|ConfigParseUtil
operator|.
name|verifyWithSchema
argument_list|(
name|di
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|modEntities
operator|.
name|add
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|entities
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modEntities
argument_list|)
expr_stmt|;
if|if
condition|(
name|functions
operator|==
literal|null
condition|)
block|{
name|functions
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|modFunc
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|(
name|functions
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|f
range|:
name|functions
control|)
block|{
name|modFunc
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|functions
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|modFunc
argument_list|)
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|dataSources
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|dataSources
argument_list|)
expr_stmt|;
block|}
DECL|method|getDeleteQuery
specifier|public
name|String
name|getDeleteQuery
parameter_list|()
block|{
return|return
name|deleteQuery
return|;
block|}
DECL|method|getEntities
specifier|public
name|List
argument_list|<
name|Entity
argument_list|>
name|getEntities
parameter_list|()
block|{
return|return
name|entities
return|;
block|}
DECL|method|getOnImportStart
specifier|public
name|String
name|getOnImportStart
parameter_list|()
block|{
return|return
name|onImportStart
return|;
block|}
DECL|method|getOnImportEnd
specifier|public
name|String
name|getOnImportEnd
parameter_list|()
block|{
return|return
name|onImportEnd
return|;
block|}
DECL|method|getFunctions
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getFunctions
parameter_list|()
block|{
return|return
name|functions
return|;
block|}
DECL|method|getDataSources
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
name|getDataSources
parameter_list|()
block|{
return|return
name|dataSources
return|;
block|}
DECL|method|getScript
specifier|public
name|Script
name|getScript
parameter_list|()
block|{
return|return
name|script
return|;
block|}
block|}
end_class
end_unit
