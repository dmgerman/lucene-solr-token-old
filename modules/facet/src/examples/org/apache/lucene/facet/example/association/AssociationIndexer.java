begin_unit
begin_package
DECL|package|org.apache.lucene.facet.example.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|association
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
name|index
operator|.
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|enhancements
operator|.
name|EnhancementsDocumentBuilder
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
name|enhancements
operator|.
name|association
operator|.
name|AssociationProperty
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
name|example
operator|.
name|ExampleUtils
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
name|example
operator|.
name|simple
operator|.
name|SimpleUtils
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
name|CategoryContainer
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
name|CategoryDocumentBuilder
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
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Sample indexer creates an index, and adds to it sample documents with  * categories, which can be simple or contain associations.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|AssociationIndexer
specifier|public
class|class
name|AssociationIndexer
block|{
comment|/**    * Create an index, and adds to it sample documents and categories.    *     * @param indexDir    *            Directory in which the index should be created.    * @param taxoDir    *            Directory in which the taxonomy index should be created.    * @throws Exception    *             on error (no detailed exception handling here for sample    *             simplicity    */
DECL|method|index
specifier|public
specifier|static
name|void
name|index
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create and open an index writer
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|ExampleUtils
operator|.
name|EXAMPLE_VER
argument_list|,
name|SimpleUtils
operator|.
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
comment|// create and open a taxonomy writer
name|TaxonomyWriter
name|taxo
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
decl_stmt|;
comment|// loop over sample documents
name|int
name|nDocsAdded
init|=
literal|0
decl_stmt|;
name|int
name|nFacetsAdded
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|SimpleUtils
operator|.
name|docTexts
operator|.
name|length
condition|;
name|docNum
operator|++
control|)
block|{
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|" ++++ DOC ID: "
operator|+
name|docNum
argument_list|)
expr_stmt|;
comment|// obtain the sample categories for current document
name|CategoryContainer
name|categoryContainer
init|=
operator|new
name|CategoryContainer
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryPath
name|path
range|:
name|SimpleUtils
operator|.
name|categories
index|[
name|docNum
index|]
control|)
block|{
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"\t ++++ PATH: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
comment|// and also those with associations
name|CategoryPath
index|[]
name|associationsPaths
init|=
name|AssociationUtils
operator|.
name|categories
index|[
name|docNum
index|]
decl_stmt|;
name|AssociationProperty
index|[]
name|associationProps
init|=
name|AssociationUtils
operator|.
name|associations
index|[
name|docNum
index|]
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
name|associationsPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|associationsPaths
index|[
name|i
index|]
argument_list|,
name|associationProps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"\t $$$$ Association: ("
operator|+
name|associationsPaths
index|[
name|i
index|]
operator|+
literal|","
operator|+
name|associationProps
index|[
name|i
index|]
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|// we do not alter indexing parameters!
comment|// a category document builder will add the categories to a document
comment|// once build() is called
name|CategoryDocumentBuilder
name|categoryDocBuilder
init|=
operator|new
name|EnhancementsDocumentBuilder
argument_list|(
name|taxo
argument_list|,
name|AssociationUtils
operator|.
name|assocIndexingParams
argument_list|)
decl_stmt|;
name|categoryDocBuilder
operator|.
name|setCategories
argument_list|(
name|categoryContainer
argument_list|)
expr_stmt|;
comment|// create a plain Lucene document and add some regular Lucene fields
comment|// to it
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|SimpleUtils
operator|.
name|TITLE
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|,
name|SimpleUtils
operator|.
name|docTitles
index|[
name|docNum
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|SimpleUtils
operator|.
name|TEXT
argument_list|,
name|SimpleUtils
operator|.
name|docTexts
index|[
name|docNum
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|// invoke the category document builder for adding categories to the
comment|// document and,
comment|// as required, to the taxonomy index
name|categoryDocBuilder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// finally add the document to the index
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|nDocsAdded
operator|++
expr_stmt|;
name|nFacetsAdded
operator|+=
name|categoryContainer
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
comment|// commit changes.
comment|// we commit changes to the taxonomy index prior to committing them to
comment|// the search index.
comment|// this is important, so that all facets referred to by documents in the
comment|// search index
comment|// will indeed exist in the taxonomy index.
name|taxo
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// close the taxonomy index and the index - all modifications are
comment|// now safely in the provided directories: indexDir and taxoDir.
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ExampleUtils
operator|.
name|log
argument_list|(
literal|"Indexed "
operator|+
name|nDocsAdded
operator|+
literal|" documents with overall "
operator|+
name|nFacetsAdded
operator|+
literal|" facets."
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
