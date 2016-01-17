begin_unit
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|index
operator|.
name|IndexableField
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
name|util
operator|.
name|BytesRef
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
name|SolrDocument
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
name|response
operator|.
name|transform
operator|.
name|DocTransformer
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
name|BinaryField
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
name|BoolField
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
name|schema
operator|.
name|StrField
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
name|TextField
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
name|TrieDateField
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
name|TrieDoubleField
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
name|TrieField
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
name|TrieFloatField
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
name|TrieIntField
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
name|TrieLongField
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
name|search
operator|.
name|DocIterator
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|SolrReturnFields
import|;
end_import
begin_comment
comment|/**  * This streams SolrDocuments from a DocList and applies transformer  */
end_comment
begin_class
DECL|class|DocsStreamer
specifier|public
class|class
name|DocsStreamer
implements|implements
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
block|{
DECL|field|KNOWN_TYPES
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|KNOWN_TYPES
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|rctx
specifier|private
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|ResultContext
name|rctx
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|DocList
name|docs
decl_stmt|;
DECL|field|transformer
specifier|private
name|DocTransformer
name|transformer
decl_stmt|;
DECL|field|docIterator
specifier|private
name|DocIterator
name|docIterator
decl_stmt|;
DECL|field|onlyPseudoFields
specifier|private
name|boolean
name|onlyPseudoFields
decl_stmt|;
DECL|field|fnames
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|fnames
decl_stmt|;
DECL|field|dvFieldsToReturn
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|dvFieldsToReturn
decl_stmt|;
DECL|field|idx
specifier|private
name|int
name|idx
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|DocsStreamer
specifier|public
name|DocsStreamer
parameter_list|(
name|ResultContext
name|rctx
parameter_list|)
block|{
name|this
operator|.
name|rctx
operator|=
name|rctx
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|rctx
operator|.
name|getDocList
argument_list|()
expr_stmt|;
name|transformer
operator|=
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|getTransformer
argument_list|()
expr_stmt|;
name|docIterator
operator|=
name|this
operator|.
name|docs
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|fnames
operator|=
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|getLuceneFieldNames
argument_list|()
expr_stmt|;
name|onlyPseudoFields
operator|=
operator|(
name|fnames
operator|==
literal|null
operator|&&
operator|!
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|wantsAllFields
argument_list|()
operator|&&
operator|!
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|hasPatternMatching
argument_list|()
operator|)
operator|||
operator|(
name|fnames
operator|!=
literal|null
operator|&&
name|fnames
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|SolrReturnFields
operator|.
name|SCORE
operator|.
name|equals
argument_list|(
name|fnames
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|)
expr_stmt|;
comment|// add non-stored DV fields that may have been requested
if|if
condition|(
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|wantsAllFields
argument_list|()
condition|)
block|{
comment|// check whether there are no additional fields
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|getLuceneFieldNames
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|dvFieldsToReturn
operator|=
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getNonStoredDVs
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dvFieldsToReturn
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getNonStoredDVs
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// copy
comment|// add all requested fields that may be useDocValuesAsStored=false
for|for
control|(
name|String
name|fl
range|:
name|fieldNames
control|)
block|{
if|if
condition|(
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getNonStoredDVs
argument_list|(
literal|false
argument_list|)
operator|.
name|contains
argument_list|(
name|fl
argument_list|)
condition|)
block|{
name|dvFieldsToReturn
operator|.
name|add
argument_list|(
name|fl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|hasPatternMatching
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getNonStoredDVs
argument_list|(
literal|true
argument_list|)
control|)
block|{
if|if
condition|(
name|rctx
operator|.
name|getReturnFields
argument_list|()
operator|.
name|wantsField
argument_list|(
name|s
argument_list|)
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|dvFieldsToReturn
condition|)
block|{
name|dvFieldsToReturn
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|dvFieldsToReturn
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|fnames
operator|!=
literal|null
condition|)
block|{
name|dvFieldsToReturn
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|fnames
argument_list|)
expr_stmt|;
comment|// copy
comment|// here we get all non-stored dv fields because even if a user has set
comment|// useDocValuesAsStored=false in schema, he may have requested a field
comment|// explicitly using the fl parameter
name|dvFieldsToReturn
operator|.
name|retainAll
argument_list|(
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getNonStoredDVs
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
name|transformer
operator|.
name|setContext
argument_list|(
name|rctx
argument_list|)
expr_stmt|;
block|}
DECL|method|currentIndex
specifier|public
name|int
name|currentIndex
parameter_list|()
block|{
return|return
name|idx
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|docIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|SolrDocument
name|next
parameter_list|()
block|{
name|int
name|id
init|=
name|docIterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|idx
operator|++
expr_stmt|;
name|SolrDocument
name|sdoc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|onlyPseudoFields
condition|)
block|{
comment|// no need to get stored fields of the document, see SOLR-5968
name|sdoc
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|Document
name|doc
init|=
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|doc
argument_list|(
name|id
argument_list|,
name|fnames
argument_list|)
decl_stmt|;
name|sdoc
operator|=
name|getDoc
argument_list|(
name|doc
argument_list|,
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure to use the schema from the searcher and not the request (cross-core)
comment|// decorate the document with non-stored docValues fields
if|if
condition|(
name|dvFieldsToReturn
operator|!=
literal|null
condition|)
block|{
name|rctx
operator|.
name|getSearcher
argument_list|()
operator|.
name|decorateDocValueFields
argument_list|(
name|sdoc
argument_list|,
name|id
argument_list|,
name|dvFieldsToReturn
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
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
name|SERVER_ERROR
argument_list|,
literal|"Error reading document with docId "
operator|+
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|boolean
name|doScore
init|=
name|rctx
operator|.
name|wantsScores
argument_list|()
decl_stmt|;
try|try
block|{
name|transformer
operator|.
name|transform
argument_list|(
name|sdoc
argument_list|,
name|id
argument_list|,
name|doScore
condition|?
name|docIterator
operator|.
name|score
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
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
name|SERVER_ERROR
argument_list|,
literal|"Error applying transformer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|sdoc
return|;
block|}
DECL|method|getDoc
specifier|public
specifier|static
name|SolrDocument
name|getDoc
parameter_list|(
name|Document
name|doc
parameter_list|,
specifier|final
name|IndexSchema
name|schema
parameter_list|)
block|{
name|SolrDocument
name|out
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
comment|// Make sure multivalued fields are represented as lists
name|Object
name|existing
init|=
name|out
operator|.
name|get
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|vals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|out
operator|.
name|setField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|setField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|addField
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//do nothing
block|}
DECL|method|getValue
specifier|public
specifier|static
name|Object
name|getValue
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|IndexableField
name|f
parameter_list|)
block|{
name|FieldType
name|ft
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
name|ft
operator|=
name|sf
operator|.
name|getType
argument_list|()
expr_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
comment|// handle fields not in the schema
name|BytesRef
name|bytesRef
init|=
name|f
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesRef
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bytesRef
operator|.
name|offset
operator|==
literal|0
operator|&&
name|bytesRef
operator|.
name|length
operator|==
name|bytesRef
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
return|return
name|bytesRef
operator|.
name|bytes
return|;
block|}
else|else
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|bytesRef
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
block|}
else|else
return|return
name|f
operator|.
name|stringValue
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|KNOWN_TYPES
operator|.
name|contains
argument_list|(
name|ft
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|ft
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ft
operator|.
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
block|}
block|}
block|}
static|static
block|{
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BoolField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|StrField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TextField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieIntField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieLongField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieFloatField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieDoubleField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieDateField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BinaryField
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// We do not add UUIDField because UUID object is not a supported type in JavaBinCodec
comment|// and if we write UUIDField.toObject, we wouldn't know how to handle it in the client side
block|}
block|}
end_class
end_unit
