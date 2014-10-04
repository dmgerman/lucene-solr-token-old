begin_unit
begin_package
DECL|package|org.apache.solr.rest.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|rest
operator|.
name|GETable
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
name|rest
operator|.
name|PUTable
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
name|ManagedIndexSchema
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
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|data
operator|.
name|MediaType
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|representation
operator|.
name|Representation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|resource
operator|.
name|ResourceException
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
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
begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/fieldtype/(typename)  * where "typename" is the name of a field type in the schema.  *   * The GET method returns properties for the named field type.  */
end_comment
begin_class
DECL|class|FieldTypeResource
specifier|public
class|class
name|FieldTypeResource
extends|extends
name|BaseFieldTypeResource
implements|implements
name|GETable
implements|,
name|PUTable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldTypeResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|typeName
specifier|private
name|String
name|typeName
decl_stmt|;
DECL|method|FieldTypeResource
specifier|public
name|FieldTypeResource
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|public
name|void
name|doInit
parameter_list|()
throws|throws
name|ResourceException
block|{
name|super
operator|.
name|doInit
argument_list|()
expr_stmt|;
if|if
condition|(
name|isExisting
argument_list|()
condition|)
block|{
name|typeName
operator|=
operator|(
name|String
operator|)
name|getRequestAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|)
expr_stmt|;
try|try
block|{
name|typeName
operator|=
literal|null
operator|==
name|typeName
condition|?
literal|""
else|:
name|urlDecode
argument_list|(
name|typeName
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Representation
name|get
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|typeName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field type name is missing"
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
else|else
block|{
name|FieldType
name|fieldType
init|=
name|getSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|get
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fieldType
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Field type '"
operator|+
name|typeName
operator|+
literal|"' not found."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|IndexSchema
operator|.
name|FIELD_TYPE
argument_list|,
name|getFieldTypeProperties
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
comment|/**     * Returns a field list using the given field type by iterating over all fields    * defined in the schema.    */
annotation|@
name|Override
DECL|method|getFieldsWithFieldType
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getFieldsWithFieldType
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchemaField
name|schemaField
range|:
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldType
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|fields
return|;
block|}
comment|/**    * Returns a dynamic field list using the given field type by iterating over all    * dynamic fields defined in the schema.     */
annotation|@
name|Override
DECL|method|getDynamicFieldsWithFieldType
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getDynamicFieldsWithFieldType
parameter_list|(
name|FieldType
name|fieldType
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|dynamicFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SchemaField
name|prototype
range|:
name|getSchema
argument_list|()
operator|.
name|getDynamicFieldPrototypes
argument_list|()
control|)
block|{
if|if
condition|(
name|prototype
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldType
operator|.
name|getTypeName
argument_list|()
argument_list|)
condition|)
block|{
name|dynamicFields
operator|.
name|add
argument_list|(
name|prototype
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dynamicFields
return|;
comment|// Don't sort these - they're matched in order
block|}
comment|/**    * Accepts JSON add fieldtype request, to URL    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|put
specifier|public
name|Representation
name|put
parameter_list|(
name|Representation
name|entity
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|getSchema
argument_list|()
operator|.
name|isMutable
argument_list|()
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"This IndexSchema is not mutable."
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|entity
operator|.
name|getMediaType
argument_list|()
condition|)
name|entity
operator|.
name|setMediaType
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|equals
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Only media type "
operator|+
name|MediaType
operator|.
name|APPLICATION_JSON
operator|.
name|toString
argument_list|()
operator|+
literal|" is accepted."
operator|+
literal|"  Request has media type "
operator|+
name|entity
operator|.
name|getMediaType
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
name|Object
name|object
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|entity
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|object
operator|instanceof
name|Map
operator|)
condition|)
block|{
name|String
name|message
init|=
literal|"Invalid JSON type "
operator|+
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected Map of the form"
operator|+
literal|" (ignore the backslashes): {\"name\":\"text_general\", \"class\":\"solr.TextField\" ...},"
operator|+
literal|" either with or without a \"name\" mapping.  If the \"name\" is specified, it must match the"
operator|+
literal|" name given in the request URL: /schema/fieldtypes/(name)"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
comment|// basic validation passed, let's try to create it!
name|addOrUpdateFieldType
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getSolrResponse
argument_list|()
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|handlePostExecution
argument_list|(
name|log
argument_list|)
expr_stmt|;
return|return
operator|new
name|SolrOutputRepresentation
argument_list|()
return|;
block|}
DECL|method|addOrUpdateFieldType
specifier|protected
name|void
name|addOrUpdateFieldType
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldTypeJson
parameter_list|)
block|{
name|ManagedIndexSchema
name|oldSchema
init|=
operator|(
name|ManagedIndexSchema
operator|)
name|getSchema
argument_list|()
decl_stmt|;
name|FieldType
name|newFieldType
init|=
name|buildFieldTypeFromJson
argument_list|(
name|oldSchema
argument_list|,
name|typeName
argument_list|,
name|fieldTypeJson
argument_list|)
decl_stmt|;
name|addNewFieldTypes
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|newFieldType
argument_list|)
argument_list|,
name|oldSchema
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds a FieldType definition from a JSON object.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|buildFieldTypeFromJson
specifier|static
name|FieldType
name|buildFieldTypeFromJson
parameter_list|(
name|ManagedIndexSchema
name|oldSchema
parameter_list|,
name|String
name|fieldTypeName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldTypeJson
parameter_list|)
block|{
if|if
condition|(
literal|1
operator|==
name|fieldTypeJson
operator|.
name|size
argument_list|()
operator|&&
name|fieldTypeJson
operator|.
name|containsKey
argument_list|(
name|IndexSchema
operator|.
name|FIELD_TYPE
argument_list|)
condition|)
block|{
name|fieldTypeJson
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldTypeJson
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
block|}
name|String
name|bodyTypeName
init|=
operator|(
name|String
operator|)
name|fieldTypeJson
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|bodyTypeName
operator|==
literal|null
condition|)
block|{
comment|// must provide the name in the JSON for converting to the XML format needed
comment|// to create FieldType objects using the FieldTypePluginLoader
name|fieldTypeJson
operator|.
name|put
argument_list|(
name|IndexSchema
operator|.
name|NAME
argument_list|,
name|fieldTypeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if they provide it in the JSON, then it must match the value from the path
if|if
condition|(
operator|!
name|fieldTypeName
operator|.
name|equals
argument_list|(
name|bodyTypeName
argument_list|)
condition|)
block|{
name|String
name|message
init|=
literal|"Field type name in the request body '"
operator|+
name|bodyTypeName
operator|+
literal|"' doesn't match field type name in the request URL '"
operator|+
name|fieldTypeName
operator|+
literal|"'"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
block|}
name|String
name|className
init|=
operator|(
name|String
operator|)
name|fieldTypeJson
operator|.
name|get
argument_list|(
name|FieldType
operator|.
name|CLASS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
name|String
name|message
init|=
literal|"Missing required '"
operator|+
name|FieldType
operator|.
name|CLASS_NAME
operator|+
literal|"' property!"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|message
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|message
argument_list|)
throw|;
block|}
return|return
name|oldSchema
operator|.
name|newFieldType
argument_list|(
name|fieldTypeName
argument_list|,
name|className
argument_list|,
name|fieldTypeJson
argument_list|)
return|;
block|}
block|}
end_class
end_unit
