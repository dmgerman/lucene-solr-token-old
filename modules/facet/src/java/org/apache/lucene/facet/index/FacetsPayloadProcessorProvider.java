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
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
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
name|io
operator|.
name|InputStream
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
name|Map
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
name|PayloadProcessorProvider
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
name|Term
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
name|store
operator|.
name|Directory
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
operator|.
name|OrdinalMap
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
name|lucene
operator|.
name|util
operator|.
name|encoding
operator|.
name|IntDecoder
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
name|encoding
operator|.
name|IntEncoder
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link PayloadProcessorProvider} for updating facets ordinal references,  * based on an ordinal map. You should use this code in conjunction with merging  * taxonomies - after you merge taxonomies, you receive an {@link OrdinalMap}  * which maps the 'old' payloads to the 'new' ones. You can use that map to  * re-map the payloads which contain the facets information (ordinals) either  * before or while merging the indexes.  *<p>  * For re-mapping the ordinals before you merge the indexes, do the following:  *   *<pre>  * // merge the old taxonomy with the new one.  * OrdinalMap map = LuceneTaxonomyWriter.addTaxonomies();  * int[] ordmap = map.getMap();  *   * // re-map the ordinals on the old directory.  * Directory oldDir;  * FacetsPayloadProcessorProvider fppp = new FacetsPayloadProcessorProvider(  *     oldDir, ordmap);  * IndexWriterConfig conf = new IndexWriterConfig(VER, ANALYZER);  * conf.setMergePolicy(new ForceOptimizeMergePolicy());  * IndexWriter writer = new IndexWriter(oldDir, conf);  * writer.setPayloadProcessorProvider(fppp);  * writer.forceMerge(1);  * writer.close();  *   * // merge that directory with the new index.  * IndexWriter newWriter; // opened on the 'new' Directory  * newWriter.addIndexes(oldDir);  * newWriter.commit();  *</pre>  *   * For re-mapping the ordinals during index merge, do the following:  *   *<pre>  * // merge the old taxonomy with the new one.  * OrdinalMap map = LuceneTaxonomyWriter.addTaxonomies();  * int[] ordmap = map.getMap();  *   * // Add the index and re-map ordinals on the go  * IndexReader r = IndexReader.open(oldDir);  * IndexWriterConfig conf = new IndexWriterConfig(VER, ANALYZER);  * IndexWriter writer = new IndexWriter(newDir, conf);  * writer.setPayloadProcessorProvider(fppp);  * writer.addIndexes(r);  * writer.commit();  *</pre>  *<p>  *<b>NOTE:</b> while the second example looks simpler, IndexWriter may trigger  * a long merge due to addIndexes. The first example avoids this perhaps  * unneeded merge, as well as can be done separately (e.g. on another node)  * before the index is merged.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetsPayloadProcessorProvider
specifier|public
class|class
name|FacetsPayloadProcessorProvider
extends|extends
name|PayloadProcessorProvider
block|{
DECL|field|workDir
specifier|private
specifier|final
name|Directory
name|workDir
decl_stmt|;
DECL|field|dirProcessor
specifier|private
specifier|final
name|DirPayloadProcessor
name|dirProcessor
decl_stmt|;
comment|/**    * Construct FacetsPayloadProcessorProvider with FacetIndexingParams    *     * @param dir the {@link Directory} containing the segments to update    * @param ordinalMap an array mapping previous facets ordinals to new ones    * @param indexingParams the facets indexing parameters    */
DECL|method|FacetsPayloadProcessorProvider
specifier|public
name|FacetsPayloadProcessorProvider
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
index|[]
name|ordinalMap
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|workDir
operator|=
name|dir
expr_stmt|;
name|dirProcessor
operator|=
operator|new
name|FacetsDirPayloadProcessor
argument_list|(
name|indexingParams
argument_list|,
name|ordinalMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDirProcessor
specifier|public
name|DirPayloadProcessor
name|getDirProcessor
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|workDir
operator|!=
name|dir
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|dirProcessor
return|;
block|}
DECL|class|FacetsDirPayloadProcessor
specifier|public
specifier|static
class|class
name|FacetsDirPayloadProcessor
extends|extends
name|DirPayloadProcessor
block|{
DECL|field|termMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Term
argument_list|,
name|CategoryListParams
argument_list|>
name|termMap
init|=
operator|new
name|HashMap
argument_list|<
name|Term
argument_list|,
name|CategoryListParams
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|ordinalMap
specifier|private
specifier|final
name|int
index|[]
name|ordinalMap
decl_stmt|;
comment|/**      * Construct FacetsDirPayloadProcessor with custom FacetIndexingParams      * @param ordinalMap an array mapping previous facets ordinals to new ones      * @param indexingParams the facets indexing parameters      */
DECL|method|FacetsDirPayloadProcessor
specifier|protected
name|FacetsDirPayloadProcessor
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|,
name|int
index|[]
name|ordinalMap
parameter_list|)
block|{
name|this
operator|.
name|ordinalMap
operator|=
name|ordinalMap
expr_stmt|;
for|for
control|(
name|CategoryListParams
name|params
range|:
name|indexingParams
operator|.
name|getAllCategoryListParams
argument_list|()
control|)
block|{
name|termMap
operator|.
name|put
argument_list|(
name|params
operator|.
name|getTerm
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProcessor
specifier|public
name|PayloadProcessor
name|getProcessor
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO (Facet): don't create terms
name|CategoryListParams
name|params
init|=
name|termMap
operator|.
name|get
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FacetsPayloadProcessor
argument_list|(
name|params
argument_list|,
name|ordinalMap
argument_list|)
return|;
block|}
block|}
comment|/** A PayloadProcessor for updating facets ordinal references, based on an ordinal map */
DECL|class|FacetsPayloadProcessor
specifier|public
specifier|static
class|class
name|FacetsPayloadProcessor
extends|extends
name|PayloadProcessor
block|{
DECL|field|encoder
specifier|private
specifier|final
name|IntEncoder
name|encoder
decl_stmt|;
DECL|field|decoder
specifier|private
specifier|final
name|IntDecoder
name|decoder
decl_stmt|;
DECL|field|ordinalMap
specifier|private
specifier|final
name|int
index|[]
name|ordinalMap
decl_stmt|;
DECL|field|os
specifier|private
specifier|final
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
comment|/**      * @param params defines the encoding of facet ordinals as payload      * @param ordinalMap an array mapping previous facets ordinals to new ones      */
DECL|method|FacetsPayloadProcessor
specifier|protected
name|FacetsPayloadProcessor
parameter_list|(
name|CategoryListParams
name|params
parameter_list|,
name|int
index|[]
name|ordinalMap
parameter_list|)
block|{
name|encoder
operator|=
name|params
operator|.
name|createEncoder
argument_list|()
expr_stmt|;
name|decoder
operator|=
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
expr_stmt|;
name|this
operator|.
name|ordinalMap
operator|=
name|ordinalMap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPayload
specifier|public
name|void
name|processPayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|decoder
operator|.
name|reInit
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|os
operator|.
name|reset
argument_list|()
expr_stmt|;
name|encoder
operator|.
name|reInit
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|long
name|ordinal
decl_stmt|;
while|while
condition|(
operator|(
name|ordinal
operator|=
name|decoder
operator|.
name|decode
argument_list|()
operator|)
operator|!=
name|IntDecoder
operator|.
name|EOS
condition|)
block|{
name|int
name|newOrdinal
init|=
name|ordinalMap
index|[
operator|(
name|int
operator|)
name|ordinal
index|]
decl_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|newOrdinal
argument_list|)
expr_stmt|;
block|}
name|encoder
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// TODO (Facet): avoid copy?
name|byte
name|out
index|[]
init|=
name|os
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|payload
operator|.
name|bytes
operator|=
name|out
expr_stmt|;
name|payload
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|out
operator|.
name|length
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
