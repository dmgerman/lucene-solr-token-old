begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
begin_comment
comment|/** Simple tests for {@link LatLonPoint} */
end_comment
begin_class
DECL|class|TestLatLonPoint
specifier|public
class|class
name|TestLatLonPoint
extends|extends
name|LuceneTestCase
block|{
comment|/** Add a single point and search for it in a box */
comment|// NOTE: we don't currently supply an exact search, only ranges, because of the lossiness...
DECL|method|testBoxQuery
specifier|public
name|void
name|testBoxQuery
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
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with a point
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|66
argument_list|,
operator|-
literal|65
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"LatLonPoint<field:18.313693958334625,-65.22744392976165>"
argument_list|,
operator|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"field:[17.99999997485429 TO 18.999999999068677],[-65.9999999217689 TO -64.99999998137355]"
argument_list|,
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|66
argument_list|,
operator|-
literal|65
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// distance query does not quantize inputs
name|assertEquals
argument_list|(
literal|"field:18.0,19.0 +/- 25.0 meters"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
literal|25
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// sort field
name|assertEquals
argument_list|(
literal|"<distance:\"field\" latitude=18.0 longitude=19.0>"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"field"
argument_list|,
literal|18.0
argument_list|,
literal|19.0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Valid values that should not cause exception */
DECL|method|testExtremeValues
specifier|public
name|void
name|testExtremeValues
parameter_list|()
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|90.0
argument_list|,
literal|180.0
argument_list|)
expr_stmt|;
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|90.0
argument_list|,
operator|-
literal|180.0
argument_list|)
expr_stmt|;
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
operator|-
literal|90.0
argument_list|,
literal|180.0
argument_list|)
expr_stmt|;
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
operator|-
literal|90.0
argument_list|,
operator|-
literal|180.0
argument_list|)
expr_stmt|;
block|}
comment|/** Invalid values */
DECL|method|testOutOfRangeValues
specifier|public
name|void
name|testOutOfRangeValues
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
name|Math
operator|.
name|nextUp
argument_list|(
literal|90.0
argument_list|)
argument_list|,
literal|50.0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid latitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
name|Math
operator|.
name|nextDown
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|,
literal|50.0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid latitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|90.0
argument_list|,
name|Math
operator|.
name|nextUp
argument_list|(
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid longitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|90.0
argument_list|,
name|Math
operator|.
name|nextDown
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid longitude"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** NaN: illegal */
DECL|method|testNaNValues
specifier|public
name|void
name|testNaNValues
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|NaN
argument_list|,
literal|50.0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid latitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|50.0
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid longitude"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Inf: illegal */
DECL|method|testInfValues
specifier|public
name|void
name|testInfValues
parameter_list|()
block|{
name|IllegalArgumentException
name|expected
decl_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
literal|50.0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid latitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|,
literal|50.0
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid latitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|50.0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid longitude"
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|LatLonPoint
argument_list|(
literal|"foo"
argument_list|,
literal|50.0
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"invalid longitude"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncodeDecode
specifier|public
name|void
name|testEncodeDecode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// just for testing quantization error
specifier|final
name|double
name|ENCODING_TOLERANCE
init|=
literal|1e-7
decl_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|double
name|lat
init|=
operator|-
literal|90
operator|+
literal|180.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|latEnc
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|lat
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lat="
operator|+
name|lat
operator|+
literal|" latEnc="
operator|+
name|latEnc
operator|+
literal|" diff="
operator|+
operator|(
name|lat
operator|-
name|latEnc
operator|)
argument_list|,
name|lat
argument_list|,
name|latEnc
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|double
name|lon
init|=
operator|-
literal|180
operator|+
literal|360.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|lonEnc
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lon="
operator|+
name|lon
operator|+
literal|" lonEnc="
operator|+
name|lonEnc
operator|+
literal|" diff="
operator|+
operator|(
name|lon
operator|-
name|lonEnc
operator|)
argument_list|,
name|lon
argument_list|,
name|lonEnc
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
block|}
comment|// check edge/interesting cases explicitly
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
literal|0.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
literal|90.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|90.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
operator|-
literal|90.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
literal|0.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|180.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
literal|180.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|180.0
argument_list|,
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
operator|-
literal|180.0
argument_list|)
argument_list|)
argument_list|,
name|ENCODING_TOLERANCE
argument_list|)
expr_stmt|;
block|}
DECL|method|testEncodeDecodeIsStable
specifier|public
name|void
name|testEncodeDecodeIsStable
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|double
name|lat
init|=
operator|-
literal|90
operator|+
literal|180.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|lon
init|=
operator|-
literal|180
operator|+
literal|360.0
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|double
name|latEnc
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|lat
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lonEnc
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|lon
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|latEnc2
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|latEnc
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lonEnc2
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|lonEnc
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|latEnc
argument_list|,
name|latEnc2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|lonEnc
argument_list|,
name|lonEnc2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQueryEquals
specifier|public
name|void
name|testQueryEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
operator|-
literal|40
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
operator|-
literal|40
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
operator|-
literal|40
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|70
argument_list|,
literal|11000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|50
argument_list|,
literal|60
argument_list|,
literal|10000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|double
index|[]
name|polyLats1
init|=
operator|new
name|double
index|[]
block|{
literal|30
block|,
literal|40
block|,
literal|40
block|,
literal|30
block|,
literal|30
block|}
decl_stmt|;
name|double
index|[]
name|polyLons1
init|=
operator|new
name|double
index|[]
block|{
literal|90
block|,
literal|90
block|,
operator|-
literal|40
block|,
operator|-
literal|40
block|,
literal|90
block|}
decl_stmt|;
name|double
index|[]
name|polyLats2
init|=
operator|new
name|double
index|[]
block|{
literal|20
block|,
literal|40
block|,
literal|40
block|,
literal|20
block|,
literal|20
block|}
decl_stmt|;
name|q
operator|=
name|LatLonPoint
operator|.
name|newPolygonQuery
argument_list|(
literal|"field"
argument_list|,
name|polyLats1
argument_list|,
name|polyLons1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|q
argument_list|,
name|LatLonPoint
operator|.
name|newPolygonQuery
argument_list|(
literal|"field"
argument_list|,
name|polyLats1
argument_list|,
name|polyLons1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q
operator|.
name|equals
argument_list|(
name|LatLonPoint
operator|.
name|newPolygonQuery
argument_list|(
literal|"field"
argument_list|,
name|polyLats2
argument_list|,
name|polyLons1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
