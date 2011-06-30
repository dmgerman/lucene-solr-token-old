begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Arrays
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|DocumentBuilder
operator|.
name|DocumentBuilderException
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
name|Analyzer
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
name|MockAnalyzer
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
name|MockTokenizer
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
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
operator|.
name|TermVector
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
name|CorruptIndexException
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
name|DocsEnum
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
name|IndexReader
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
name|index
operator|.
name|MultiFields
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
name|RandomIndexWriter
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
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|IndexSearcher
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
name|util
operator|.
name|Bits
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
name|LuceneTestCase
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
name|_TestUtil
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
name|DefaultFacetIndexingParams
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
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|TaxonomyReader
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
name|LuceneTaxonomyReader
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
comment|/** Base faceted search test. */
end_comment
begin_class
DECL|class|FacetTestBase
specifier|public
specifier|abstract
class|class
name|FacetTestBase
extends|extends
name|LuceneTestCase
block|{
comment|/** Documents text field. */
DECL|field|CONTENT_FIELD
specifier|protected
specifier|static
specifier|final
name|String
name|CONTENT_FIELD
init|=
literal|"content"
decl_stmt|;
comment|/** Directory for the index */
DECL|field|indexDir
specifier|protected
name|Directory
name|indexDir
decl_stmt|;
comment|/** Directory for the taxonomy */
DECL|field|taxoDir
specifier|protected
name|Directory
name|taxoDir
decl_stmt|;
comment|/** taxonomy Reader for the test. */
DECL|field|taxoReader
specifier|protected
name|TaxonomyReader
name|taxoReader
decl_stmt|;
comment|/** Index Reader for the test. */
DECL|field|indexReader
specifier|protected
name|IndexReader
name|indexReader
decl_stmt|;
comment|/** Searcher for the test. */
DECL|field|searcher
specifier|protected
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/** documents text (for the text field). */
DECL|field|DEFAULT_CONTENT
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_CONTENT
init|=
block|{
literal|"the white car is the one I want."
block|,
literal|"the white dog does not belong to anyone."
block|,   }
decl_stmt|;
comment|/** Facets: facets[D][F] == category-path no. F for document no. D. */
DECL|field|DEFAULT_CATEGORIES
specifier|private
specifier|static
specifier|final
name|CategoryPath
index|[]
index|[]
name|DEFAULT_CATEGORIES
init|=
block|{
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f1"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f2"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f1"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f3"
argument_list|)
block|}
block|,   }
decl_stmt|;
comment|/** categories to be added to specified doc */
DECL|method|getCategories
specifier|protected
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|getCategories
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|DEFAULT_CATEGORIES
index|[
name|doc
index|]
argument_list|)
return|;
block|}
comment|/** Number of documents to index */
DECL|method|numDocsToIndex
specifier|protected
name|int
name|numDocsToIndex
parameter_list|()
block|{
return|return
name|DEFAULT_CONTENT
operator|.
name|length
return|;
block|}
comment|/** content to be added to specified doc */
DECL|method|getContent
specifier|protected
name|String
name|getContent
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|DEFAULT_CONTENT
index|[
name|doc
index|]
return|;
block|}
comment|/** Prepare index (in RAM) with single partition */
DECL|method|initIndex
specifier|protected
specifier|final
name|void
name|initIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|initIndex
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/** Prepare index (in RAM) with some documents and some facets */
DECL|method|initIndex
specifier|protected
specifier|final
name|void
name|initIndex
parameter_list|(
name|int
name|partitionSize
parameter_list|)
throws|throws
name|Exception
block|{
name|initIndex
argument_list|(
name|partitionSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Prepare index (in RAM/Disk) with some documents and some facets */
DECL|method|initIndex
specifier|protected
specifier|final
name|void
name|initIndex
parameter_list|(
name|int
name|partitionSize
parameter_list|,
name|boolean
name|onDisk
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Partition Size: "
operator|+
name|partitionSize
operator|+
literal|"  onDisk: "
operator|+
name|onDisk
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|onDisk
condition|)
block|{
name|File
name|indexFile
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|indexDir
operator|=
name|newFSDirectory
argument_list|(
name|indexFile
argument_list|)
expr_stmt|;
name|taxoDir
operator|=
name|newFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|indexFile
argument_list|,
literal|"facets"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|getAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|populateIndex
argument_list|(
name|iw
argument_list|,
name|taxo
argument_list|,
name|getFacetIndexingParams
argument_list|(
name|partitionSize
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit changes (taxonomy prior to search index for consistency)
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
comment|// prepare for searching
name|taxoReader
operator|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
expr_stmt|;
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a default facet indexing params */
DECL|method|getFacetIndexingParams
specifier|protected
name|FacetIndexingParams
name|getFacetIndexingParams
parameter_list|(
specifier|final
name|int
name|partSize
parameter_list|)
block|{
return|return
operator|new
name|DefaultFacetIndexingParams
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|int
name|fixedPartitionSize
parameter_list|()
block|{
return|return
name|partSize
return|;
block|}
block|}
return|;
block|}
comment|/**    * Faceted Search Params for the test.    * Sub classes should override in order to test with different faceted search params.    */
DECL|method|getFacetedSearchParams
specifier|protected
name|FacetSearchParams
name|getFacetedSearchParams
parameter_list|()
block|{
return|return
name|getFacetedSearchParams
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|/**    * Faceted Search Params with specified partition size.    * @see #getFacetedSearchParams()    */
DECL|method|getFacetedSearchParams
specifier|protected
name|FacetSearchParams
name|getFacetedSearchParams
parameter_list|(
name|int
name|partitionSize
parameter_list|)
block|{
name|FacetSearchParams
name|res
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|getFacetIndexingParams
argument_list|(
name|partitionSize
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|res
return|;
block|}
comment|/**    * Populate the test index+taxonomy for this test.    *<p>Subclasses can override this to test different scenarios    */
DECL|method|populateIndex
specifier|protected
name|void
name|populateIndex
parameter_list|(
name|RandomIndexWriter
name|iw
parameter_list|,
name|TaxonomyWriter
name|taxo
parameter_list|,
name|FacetIndexingParams
name|iParams
parameter_list|)
throws|throws
name|IOException
throws|,
name|DocumentBuilderException
throws|,
name|CorruptIndexException
block|{
comment|// add test documents
name|int
name|numDocsToIndex
init|=
name|numDocsToIndex
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|numDocsToIndex
condition|;
name|doc
operator|++
control|)
block|{
name|indexDoc
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|taxo
argument_list|,
name|getContent
argument_list|(
name|doc
argument_list|)
argument_list|,
name|getCategories
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// also add a document that would be deleted, so that all tests are also working against deletions in the index
name|String
name|content4del
init|=
literal|"ContentOfDocToDelete"
decl_stmt|;
name|indexDoc
argument_list|(
name|iParams
argument_list|,
name|iw
argument_list|,
name|taxo
argument_list|,
name|content4del
argument_list|,
name|getCategories
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// commit it
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
name|CONTENT_FIELD
argument_list|,
name|content4del
argument_list|)
argument_list|)
expr_stmt|;
comment|// now delete the committed doc
block|}
comment|/** Close all indexes */
DECL|method|closeAll
specifier|protected
name|void
name|closeAll
parameter_list|()
throws|throws
name|Exception
block|{
comment|// close and nullify everything
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
literal|null
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
literal|null
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|indexDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|=
literal|null
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Analyzer to use for the test.    * Sub classes should override in order to test with different analyzer.    */
DECL|method|getAnalyzer
specifier|protected
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** convenience method: convert sub results to an array */
DECL|method|resultNodesAsArray
specifier|protected
specifier|static
name|FacetResultNode
index|[]
name|resultNodesAsArray
parameter_list|(
name|FacetResultNode
name|parentRes
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|a
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|parentRes
operator|.
name|getSubResults
argument_list|()
control|)
block|{
name|a
operator|.
name|add
argument_list|(
name|frn
argument_list|)
expr_stmt|;
block|}
return|return
name|a
operator|.
name|toArray
argument_list|(
operator|new
name|FacetResultNode
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** utility Create a dummy document with specified categories and content */
DECL|method|indexDoc
specifier|protected
specifier|final
name|void
name|indexDoc
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|RandomIndexWriter
name|iw
parameter_list|,
name|TaxonomyWriter
name|tw
parameter_list|,
name|String
name|content
parameter_list|,
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|categories
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptIndexException
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|CategoryDocumentBuilder
name|builder
init|=
operator|new
name|CategoryDocumentBuilder
argument_list|(
name|tw
argument_list|,
name|iParams
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setCategoryPaths
argument_list|(
name|categories
argument_list|)
expr_stmt|;
name|builder
operator|.
name|build
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|/** Build the "truth" with ALL the facets enumerating indexes content. */
DECL|method|facetCountsTruth
specifier|protected
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
name|facetCountsTruth
parameter_list|()
throws|throws
name|IOException
block|{
name|FacetIndexingParams
name|iParams
init|=
name|getFacetIndexingParams
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|String
name|delim
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|iParams
operator|.
name|getFacetDelimChar
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
name|res
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|handledTerms
init|=
operator|new
name|HashSet
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|iParams
operator|.
name|getAllCategoryListParams
argument_list|()
control|)
block|{
name|Term
name|baseTerm
init|=
operator|new
name|Term
argument_list|(
name|clp
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|handledTerms
operator|.
name|add
argument_list|(
name|baseTerm
argument_list|)
condition|)
block|{
continue|continue;
comment|// already handled this term (for another list)
block|}
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|indexReader
argument_list|,
name|baseTerm
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Bits
name|deletedDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocsEnum
name|de
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|te
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|de
operator|=
name|te
operator|.
name|docs
argument_list|(
name|deletedDocs
argument_list|,
name|de
argument_list|)
expr_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|de
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|cnt
operator|++
expr_stmt|;
block|}
name|res
operator|.
name|put
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
operator|.
name|split
argument_list|(
name|delim
argument_list|)
argument_list|)
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/** Validate counts for returned facets, and that there are not too many results */
DECL|method|assertCountsAndCardinality
specifier|protected
specifier|static
name|void
name|assertCountsAndCardinality
parameter_list|(
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
name|facetCountsTruth
parameter_list|,
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|FacetResult
name|fr
range|:
name|facetResults
control|)
block|{
name|FacetResultNode
name|topResNode
init|=
name|fr
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|FacetRequest
name|freq
init|=
name|fr
operator|.
name|getFacetRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|freq
operator|.
name|getCategoryPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\t\t"
operator|+
name|topResNode
argument_list|)
expr_stmt|;
block|}
name|assertCountsAndCardinality
argument_list|(
name|facetCountsTruth
argument_list|,
name|topResNode
argument_list|,
name|freq
operator|.
name|getNumResults
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Validate counts for returned facets, and that there are not too many results */
DECL|method|assertCountsAndCardinality
specifier|private
specifier|static
name|void
name|assertCountsAndCardinality
parameter_list|(
name|Map
argument_list|<
name|CategoryPath
argument_list|,
name|Integer
argument_list|>
name|facetCountsTruth
parameter_list|,
name|FacetResultNode
name|resNode
parameter_list|,
name|int
name|reqNumResults
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|actualNumResults
init|=
name|resNode
operator|.
name|getNumSubResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NumResults: "
operator|+
name|actualNumResults
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Too many results!"
argument_list|,
name|actualNumResults
operator|<=
name|reqNumResults
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|subRes
range|:
name|resNode
operator|.
name|getSubResults
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"wrong count for: "
operator|+
name|subRes
argument_list|,
name|facetCountsTruth
operator|.
name|get
argument_list|(
name|subRes
operator|.
name|getLabel
argument_list|()
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|subRes
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertCountsAndCardinality
argument_list|(
name|facetCountsTruth
argument_list|,
name|subRes
argument_list|,
name|reqNumResults
argument_list|)
expr_stmt|;
comment|// recurse into child results
block|}
block|}
comment|/** Validate results equality */
DECL|method|assertSameResults
specifier|protected
specifier|static
name|void
name|assertSameResults
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|expected
parameter_list|,
name|List
argument_list|<
name|FacetResult
argument_list|>
name|actual
parameter_list|)
block|{
name|String
name|expectedResults
init|=
name|resStringValueOnly
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|String
name|actualResults
init|=
name|resStringValueOnly
argument_list|(
name|actual
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|expectedResults
operator|.
name|equals
argument_list|(
name|actualResults
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Results are not the same!"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Expected:\n"
operator|+
name|expectedResults
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Actual"
operator|+
name|actualResults
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Results are not the same!"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** exclude the residue and numDecendants because it is incorrect in sampling */
DECL|method|resStringValueOnly
specifier|private
specifier|static
specifier|final
name|String
name|resStringValueOnly
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResult
name|facetRes
range|:
name|results
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|facetRes
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"Residue:.*.0"
argument_list|,
literal|""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"Num valid Descendants.*"
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
end_class
end_unit
