begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|List
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
name|facet
operator|.
name|index
operator|.
name|FacetFields
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
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|Query
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestDemoFacets
specifier|public
class|class
name|TestDemoFacets
extends|extends
name|LuceneTestCase
block|{
DECL|field|taxoWriter
specifier|private
name|DirectoryTaxonomyWriter
name|taxoWriter
decl_stmt|;
DECL|field|writer
specifier|private
name|RandomIndexWriter
name|writer
decl_stmt|;
DECL|field|docBuilder
specifier|private
name|FacetFields
name|docBuilder
decl_stmt|;
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
modifier|...
name|categoryPaths
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|categoryPath
range|:
name|categoryPaths
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|categoryPath
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docBuilder
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|writer
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
comment|// Writes facet ords to a separate directory from the
comment|// main index:
name|taxoWriter
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|,
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
comment|// Reused across documents, to add the necessary facet
comment|// fields:
name|docBuilder
operator|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"Author/Bob"
argument_list|,
literal|"Publish Date/2010/10/15"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"Author/Lisa"
argument_list|,
literal|"Publish Date/2010/10/20"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"Author/Lisa"
argument_list|,
literal|"Publish Date/2012/1/1"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"Author/Susan"
argument_list|,
literal|"Publish Date/2012/1/7"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"Author/Frank"
argument_list|,
literal|"Publish Date/1999/5/5"
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// NRT open
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Count both "Publish Date" and "Author" dimensions:
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Date"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
comment|// Aggregatses the facet counts:
name|FacetsCollector
name|c
init|=
operator|new
name|FacetsCollector
argument_list|(
name|fsp
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
comment|// MatchAllDocsQuery is for "browsing" (counts facets
comment|// for all non-deleted docs in the index); normally
comment|// you'd use a "normal" query, and use MultiCollector to
comment|// wrap collecting the "normal" hits and also facets:
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|// Retrieve& verify results:
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|c
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Publish Date (5)\n  2012 (2)\n  2010 (2)\n  1999 (1)\n"
argument_list|,
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Author (5)\n  Lisa (2)\n  Frank (1)\n  Susan (1)\n  Bob (1)\n"
argument_list|,
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now user drills down on Publish Date/2010:
name|fsp
operator|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
name|DrillDown
operator|.
name|query
argument_list|(
name|fsp
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"Publish Date/2010"
argument_list|,
literal|'/'
argument_list|)
argument_list|)
decl_stmt|;
name|c
operator|=
operator|new
name|FacetsCollector
argument_list|(
name|fsp
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|results
operator|=
name|c
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Author (2)\n  Lisa (1)\n  Bob (1)\n"
argument_list|,
name|toSimpleString
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|toSimpleString
specifier|private
name|String
name|toSimpleString
parameter_list|(
name|FacetResult
name|fr
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|toSimpleString
argument_list|(
literal|0
argument_list|,
name|sb
argument_list|,
name|fr
operator|.
name|getFacetResultNode
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toSimpleString
specifier|private
name|void
name|toSimpleString
parameter_list|(
name|int
name|depth
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|FacetResultNode
name|node
parameter_list|,
name|String
name|indent
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
operator|+
name|node
operator|.
name|getLabel
argument_list|()
operator|.
name|getComponent
argument_list|(
name|depth
argument_list|)
operator|+
literal|" ("
operator|+
operator|(
name|int
operator|)
name|node
operator|.
name|getValue
argument_list|()
operator|+
literal|")\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResultNode
name|childNode
range|:
name|node
operator|.
name|getSubResults
argument_list|()
control|)
block|{
name|toSimpleString
argument_list|(
name|depth
operator|+
literal|1
argument_list|,
name|sb
argument_list|,
name|childNode
argument_list|,
name|indent
operator|+
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
