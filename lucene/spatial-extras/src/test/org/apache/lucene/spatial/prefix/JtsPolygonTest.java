begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.prefix
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
package|;
end_package
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContextFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Shape
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
name|search
operator|.
name|ScoreDoc
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
name|TopDocs
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
name|spatial
operator|.
name|StrategyTestCase
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|QuadPrefixTree
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgs
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
begin_class
DECL|class|JtsPolygonTest
specifier|public
class|class
name|JtsPolygonTest
extends|extends
name|StrategyTestCase
block|{
DECL|field|LUCENE_4464_distErrPct
specifier|private
specifier|static
specifier|final
name|double
name|LUCENE_4464_distErrPct
init|=
name|SpatialArgs
operator|.
name|DEFAULT_DISTERRPCT
decl_stmt|;
comment|//DEFAULT 2.5%
DECL|method|JtsPolygonTest
specifier|public
name|JtsPolygonTest
parameter_list|()
block|{
try|try
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"spatialContextFactory"
argument_list|,
literal|"org.locationtech.spatial4j.context.jts.JtsSpatialContextFactory"
argument_list|)
expr_stmt|;
name|ctx
operator|=
name|SpatialContextFactory
operator|.
name|makeSpatialContext
argument_list|(
name|args
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
name|assumeTrue
argument_list|(
literal|"This test requires JTS jar: "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|GeohashPrefixTree
name|grid
init|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|11
argument_list|)
decl_stmt|;
comment|//< 1 meter == 11 maxLevels
name|this
operator|.
name|strategy
operator|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|RecursivePrefixTreeStrategy
operator|)
name|this
operator|.
name|strategy
operator|)
operator|.
name|setDistErrPct
argument_list|(
name|LUCENE_4464_distErrPct
argument_list|)
expr_stmt|;
comment|//1% radius (small!)
block|}
annotation|@
name|Test
comment|/** LUCENE-4464 */
DECL|method|testCloseButNoMatch
specifier|public
name|void
name|testCloseButNoMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|getAddAndVerifyIndexedDocuments
argument_list|(
literal|"LUCENE-4464.txt"
argument_list|)
expr_stmt|;
name|SpatialArgs
name|args
init|=
name|q
argument_list|(
literal|"POLYGON((-93.18100824442227 45.25676372469945,"
operator|+
literal|"-93.23182001200654 45.21421290799412,"
operator|+
literal|"-93.16315546122038 45.23742639412364,"
operator|+
literal|"-93.18100824442227 45.25676372469945))"
argument_list|,
name|LUCENE_4464_distErrPct
argument_list|)
decl_stmt|;
name|SearchResults
name|got
init|=
name|executeQuery
argument_list|(
name|strategy
operator|.
name|makeQuery
argument_list|(
name|args
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|got
operator|.
name|numFound
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"poly2"
argument_list|,
name|got
operator|.
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
comment|//did not find poly 1 !
block|}
DECL|method|q
specifier|private
name|SpatialArgs
name|q
parameter_list|(
name|String
name|shapeStr
parameter_list|,
name|double
name|distErrPct
parameter_list|)
throws|throws
name|ParseException
block|{
name|Shape
name|shape
init|=
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|shapeStr
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
name|args
operator|.
name|setDistErrPct
argument_list|(
name|distErrPct
argument_list|)
expr_stmt|;
return|return
name|args
return|;
block|}
comment|/**    * A PrefixTree pruning optimization gone bad.    * See<a href="https://issues.apache.org/jira/browse/LUCENE-4770">LUCENE-4770</a>.    */
annotation|@
name|Test
DECL|method|testBadPrefixTreePrune
specifier|public
name|void
name|testBadPrefixTreePrune
parameter_list|()
throws|throws
name|Exception
block|{
name|Shape
name|area
init|=
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
literal|"POLYGON((-122.83 48.57, -122.77 48.56, -122.79 48.53, -122.83 48.57))"
argument_list|)
decl_stmt|;
name|SpatialPrefixTree
name|trie
init|=
operator|new
name|QuadPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|TermQueryPrefixTreeStrategy
name|strategy
init|=
operator|new
name|TermQueryPrefixTreeStrategy
argument_list|(
name|trie
argument_list|,
literal|"geo"
argument_list|)
decl_stmt|;
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
name|TextField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|Field
index|[]
name|fields
init|=
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|area
argument_list|,
literal|0.025
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Point
name|upperleft
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|122.88
argument_list|,
literal|48.54
argument_list|)
decl_stmt|;
name|Point
name|lowerright
init|=
name|ctx
operator|.
name|makePoint
argument_list|(
operator|-
literal|122.82
argument_list|,
literal|48.62
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|strategy
operator|.
name|makeQuery
argument_list|(
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|upperleft
argument_list|,
name|lowerright
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|TopDocs
name|search
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|search
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|scoreDocs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|indexSearcher
operator|.
name|doc
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
