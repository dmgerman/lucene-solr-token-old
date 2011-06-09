begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
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
package|;
end_package
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
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
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
name|Node
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
name|NodeList
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
import|import static
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
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *<p>  * Mapping for data-config.xml  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|DataConfig
specifier|public
class|class
name|DataConfig
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|document
specifier|public
name|Document
name|document
decl_stmt|;
DECL|field|functions
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
name|functions
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
argument_list|()
decl_stmt|;
DECL|field|script
specifier|public
name|Script
name|script
decl_stmt|;
DECL|field|dataSources
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
name|dataSources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|lowerNameVsSchemaField
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|lowerNameVsSchemaField
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|isMultiThreaded
name|boolean
name|isMultiThreaded
init|=
literal|false
decl_stmt|;
DECL|class|Document
specifier|public
specifier|static
class|class
name|Document
block|{
comment|// TODO - remove from here and add it to entity
DECL|field|deleteQuery
specifier|public
name|String
name|deleteQuery
decl_stmt|;
DECL|field|entities
specifier|public
name|List
argument_list|<
name|Entity
argument_list|>
name|entities
init|=
operator|new
name|ArrayList
argument_list|<
name|Entity
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|onImportStart
DECL|field|onImportEnd
specifier|public
name|String
name|onImportStart
decl_stmt|,
name|onImportEnd
decl_stmt|;
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{     }
DECL|method|Document
specifier|public
name|Document
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|this
operator|.
name|deleteQuery
operator|=
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
name|Element
argument_list|>
name|l
init|=
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"entity"
argument_list|)
decl_stmt|;
for|for
control|(
name|Element
name|e
range|:
name|l
control|)
name|entities
operator|.
name|add
argument_list|(
operator|new
name|Entity
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Entity
specifier|public
specifier|static
class|class
name|Entity
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|pk
specifier|public
name|String
name|pk
decl_stmt|;
DECL|field|pkMappingFromSchema
specifier|public
name|String
name|pkMappingFromSchema
decl_stmt|;
DECL|field|dataSource
specifier|public
name|String
name|dataSource
decl_stmt|;
DECL|field|allAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allAttributes
decl_stmt|;
DECL|field|proc
specifier|public
name|String
name|proc
decl_stmt|;
DECL|field|docRoot
specifier|public
name|String
name|docRoot
decl_stmt|;
DECL|field|isDocRoot
specifier|public
name|boolean
name|isDocRoot
init|=
literal|false
decl_stmt|;
DECL|field|fields
specifier|public
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allFieldsList
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
name|allFieldsList
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
argument_list|()
decl_stmt|;
DECL|field|entities
specifier|public
name|List
argument_list|<
name|Entity
argument_list|>
name|entities
decl_stmt|;
DECL|field|parentEntity
specifier|public
name|Entity
name|parentEntity
decl_stmt|;
DECL|field|processor
specifier|public
name|EntityProcessorWrapper
name|processor
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|dataSrc
specifier|public
name|DataSource
name|dataSrc
decl_stmt|;
DECL|field|colNameVsField
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Field
argument_list|>
argument_list|>
name|colNameVsField
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Field
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Entity
specifier|public
name|Entity
parameter_list|()
block|{     }
DECL|method|Entity
specifier|public
name|Entity
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|name
operator|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Entity does not have a name"
argument_list|)
expr_stmt|;
name|name
operator|=
literal|""
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Entity name must not have period (.): '"
operator|+
name|name
argument_list|)
throw|;
block|}
if|if
condition|(
name|RESERVED_WORDS
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Entity name : '"
operator|+
name|name
operator|+
literal|"' is a reserved keyword. Reserved words are: "
operator|+
name|RESERVED_WORDS
argument_list|)
throw|;
block|}
name|pk
operator|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
literal|"pk"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|docRoot
operator|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|ROOT_ENTITY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|proc
operator|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|PROCESSOR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dataSource
operator|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|DataImporter
operator|.
name|DATA_SRC
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|allAttributes
operator|=
name|getAllAttributes
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Element
argument_list|>
name|n
init|=
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
for|for
control|(
name|Element
name|elem
range|:
name|n
control|)
block|{
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
name|elem
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Field
argument_list|>
name|l
init|=
name|colNameVsField
operator|.
name|get
argument_list|(
name|field
operator|.
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
name|l
operator|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
expr_stmt|;
name|boolean
name|alreadyFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|l
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|alreadyFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|alreadyFound
condition|)
name|l
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|colNameVsField
operator|.
name|put
argument_list|(
name|field
operator|.
name|column
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|n
operator|=
name|getChildNodes
argument_list|(
name|element
argument_list|,
literal|"entity"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|isEmpty
argument_list|()
condition|)
name|entities
operator|=
operator|new
name|ArrayList
argument_list|<
name|Entity
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Element
name|elem
range|:
name|n
control|)
name|entities
operator|.
name|add
argument_list|(
operator|new
name|Entity
argument_list|(
name|elem
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|clearCache
specifier|public
name|void
name|clearCache
parameter_list|()
block|{
if|if
condition|(
name|entities
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entity
name|entity
range|:
name|entities
control|)
name|entity
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|processor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/*no op*/
block|}
name|processor
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|dataSrc
operator|!=
literal|null
condition|)
name|dataSrc
operator|.
name|close
argument_list|()
expr_stmt|;
name|dataSrc
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getPk
specifier|public
name|String
name|getPk
parameter_list|()
block|{
return|return
name|pk
operator|==
literal|null
condition|?
name|pkMappingFromSchema
else|:
name|pk
return|;
block|}
DECL|method|getSchemaPk
specifier|public
name|String
name|getSchemaPk
parameter_list|()
block|{
return|return
name|pkMappingFromSchema
operator|!=
literal|null
condition|?
name|pkMappingFromSchema
else|:
name|pk
return|;
block|}
block|}
DECL|class|Script
specifier|public
specifier|static
class|class
name|Script
block|{
DECL|field|language
specifier|public
name|String
name|language
decl_stmt|;
DECL|field|text
specifier|public
name|String
name|text
decl_stmt|;
DECL|method|Script
specifier|public
name|Script
parameter_list|()
block|{     }
DECL|method|Script
specifier|public
name|Script
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|this
operator|.
name|language
operator|=
name|getStringAttribute
argument_list|(
name|e
argument_list|,
literal|"language"
argument_list|,
literal|"JavaScript"
argument_list|)
expr_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|script
init|=
name|getTxt
argument_list|(
name|e
argument_list|,
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
name|this
operator|.
name|text
operator|=
name|script
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Field
specifier|public
specifier|static
class|class
name|Field
block|{
DECL|field|column
specifier|public
name|String
name|column
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|boost
specifier|public
name|Float
name|boost
init|=
literal|1.0f
decl_stmt|;
DECL|field|toWrite
specifier|public
name|boolean
name|toWrite
init|=
literal|true
decl_stmt|;
DECL|field|multiValued
specifier|public
name|boolean
name|multiValued
init|=
literal|false
decl_stmt|;
DECL|field|dynamicName
name|boolean
name|dynamicName
decl_stmt|;
DECL|field|allAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|allAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
return|return
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
return|return
name|super
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|Field
specifier|public
name|Field
parameter_list|()
block|{     }
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|getStringAttribute
argument_list|(
name|e
argument_list|,
name|DataImporter
operator|.
name|NAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|getStringAttribute
argument_list|(
name|e
argument_list|,
name|DataImporter
operator|.
name|COLUMN
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|column
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"Field must have a column attribute"
argument_list|)
throw|;
block|}
name|this
operator|.
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|getStringAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|"1.0f"
argument_list|)
argument_list|)
expr_stmt|;
name|allAttributes
operator|.
name|putAll
argument_list|(
name|getAllAttributes
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
operator|==
literal|null
condition|?
name|column
else|:
name|name
return|;
block|}
DECL|field|entity
specifier|public
name|Entity
name|entity
decl_stmt|;
block|}
DECL|method|readFromXml
specifier|public
name|void
name|readFromXml
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|List
argument_list|<
name|Element
argument_list|>
name|n
init|=
name|getChildNodes
argument_list|(
name|e
argument_list|,
literal|"document"
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"DataImportHandler "
operator|+
literal|"configuration file must have one<document> node."
argument_list|)
throw|;
block|}
name|document
operator|=
operator|new
name|Document
argument_list|(
name|n
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|n
operator|=
name|getChildNodes
argument_list|(
name|e
argument_list|,
name|SCRIPT
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|script
operator|=
operator|new
name|Script
argument_list|(
name|n
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add the provided evaluators
name|n
operator|=
name|getChildNodes
argument_list|(
name|e
argument_list|,
name|FUNCTION
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Element
name|element
range|:
name|n
control|)
block|{
name|String
name|func
init|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|clz
init|=
name|getStringAttribute
argument_list|(
name|element
argument_list|,
name|CLASS
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|func
operator|==
literal|null
operator|||
name|clz
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"<function> must have a 'name' and 'class' attributes"
argument_list|)
throw|;
block|}
else|else
block|{
name|functions
operator|.
name|add
argument_list|(
name|getAllAttributes
argument_list|(
name|element
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|n
operator|=
name|getChildNodes
argument_list|(
name|e
argument_list|,
name|DATA_SRC
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Element
name|element
range|:
name|n
control|)
block|{
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrs
init|=
name|getAllAttributes
argument_list|(
name|element
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|attrs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dataSources
operator|.
name|put
argument_list|(
name|p
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dataSources
operator|.
name|get
argument_list|(
literal|null
argument_list|)
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|Properties
name|properties
range|:
name|dataSources
operator|.
name|values
argument_list|()
control|)
block|{
name|dataSources
operator|.
name|put
argument_list|(
literal|null
argument_list|,
name|properties
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|getStringAttribute
specifier|private
specifier|static
name|String
name|getStringAttribute
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|r
init|=
name|e
operator|.
name|getAttribute
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|r
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
name|r
operator|=
name|def
expr_stmt|;
return|return
name|r
return|;
block|}
DECL|method|getAllAttributes
specifier|private
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAllAttributes
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|nnm
init|=
name|e
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nnm
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|getTxt
specifier|public
specifier|static
name|String
name|getTxt
parameter_list|(
name|Node
name|elem
parameter_list|,
name|StringBuilder
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|elem
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
name|NodeList
name|childs
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|childs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|child
init|=
name|childs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|short
name|childType
init|=
name|child
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|childType
operator|!=
name|Node
operator|.
name|COMMENT_NODE
operator|&&
name|childType
operator|!=
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
condition|)
block|{
name|getTxt
argument_list|(
name|child
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|elem
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getChildNodes
specifier|public
specifier|static
name|List
argument_list|<
name|Element
argument_list|>
name|getChildNodes
parameter_list|(
name|Element
name|e
parameter_list|,
name|String
name|byName
parameter_list|)
block|{
name|List
argument_list|<
name|Element
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Element
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|l
init|=
name|e
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|e
operator|.
name|equals
argument_list|(
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getParentNode
argument_list|()
argument_list|)
operator|&&
name|byName
operator|.
name|equals
argument_list|(
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
operator|(
name|Element
operator|)
name|l
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|clearCaches
specifier|public
name|void
name|clearCaches
parameter_list|()
block|{
for|for
control|(
name|Entity
name|entity
range|:
name|document
operator|.
name|entities
control|)
name|entity
operator|.
name|clearCache
argument_list|()
expr_stmt|;
block|}
DECL|field|SCRIPT
specifier|public
specifier|static
specifier|final
name|String
name|SCRIPT
init|=
literal|"script"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|PROCESSOR
specifier|public
specifier|static
specifier|final
name|String
name|PROCESSOR
init|=
literal|"processor"
decl_stmt|;
comment|/**    * @deprecated use IMPORTER_NS_SHORT instead    */
annotation|@
name|Deprecated
DECL|field|IMPORTER_NS
specifier|public
specifier|static
specifier|final
name|String
name|IMPORTER_NS
init|=
literal|"dataimporter"
decl_stmt|;
DECL|field|IMPORTER_NS_SHORT
specifier|public
specifier|static
specifier|final
name|String
name|IMPORTER_NS_SHORT
init|=
literal|"dih"
decl_stmt|;
DECL|field|ROOT_ENTITY
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_ENTITY
init|=
literal|"rootEntity"
decl_stmt|;
DECL|field|FUNCTION
specifier|public
specifier|static
specifier|final
name|String
name|FUNCTION
init|=
literal|"function"
decl_stmt|;
DECL|field|CLASS
specifier|public
specifier|static
specifier|final
name|String
name|CLASS
init|=
literal|"class"
decl_stmt|;
DECL|field|DATA_SRC
specifier|public
specifier|static
specifier|final
name|String
name|DATA_SRC
init|=
literal|"dataSource"
decl_stmt|;
DECL|field|RESERVED_WORDS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|RESERVED_WORDS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
name|IMPORTER_NS
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
name|IMPORTER_NS_SHORT
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
literal|"request"
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
literal|"delta"
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
literal|"functions"
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
literal|"session"
argument_list|)
expr_stmt|;
name|RESERVED_WORDS
operator|.
name|add
argument_list|(
name|SolrWriter
operator|.
name|LAST_INDEX_KEY
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
