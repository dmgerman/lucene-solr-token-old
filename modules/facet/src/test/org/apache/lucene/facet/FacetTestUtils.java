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
name|Collection
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
name|standard
operator|.
name|StandardAnalyzer
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
name|search
operator|.
name|Collector
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|TopScoreDocCollector
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
name|search
operator|.
name|MultiCollector
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
name|FacetsCollector
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
name|CountFacetRequest
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
begin_class
DECL|class|FacetTestUtils
specifier|public
class|class
name|FacetTestUtils
block|{
DECL|method|createIndexTaxonomyDirs
specifier|public
specifier|static
name|Directory
index|[]
index|[]
name|createIndexTaxonomyDirs
parameter_list|(
name|int
name|number
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
index|[]
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|number
index|]
index|[
literal|2
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
name|number
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|LuceneTestCase
operator|.
name|newDirectory
argument_list|()
expr_stmt|;
name|dirs
index|[
name|i
index|]
index|[
literal|1
index|]
operator|=
name|LuceneTestCase
operator|.
name|newDirectory
argument_list|()
expr_stmt|;
block|}
return|return
name|dirs
return|;
block|}
DECL|method|createIndexTaxonomyReaderPair
specifier|public
specifier|static
name|IndexTaxonomyReaderPair
index|[]
name|createIndexTaxonomyReaderPair
parameter_list|(
name|Directory
index|[]
index|[]
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexTaxonomyReaderPair
index|[]
name|pairs
init|=
operator|new
name|IndexTaxonomyReaderPair
index|[
name|dirs
operator|.
name|length
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexTaxonomyReaderPair
name|pair
init|=
operator|new
name|IndexTaxonomyReaderPair
argument_list|()
decl_stmt|;
name|pair
operator|.
name|indexReader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|pair
operator|.
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|pair
operator|.
name|indexReader
argument_list|)
expr_stmt|;
name|pair
operator|.
name|taxReader
operator|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|pairs
index|[
name|i
index|]
operator|=
name|pair
expr_stmt|;
block|}
return|return
name|pairs
return|;
block|}
DECL|method|createIndexTaxonomyWriterPair
specifier|public
specifier|static
name|IndexTaxonomyWriterPair
index|[]
name|createIndexTaxonomyWriterPair
parameter_list|(
name|Directory
index|[]
index|[]
name|dirs
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexTaxonomyWriterPair
index|[]
name|pairs
init|=
operator|new
name|IndexTaxonomyWriterPair
index|[
name|dirs
operator|.
name|length
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexTaxonomyWriterPair
name|pair
init|=
operator|new
name|IndexTaxonomyWriterPair
argument_list|()
decl_stmt|;
name|pair
operator|.
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dirs
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|pair
operator|.
name|taxWriter
operator|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dirs
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|pair
operator|.
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|pair
operator|.
name|taxWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|pairs
index|[
name|i
index|]
operator|=
name|pair
expr_stmt|;
block|}
return|return
name|pairs
return|;
block|}
DECL|method|search
specifier|public
specifier|static
name|Collector
index|[]
name|search
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|DefaultFacetIndexingParams
name|iParams
parameter_list|,
name|int
name|k
parameter_list|,
name|String
modifier|...
name|facetNames
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
block|{
name|Collector
index|[]
name|collectors
init|=
operator|new
name|Collector
index|[
literal|2
index|]
decl_stmt|;
name|FacetSearchParams
name|facetSearchParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|iParams
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|FacetRequest
argument_list|>
name|fRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|facetName
range|:
name|facetNames
control|)
block|{
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|facetName
argument_list|)
decl_stmt|;
name|FacetRequest
name|fq
init|=
operator|new
name|CountFacetRequest
argument_list|(
name|cp
argument_list|,
name|k
argument_list|)
decl_stmt|;
name|facetSearchParams
operator|.
name|addFacetRequest
argument_list|(
name|fq
argument_list|)
expr_stmt|;
name|fRequests
operator|.
name|add
argument_list|(
name|fq
argument_list|)
expr_stmt|;
block|}
name|TopScoreDocCollector
name|topDocsCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FacetsCollector
name|facetsCollector
init|=
operator|new
name|FacetsCollector
argument_list|(
name|facetSearchParams
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
name|Collector
name|mColl
init|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|topDocsCollector
argument_list|,
name|facetsCollector
argument_list|)
decl_stmt|;
name|collectors
index|[
literal|0
index|]
operator|=
name|topDocsCollector
expr_stmt|;
name|collectors
index|[
literal|1
index|]
operator|=
name|facetsCollector
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|mColl
argument_list|)
expr_stmt|;
return|return
name|collectors
return|;
block|}
DECL|method|add
specifier|public
specifier|static
name|void
name|add
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
modifier|...
name|strings
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptIndexException
block|{
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
name|cps
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
name|strings
argument_list|)
decl_stmt|;
name|cps
operator|.
name|add
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
operator|new
name|CategoryDocumentBuilder
argument_list|(
name|tw
argument_list|,
name|iParams
argument_list|)
operator|.
name|setCategoryPaths
argument_list|(
name|cps
argument_list|)
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
name|TextField
operator|.
name|TYPE_STORED
argument_list|,
literal|"alpha"
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
DECL|class|IndexTaxonomyReaderPair
specifier|public
specifier|static
class|class
name|IndexTaxonomyReaderPair
block|{
DECL|field|indexReader
specifier|public
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|taxReader
specifier|public
name|TaxonomyReader
name|taxReader
decl_stmt|;
DECL|field|indexSearcher
specifier|public
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|indexSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|IndexTaxonomyWriterPair
specifier|public
specifier|static
class|class
name|IndexTaxonomyWriterPair
block|{
DECL|field|indexWriter
specifier|public
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|taxWriter
specifier|public
name|TaxonomyWriter
name|taxWriter
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
