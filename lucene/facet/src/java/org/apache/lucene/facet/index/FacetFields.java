begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
package|;
end_package
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
name|HashMap
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|document
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
name|lucene
operator|.
name|document
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
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|FieldInfo
operator|.
name|IndexOptions
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A utility class for adding facet fields to a document. Usually one field will  * be added for all facets, however per the  * {@link FacetIndexingParams#getCategoryListParams(CategoryPath)}, one field  * may be added for every group of facets.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetFields
specifier|public
class|class
name|FacetFields
block|{
comment|// a TokenStream for writing the counting list payload
DECL|class|CountingListStream
specifier|private
specifier|static
specifier|final
class|class
name|CountingListStream
extends|extends
name|TokenStream
block|{
DECL|field|payloadAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAtt
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|categoriesData
specifier|private
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|categoriesData
decl_stmt|;
DECL|method|CountingListStream
name|CountingListStream
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|categoriesData
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Entry
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|entry
init|=
name|categoriesData
operator|.
name|next
argument_list|()
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|setCategoriesData
name|void
name|setCategoriesData
parameter_list|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|categoriesData
parameter_list|)
block|{
name|this
operator|.
name|categoriesData
operator|=
name|categoriesData
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
comment|// The counting list is written in a payload, but we don't store it
comment|// nor need norms.
DECL|field|COUNTING_LIST_PAYLOAD_TYPE
specifier|private
specifier|static
specifier|final
name|FieldType
name|COUNTING_LIST_PAYLOAD_TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|COUNTING_LIST_PAYLOAD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|// The drill-down field is added with a TokenStream, hence why it's based on
comment|// TextField type. However in practice, it is added just like StringField.
comment|// Therefore we set its IndexOptions to DOCS_ONLY.
DECL|field|DRILL_DOWN_TYPE
specifier|private
specifier|static
specifier|final
name|FieldType
name|DRILL_DOWN_TYPE
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
static|static
block|{
comment|// TODO: once we cutover to DocValues, we can set it to DOCS_ONLY for this
comment|// FacetFields (not associations)
name|DRILL_DOWN_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|DRILL_DOWN_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|taxonomyWriter
specifier|protected
specifier|final
name|TaxonomyWriter
name|taxonomyWriter
decl_stmt|;
DECL|field|indexingParams
specifier|protected
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
comment|/**    * Constructs a new instance with the {@link FacetIndexingParams#ALL_PARENTS    * default} facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    */
DECL|method|FacetFields
specifier|public
name|FacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|)
block|{
name|this
argument_list|(
name|taxonomyWriter
argument_list|,
name|FacetIndexingParams
operator|.
name|ALL_PARENTS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new instance with the given facet indexing params.    *     * @param taxonomyWriter    *          used to resolve given categories to ordinals    * @param params    *          determines under which fields the categories should be indexed    */
DECL|method|FacetFields
specifier|public
name|FacetFields
parameter_list|(
name|TaxonomyWriter
name|taxonomyWriter
parameter_list|,
name|FacetIndexingParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|taxonomyWriter
operator|=
name|taxonomyWriter
expr_stmt|;
name|this
operator|.
name|indexingParams
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * Creates a mapping between a {@link CategoryListParams} and all    * {@link CategoryPath categories} that are associated with it.    */
DECL|method|createCategoryListMapping
specifier|protected
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
argument_list|>
name|createCategoryListMapping
parameter_list|(
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
block|{
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
argument_list|>
name|categoryLists
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryPath
name|cp
range|:
name|categories
control|)
block|{
comment|// each category may be indexed under a different field, so add it to the right list.
name|CategoryListParams
name|clp
init|=
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|CategoryPath
argument_list|>
operator|)
name|categoryLists
operator|.
name|get
argument_list|(
name|clp
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
expr_stmt|;
name|categoryLists
operator|.
name|put
argument_list|(
name|clp
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
comment|// DrillDownStream modifies the CategoryPath by calling trim(). That means
comment|// that the source category, as the app ses it, is modified. While for
comment|// most apps this is not a problem, we need to protect against it. If
comment|// CategoryPath will be made immutable, we can stop cloning.
name|list
operator|.
name|add
argument_list|(
name|cp
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|categoryLists
return|;
block|}
comment|/** Returns a {@link CategoryListBuilder} for encoding the given categories. */
DECL|method|getCategoryListBuilder
specifier|protected
name|CategoryListBuilder
name|getCategoryListBuilder
parameter_list|(
name|CategoryListParams
name|categoryListParams
parameter_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
comment|/* needed for AssociationsFacetFields */
parameter_list|)
block|{
return|return
operator|new
name|CategoryListBuilder
argument_list|(
name|categoryListParams
argument_list|,
name|indexingParams
argument_list|,
name|taxonomyWriter
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link DrillDownStream} for writing the categories drill-down    * terms.    */
DECL|method|getDrillDownStream
specifier|protected
name|DrillDownStream
name|getDrillDownStream
parameter_list|(
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
block|{
return|return
operator|new
name|DrillDownStream
argument_list|(
name|categories
argument_list|,
name|indexingParams
argument_list|)
return|;
block|}
comment|/**    * Returns the {@link FieldType} with which the drill-down terms should be    * indexed. The default is {@link IndexOptions#DOCS_ONLY}.    */
DECL|method|fieldType
specifier|protected
name|FieldType
name|fieldType
parameter_list|()
block|{
return|return
name|DRILL_DOWN_TYPE
return|;
block|}
comment|/** Adds the needed facet fields to the document. */
DECL|method|addFields
specifier|public
name|void
name|addFields
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|categories
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"categories should not be null"
argument_list|)
throw|;
block|}
comment|// TODO: add reuse capabilities to this class, per CLP objects:
comment|// - drill-down field
comment|// - counting list field
comment|// - DrillDownStream
comment|// - CountingListStream
specifier|final
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
argument_list|>
name|categoryLists
init|=
name|createCategoryListMapping
argument_list|(
name|categories
argument_list|)
decl_stmt|;
comment|// for each CLP we add a different field for drill-down terms as well as for
comment|// counting list data.
for|for
control|(
name|Entry
argument_list|<
name|CategoryListParams
argument_list|,
name|Iterable
argument_list|<
name|CategoryPath
argument_list|>
argument_list|>
name|e
range|:
name|categoryLists
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|CategoryListParams
name|clp
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|field
init|=
name|clp
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
decl_stmt|;
comment|// add the counting list data
name|CategoryListBuilder
name|categoriesPayloadBuilder
init|=
name|getCategoryListBuilder
argument_list|(
name|clp
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CategoryPath
name|cp
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|int
name|ordinal
init|=
name|taxonomyWriter
operator|.
name|addCategory
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|categoriesPayloadBuilder
operator|.
name|handle
argument_list|(
name|ordinal
argument_list|,
name|cp
argument_list|)
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|categoriesData
init|=
name|categoriesPayloadBuilder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|CountingListStream
name|ts
init|=
operator|new
name|CountingListStream
argument_list|()
decl_stmt|;
name|ts
operator|.
name|setCategoriesData
argument_list|(
name|categoriesData
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|ts
argument_list|,
name|COUNTING_LIST_PAYLOAD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
comment|// add the drill-down field
name|DrillDownStream
name|drillDownStream
init|=
name|getDrillDownStream
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Field
name|drillDown
init|=
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|drillDownStream
argument_list|,
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|drillDown
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
