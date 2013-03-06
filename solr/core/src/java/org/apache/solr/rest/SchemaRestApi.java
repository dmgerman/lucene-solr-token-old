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
name|restlet
operator|.
name|Application
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|Restlet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|routing
operator|.
name|Router
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
begin_class
DECL|class|SchemaRestApi
specifier|public
class|class
name|SchemaRestApi
extends|extends
name|Application
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SchemaRestApi
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS
init|=
literal|"fields"
decl_stmt|;
DECL|field|FIELDS_PATH
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_PATH
init|=
literal|"/"
operator|+
name|FIELDS
decl_stmt|;
DECL|field|DYNAMIC_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_FIELDS
init|=
literal|"dynamicfields"
decl_stmt|;
DECL|field|DYNAMIC_FIELDS_PATH
specifier|public
specifier|static
specifier|final
name|String
name|DYNAMIC_FIELDS_PATH
init|=
literal|"/"
operator|+
name|DYNAMIC_FIELDS
decl_stmt|;
DECL|field|FIELDTYPES
specifier|public
specifier|static
specifier|final
name|String
name|FIELDTYPES
init|=
literal|"fieldtypes"
decl_stmt|;
DECL|field|FIELDTYPES_PATH
specifier|public
specifier|static
specifier|final
name|String
name|FIELDTYPES_PATH
init|=
literal|"/"
operator|+
name|FIELDTYPES
decl_stmt|;
DECL|field|NAME_VARIABLE
specifier|public
specifier|static
specifier|final
name|String
name|NAME_VARIABLE
init|=
literal|"name"
decl_stmt|;
DECL|field|NAME_SEGMENT
specifier|public
specifier|static
specifier|final
name|String
name|NAME_SEGMENT
init|=
literal|"/{"
operator|+
name|NAME_VARIABLE
operator|+
literal|"}"
decl_stmt|;
DECL|field|COPY_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|COPY_FIELDS
init|=
literal|"copyfields"
decl_stmt|;
DECL|field|COPY_FIELDS_PATH
specifier|public
specifier|static
specifier|final
name|String
name|COPY_FIELDS_PATH
init|=
literal|"/"
operator|+
name|COPY_FIELDS
decl_stmt|;
DECL|field|router
specifier|private
name|Router
name|router
decl_stmt|;
DECL|method|SchemaRestApi
specifier|public
name|SchemaRestApi
parameter_list|()
block|{
name|router
operator|=
operator|new
name|Router
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|router
operator|!=
literal|null
condition|)
block|{
name|router
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Bind URL paths to the appropriate ServerResource subclass.     */
annotation|@
name|Override
DECL|method|createInboundRoot
specifier|public
specifier|synchronized
name|Restlet
name|createInboundRoot
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"createInboundRoot started"
argument_list|)
expr_stmt|;
name|router
operator|.
name|attachDefault
argument_list|(
name|DefaultSchemaResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|FIELDS_PATH
argument_list|,
name|FieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Allow a trailing slash on collection requests
name|router
operator|.
name|attach
argument_list|(
name|FIELDS_PATH
operator|+
literal|"/"
argument_list|,
name|FieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|FIELDS_PATH
operator|+
name|NAME_SEGMENT
argument_list|,
name|FieldResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|DYNAMIC_FIELDS_PATH
argument_list|,
name|DynamicFieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Allow a trailing slash on collection requests
name|router
operator|.
name|attach
argument_list|(
name|DYNAMIC_FIELDS_PATH
operator|+
literal|"/"
argument_list|,
name|DynamicFieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|DYNAMIC_FIELDS_PATH
operator|+
name|NAME_SEGMENT
argument_list|,
name|DynamicFieldResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|FIELDTYPES_PATH
argument_list|,
name|FieldTypeCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Allow a trailing slash on collection requests
name|router
operator|.
name|attach
argument_list|(
name|FIELDTYPES_PATH
operator|+
literal|"/"
argument_list|,
name|FieldTypeCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|FIELDTYPES_PATH
operator|+
name|NAME_SEGMENT
argument_list|,
name|FieldTypeResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|router
operator|.
name|attach
argument_list|(
name|COPY_FIELDS_PATH
argument_list|,
name|CopyFieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Allow a trailing slash on collection requests
name|router
operator|.
name|attach
argument_list|(
name|COPY_FIELDS_PATH
operator|+
literal|"/"
argument_list|,
name|CopyFieldCollectionResource
operator|.
name|class
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"createInboundRoot complete"
argument_list|)
expr_stmt|;
return|return
name|router
return|;
block|}
block|}
end_class
end_unit
