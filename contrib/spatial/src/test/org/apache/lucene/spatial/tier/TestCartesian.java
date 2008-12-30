begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
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
name|LinkedList
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|WhitespaceAnalyzer
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
name|search
operator|.
name|Hits
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
name|Sort
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
name|SortField
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
name|TermQuery
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
name|tier
operator|.
name|DistanceQueryBuilder
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
name|tier
operator|.
name|DistanceSortSource
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
name|tier
operator|.
name|DistanceUtils
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
name|tier
operator|.
name|InvalidGeoException
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
name|tier
operator|.
name|projections
operator|.
name|CartesianTierPlotter
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
name|tier
operator|.
name|projections
operator|.
name|IProjector
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
name|tier
operator|.
name|projections
operator|.
name|SinusoidalProjector
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
name|store
operator|.
name|RAMDirectory
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
name|NumberUtils
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
name|function
operator|.
name|CustomScoreQuery
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
name|function
operator|.
name|FieldScoreQuery
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
name|function
operator|.
name|FieldScoreQuery
operator|.
name|Type
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|TestCartesian
specifier|public
class|class
name|TestCartesian
extends|extends
name|TestCase
block|{
comment|/**    * @param args    */
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
comment|// reston va
DECL|field|lat
specifier|private
name|double
name|lat
init|=
literal|38.969398
decl_stmt|;
DECL|field|lng
specifier|private
name|double
name|lng
init|=
operator|-
literal|77.386398
decl_stmt|;
DECL|field|latField
specifier|private
name|String
name|latField
init|=
literal|"lat"
decl_stmt|;
DECL|field|lngField
specifier|private
name|String
name|lngField
init|=
literal|"lng"
decl_stmt|;
DECL|field|ctps
specifier|private
name|List
argument_list|<
name|CartesianTierPlotter
argument_list|>
name|ctps
init|=
operator|new
name|LinkedList
argument_list|<
name|CartesianTierPlotter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|project
specifier|private
name|IProjector
name|project
init|=
operator|new
name|SinusoidalProjector
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setUpPlotter
argument_list|(
literal|2
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|addData
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
DECL|method|setUpPlotter
specifier|private
name|void
name|setUpPlotter
parameter_list|(
name|int
name|base
parameter_list|,
name|int
name|top
parameter_list|)
block|{
for|for
control|(
init|;
name|base
operator|<=
name|top
condition|;
name|base
operator|++
control|)
block|{
name|ctps
operator|.
name|add
argument_list|(
operator|new
name|CartesianTierPlotter
argument_list|(
name|base
argument_list|,
name|project
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addPoint
specifier|private
name|void
name|addPoint
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lng
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// convert the lat / long to lucene fields
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|latField
argument_list|,
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|lngField
argument_list|,
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|lng
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a default meta field to make searching all documents easy
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"metafile"
argument_list|,
literal|"doc"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ctpsize
init|=
name|ctps
operator|.
name|size
argument_list|()
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
name|ctpsize
condition|;
name|i
operator|++
control|)
block|{
name|CartesianTierPlotter
name|ctp
init|=
name|ctps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|ctp
operator|.
name|getTierFieldName
argument_list|()
argument_list|,
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|ctp
operator|.
name|getTierBoxId
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|)
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|addData
specifier|private
name|void
name|addData
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"McCormick&amp; Schmick's Seafood Restaurant"
argument_list|,
literal|38.9579000
argument_list|,
operator|-
literal|77.3572000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Jimmy's Old Town Tavern"
argument_list|,
literal|38.9690000
argument_list|,
operator|-
literal|77.3862000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Ned Devine's"
argument_list|,
literal|38.9510000
argument_list|,
operator|-
literal|77.4107000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Old Brogue Irish Pub"
argument_list|,
literal|38.9955000
argument_list|,
operator|-
literal|77.2884000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Alf Laylah Wa Laylah"
argument_list|,
literal|38.8956000
argument_list|,
operator|-
literal|77.4258000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Sully's Restaurant&amp; Supper"
argument_list|,
literal|38.9003000
argument_list|,
operator|-
literal|77.4467000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"TGIFriday"
argument_list|,
literal|38.8725000
argument_list|,
operator|-
literal|77.3829000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Potomac Swing Dance Club"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2639000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"White Tiger Restaurant"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2638000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Jammin' Java"
argument_list|,
literal|38.9039000
argument_list|,
operator|-
literal|77.2622000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Potomac Swing Dance Club"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2639000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"WiseAcres Comedy Club"
argument_list|,
literal|38.9248000
argument_list|,
operator|-
literal|77.2344000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Glen Echo Spanish Ballroom"
argument_list|,
literal|38.9691000
argument_list|,
operator|-
literal|77.1400000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Whitlow's on Wilson"
argument_list|,
literal|38.8889000
argument_list|,
operator|-
literal|77.0926000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Iota Club and Cafe"
argument_list|,
literal|38.8890000
argument_list|,
operator|-
literal|77.0923000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Hilton Washington Embassy Row"
argument_list|,
literal|38.9103000
argument_list|,
operator|-
literal|77.0451000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"HorseFeathers, Bar& Grill"
argument_list|,
literal|39.01220000000001
argument_list|,
operator|-
literal|77.3942
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|//writer.close();
block|}
DECL|method|testRange
specifier|public
name|void
name|testRange
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidGeoException
block|{
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
specifier|final
name|double
name|miles
init|=
literal|6.0
decl_stmt|;
comment|// create a distance query
specifier|final
name|DistanceQueryBuilder
name|dq
init|=
operator|new
name|DistanceQueryBuilder
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|miles
argument_list|,
name|latField
argument_list|,
name|lngField
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dq
argument_list|)
expr_stmt|;
comment|//create a term query to search against all documents
name|Query
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"metafile"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
decl_stmt|;
name|FieldScoreQuery
name|fsQuery
init|=
operator|new
name|FieldScoreQuery
argument_list|(
literal|"geo_distance"
argument_list|,
name|Type
operator|.
name|FLOAT
argument_list|)
decl_stmt|;
name|CustomScoreQuery
name|customScore
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|tq
argument_list|,
name|fsQuery
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|float
name|customScore
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|subQueryScore
parameter_list|,
name|float
name|valSrcScore
parameter_list|)
block|{
comment|//System.out.println(doc);
if|if
condition|(
name|dq
operator|.
name|distanceFilter
operator|.
name|getDistance
argument_list|(
name|doc
argument_list|)
operator|==
literal|null
condition|)
return|return
literal|0
return|;
name|double
name|distance
init|=
name|dq
operator|.
name|distanceFilter
operator|.
name|getDistance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// boost score shouldn't exceed 1
if|if
condition|(
name|distance
operator|<
literal|1.0d
condition|)
name|distance
operator|=
literal|1.0d
expr_stmt|;
comment|//boost by distance is invertly proportional to
comment|// to distance from center point to location
name|float
name|score
init|=
operator|new
name|Float
argument_list|(
operator|(
name|miles
operator|-
name|distance
operator|)
operator|/
name|miles
argument_list|)
operator|.
name|floatValue
argument_list|()
decl_stmt|;
return|return
name|score
operator|*
name|subQueryScore
return|;
block|}
block|}
decl_stmt|;
comment|// Create a distance sort
comment|// As the radius filter has performed the distance calculations
comment|// already, pass in the filter to reuse the results.
comment|//
name|DistanceSortSource
name|dsort
init|=
operator|new
name|DistanceSortSource
argument_list|(
name|dq
operator|.
name|distanceFilter
argument_list|)
decl_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"foo"
argument_list|,
name|dsort
argument_list|)
argument_list|)
decl_stmt|;
comment|// Perform the search, using the term query, the serial chain filter, and the
comment|// distance sort
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|customScore
argument_list|,
name|dq
operator|.
name|getFilter
argument_list|()
argument_list|)
decl_stmt|;
comment|//,sort);
name|int
name|results
init|=
name|hits
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// Get a list of distances
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
init|=
name|dq
operator|.
name|distanceFilter
operator|.
name|getDistances
argument_list|()
decl_stmt|;
comment|// distances calculated from filter first pass must be less than total
comment|// docs, from the above test of 20 items, 12 will come from the boundary box
comment|// filter, but only 5 are actually in the radius of the results.
comment|// Note Boundary Box filtering, is not accurate enough for most systems.
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Distance Filter filtered: "
operator|+
name|distances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Results: "
operator|+
name|results
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"============================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Distances should be 14 "
operator|+
name|distances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Results should be 7 "
operator|+
name|results
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|14
argument_list|,
name|distances
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|results
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|d
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|double
name|rsLat
init|=
name|NumberUtils
operator|.
name|SortableStr2double
argument_list|(
name|d
operator|.
name|get
argument_list|(
name|latField
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|rsLng
init|=
name|NumberUtils
operator|.
name|SortableStr2double
argument_list|(
name|d
operator|.
name|get
argument_list|(
name|lngField
argument_list|)
argument_list|)
decl_stmt|;
name|Double
name|geo_distance
init|=
name|distances
operator|.
name|get
argument_list|(
name|hits
operator|.
name|id
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|distance
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|rsLat
argument_list|,
name|rsLng
argument_list|)
decl_stmt|;
name|double
name|llm
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getLLMDistance
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
name|rsLat
argument_list|,
name|rsLng
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Name: "
operator|+
name|name
operator|+
literal|", Distance (res, ortho, harvesine):"
operator|+
name|distance
operator|+
literal|" |"
operator|+
name|geo_distance
operator|+
literal|"|"
operator|+
name|llm
operator|+
literal|" | score "
operator|+
name|hits
operator|.
name|score
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|distance
operator|-
name|llm
operator|)
argument_list|)
operator|<
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
name|distance
operator|<
name|miles
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
