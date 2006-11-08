begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|solr
operator|.
name|core
operator|.
name|SolrException
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
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_comment
comment|// Not thread safe - by design.  Create a new builder for each thread.
end_comment
begin_class
DECL|class|DocumentBuilder
specifier|public
class|class
name|DocumentBuilder
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|map
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
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
DECL|method|DocumentBuilder
specifier|public
name|DocumentBuilder
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|addSingleField
specifier|protected
name|void
name|addSingleField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|//System.out.println("###################ADDING FIELD "+sfield+"="+val);
comment|// we don't check for a null val ourselves because a solr.FieldType
comment|// might actually want to map it to something.  If createField()
comment|// returns null, then we don't store the field.
name|Field
name|field
init|=
name|sfield
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|String
name|oldValue
init|=
name|map
operator|.
name|put
argument_list|(
name|sfield
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": first='"
operator|+
name|oldValue
operator|+
literal|"' second='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// field.setBoost(boost);
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|addSingleField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|SchemaField
name|sfield
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|// Check if we should copy this field to any other fields.
comment|// This could happen whether it is explicit or not.
name|SchemaField
index|[]
name|destArr
init|=
name|schema
operator|.
name|getCopyFields
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|destArr
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SchemaField
name|destField
range|:
name|destArr
control|)
block|{
name|addSingleField
argument_list|(
name|destField
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|// error if this field name doesn't match anything
if|if
condition|(
name|sfield
operator|==
literal|null
operator|&&
operator|(
name|destArr
operator|==
literal|null
operator|||
name|destArr
operator|.
name|length
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"ERROR:unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|doc
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|endDoc
specifier|public
name|void
name|endDoc
parameter_list|()
block|{   }
comment|// specific to this type of document builder
DECL|method|getDoc
specifier|public
name|Document
name|getDoc
parameter_list|()
block|{
name|Document
name|ret
init|=
name|doc
decl_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
end_class
end_unit
