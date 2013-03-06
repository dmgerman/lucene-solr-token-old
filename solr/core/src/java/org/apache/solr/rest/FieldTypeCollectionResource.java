begin_unit
begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
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
name|util
operator|.
name|SimpleOrderedMap
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
name|HashMap
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
name|TreeMap
import|;
end_import
begin_comment
comment|/**  * This class responds to requests at /solr/(corename)/schema/fieldtypes  *   * The GET method returns properties for all field types defined in the schema.  */
end_comment
begin_class
DECL|class|FieldTypeCollectionResource
specifier|public
class|class
name|FieldTypeCollectionResource
extends|extends
name|BaseFieldTypeResource
implements|implements
name|GETable
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
name|FieldTypeCollectionResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FIELD_TYPES
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_TYPES
init|=
literal|"fieldTypes"
decl_stmt|;
DECL|field|fieldsByFieldType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldsByFieldType
decl_stmt|;
DECL|field|dynamicFieldsByFieldType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|dynamicFieldsByFieldType
decl_stmt|;
DECL|method|FieldTypeCollectionResource
specifier|public
name|FieldTypeCollectionResource
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
name|fieldsByFieldType
operator|=
name|getFieldsByFieldType
argument_list|()
expr_stmt|;
name|dynamicFieldsByFieldType
operator|=
name|getDynamicFieldsByFieldType
argument_list|()
expr_stmt|;
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
name|List
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|props
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|sortedFieldTypes
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
argument_list|(
name|getSchema
argument_list|()
operator|.
name|getFieldTypes
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|FieldType
name|fieldType
range|:
name|sortedFieldTypes
operator|.
name|values
argument_list|()
control|)
block|{
name|props
operator|.
name|add
argument_list|(
name|getFieldTypeProperties
argument_list|(
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|getSolrResponse
argument_list|()
operator|.
name|add
argument_list|(
name|FIELD_TYPES
argument_list|,
name|props
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
comment|/** Returns field lists from the map constructed in doInit() */
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
name|fieldsByFieldType
operator|.
name|get
argument_list|(
name|fieldType
operator|.
name|getTypeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fields
condition|)
block|{
name|fields
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
comment|/** Returns dynamic field lists from the map constructed in doInit() */
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
name|dynamicFieldsByFieldType
operator|.
name|get
argument_list|(
name|fieldType
operator|.
name|getTypeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|dynamicFields
condition|)
block|{
name|dynamicFields
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
return|return
name|dynamicFields
return|;
block|}
comment|/**    * Returns a map from field type names to a sorted list of fields that use the field type.    * The map only includes field types that are used by at least one field.      */
DECL|method|getFieldsByFieldType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getFieldsByFieldType
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldsByFieldType
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
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
specifier|final
name|String
name|fieldType
init|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|fieldsByFieldType
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|fields
condition|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|fieldsByFieldType
operator|.
name|put
argument_list|(
name|fieldType
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
range|:
name|fieldsByFieldType
operator|.
name|values
argument_list|()
control|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldsByFieldType
return|;
block|}
comment|/**    * Returns a map from field type names to a list of dynamic fields that use the field type.    * The map only includes field types that are used by at least one dynamic field.      */
DECL|method|getDynamicFieldsByFieldType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getDynamicFieldsByFieldType
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|dynamicFieldsByFieldType
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
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
name|getDynamicFieldPrototypes
argument_list|()
control|)
block|{
specifier|final
name|String
name|fieldType
init|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dynamicFields
init|=
name|dynamicFieldsByFieldType
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|dynamicFields
condition|)
block|{
name|dynamicFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|dynamicFieldsByFieldType
operator|.
name|put
argument_list|(
name|fieldType
argument_list|,
name|dynamicFields
argument_list|)
expr_stmt|;
block|}
name|dynamicFields
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
return|return
name|dynamicFieldsByFieldType
return|;
block|}
block|}
end_class
end_unit
