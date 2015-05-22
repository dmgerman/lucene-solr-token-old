begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
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
name|lucene
operator|.
name|index
operator|.
name|StorableField
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
name|SolrInputDocument
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
name|SolrInputField
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
name|CopyField
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|DocumentBuilder
specifier|public
class|class
name|DocumentBuilder
block|{
DECL|method|addField
specifier|private
specifier|static
name|void
name|addField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|StorableField
condition|)
block|{
comment|// set boost to the calculated compound boost
operator|(
operator|(
name|Field
operator|)
name|val
operator|)
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|(
name|Field
operator|)
name|val
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|StorableField
name|f
range|:
name|field
operator|.
name|getType
argument_list|()
operator|.
name|createFields
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
control|)
block|{
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
name|doc
operator|.
name|add
argument_list|(
operator|(
name|Field
operator|)
name|f
argument_list|)
expr_stmt|;
comment|// null fields are not added
block|}
block|}
DECL|method|getID
specifier|private
specifier|static
name|String
name|getID
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|String
name|id
init|=
literal|""
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|id
operator|=
literal|"[doc="
operator|+
name|doc
operator|.
name|getFieldValue
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
literal|"] "
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/**    * Convert a SolrInputDocument to a lucene Document.    *     * This function should go elsewhere.  This builds the Document without an    * extra Map&lt;&gt; checking for multiple values.  For more discussion, see:    * http://www.nabble.com/Re%3A-svn-commit%3A-r547493---in--lucene-solr-trunk%3A-.--src-java-org-apache-solr-common--src-java-org-apache-solr-schema--src-java-org-apache-solr-update--src-test-org-apache-solr-common--tf3931539.html    *     * TODO: /!\ NOTE /!\ This semantics of this function are still in flux.      * Something somewhere needs to be able to fill up a SolrDocument from    * a lucene document - this is one place that may happen.  It may also be    * moved to an independent function    *     * @since solr 1.3    */
DECL|method|toDocument
specifier|public
specifier|static
name|Document
name|toDocument
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|Document
name|out
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|float
name|docBoost
init|=
name|doc
operator|.
name|getDocumentBoost
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|usedFields
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
comment|// Load fields from SolrDocument to Document
for|for
control|(
name|SolrInputField
name|field
range|:
name|doc
control|)
block|{
name|String
name|name
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
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
name|boolean
name|used
init|=
literal|false
decl_stmt|;
comment|// Make sure it has the correct number
if|if
condition|(
name|sfield
operator|!=
literal|null
operator|&&
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
operator|&&
name|field
operator|.
name|getValueCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
name|float
name|fieldBoost
init|=
name|field
operator|.
name|getBoost
argument_list|()
decl_stmt|;
name|boolean
name|applyBoost
init|=
name|sfield
operator|!=
literal|null
operator|&&
name|sfield
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|sfield
operator|.
name|omitNorms
argument_list|()
decl_stmt|;
if|if
condition|(
name|applyBoost
operator|==
literal|false
operator|&&
name|fieldBoost
operator|!=
literal|1.0F
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"cannot set an index-time boost, unindexed or norms are omitted for field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|field
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
comment|// Lucene no longer has a native docBoost, so we have to multiply
comment|// it ourselves
name|float
name|compoundBoost
init|=
name|fieldBoost
operator|*
name|docBoost
decl_stmt|;
name|List
argument_list|<
name|CopyField
argument_list|>
name|copyFields
init|=
name|schema
operator|.
name|getCopyFieldsList
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|copyFields
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
name|copyFields
operator|=
literal|null
expr_stmt|;
comment|// load each field value
name|boolean
name|hasField
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|Object
name|v
range|:
name|field
control|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|hasField
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|used
operator|=
literal|true
expr_stmt|;
name|addField
argument_list|(
name|out
argument_list|,
name|sfield
argument_list|,
name|v
argument_list|,
name|applyBoost
condition|?
name|compoundBoost
else|:
literal|1f
argument_list|)
expr_stmt|;
comment|// record the field as having a value
name|usedFields
operator|.
name|add
argument_list|(
name|sfield
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Check if we should copy this field value to any other fields.
comment|// This could happen whether it is explicit or not.
if|if
condition|(
name|copyFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CopyField
name|cf
range|:
name|copyFields
control|)
block|{
name|SchemaField
name|destinationField
init|=
name|cf
operator|.
name|getDestination
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|destHasValues
init|=
name|usedFields
operator|.
name|contains
argument_list|(
name|destinationField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// check if the copy field is a multivalued or not
if|if
condition|(
operator|!
name|destinationField
operator|.
name|multiValued
argument_list|()
operator|&&
name|destHasValues
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"multiple values encountered for non multiValued copy field "
operator|+
name|destinationField
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|v
argument_list|)
throw|;
block|}
name|used
operator|=
literal|true
expr_stmt|;
comment|// Perhaps trim the length of a copy field
name|Object
name|val
init|=
name|v
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|String
operator|&&
name|cf
operator|.
name|getMaxChars
argument_list|()
operator|>
literal|0
condition|)
block|{
name|val
operator|=
name|cf
operator|.
name|getLimitedValue
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
comment|// we can't copy any boost unless the dest field is
comment|// indexed& !omitNorms, but which boost we copy depends
comment|// on whether the dest field already contains values (we
comment|// don't want to apply the compounded docBoost more then once)
specifier|final
name|float
name|destBoost
init|=
operator|(
name|destinationField
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|destinationField
operator|.
name|omitNorms
argument_list|()
operator|)
condition|?
operator|(
name|destHasValues
condition|?
name|fieldBoost
else|:
name|compoundBoost
operator|)
else|:
literal|1.0F
decl_stmt|;
name|addField
argument_list|(
name|out
argument_list|,
name|destinationField
argument_list|,
name|val
argument_list|,
name|destBoost
argument_list|)
expr_stmt|;
comment|// record the field as having a value
name|usedFields
operator|.
name|add
argument_list|(
name|destinationField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The final boost for a given field named is the product of the
comment|// *all* boosts on values of that field.
comment|// For multi-valued fields, we only want to set the boost on the
comment|// first field.
name|fieldBoost
operator|=
name|compoundBoost
operator|=
literal|1.0f
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"Error adding field '"
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|"'='"
operator|+
name|field
operator|.
name|getValue
argument_list|()
operator|+
literal|"' msg="
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
comment|// make sure the field was used somehow...
if|if
condition|(
operator|!
name|used
operator|&&
name|hasField
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"ERROR: "
operator|+
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// Now validate required fields or add default values
comment|// fields with default values are defacto 'required'
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getRequiredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|out
operator|.
name|getField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|out
argument_list|,
name|field
argument_list|,
name|field
operator|.
name|getDefaultValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msg
init|=
name|getID
argument_list|(
name|doc
argument_list|,
name|schema
argument_list|)
operator|+
literal|"missing required field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|out
return|;
block|}
block|}
end_class
end_unit
