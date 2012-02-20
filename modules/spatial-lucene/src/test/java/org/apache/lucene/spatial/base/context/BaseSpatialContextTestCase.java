begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.context
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|context
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
name|spatial
operator|.
name|base
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
name|base
operator|.
name|query
operator|.
name|SpatialArgsParser
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
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|MultiShape
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
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Rectangle
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
name|base
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|CircleImpl
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
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|PointImpl
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
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|RectangleImpl
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import
begin_comment
comment|/**  */
end_comment
begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|BaseSpatialContextTestCase
specifier|public
specifier|abstract
class|class
name|BaseSpatialContextTestCase
block|{
DECL|method|getSpatialContext
specifier|protected
specifier|abstract
name|SpatialContext
name|getSpatialContext
parameter_list|()
function_decl|;
DECL|method|checkArgParser
specifier|public
specifier|static
name|void
name|checkArgParser
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|SpatialArgsParser
name|parser
init|=
operator|new
name|SpatialArgsParser
argument_list|()
decl_stmt|;
name|String
name|arg
init|=
name|SpatialOperation
operator|.
name|IsWithin
operator|+
literal|"(-10 -20 10 20)"
decl_stmt|;
name|SpatialArgs
name|out
init|=
name|parser
operator|.
name|parse
argument_list|(
name|arg
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SpatialOperation
operator|.
name|IsWithin
argument_list|,
name|out
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
name|Rectangle
name|bounds
init|=
operator|(
name|Rectangle
operator|)
name|out
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10.0
argument_list|,
name|bounds
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|bounds
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
comment|// Disjoint should not be scored
name|arg
operator|=
name|SpatialOperation
operator|.
name|IsDisjointTo
operator|+
literal|" (-10 10 -20 20)"
expr_stmt|;
name|out
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|arg
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
argument_list|,
name|out
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|SpatialOperation
operator|.
name|IsDisjointTo
operator|+
literal|"[ ]"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"spatial operations need args"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
literal|"XXXX(-10 10 -20 20)"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unknown operation!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//expected
block|}
block|}
DECL|method|checkShapesImplementEquals
specifier|public
specifier|static
name|void
name|checkShapesImplementEquals
parameter_list|(
name|Class
index|[]
name|classes
parameter_list|)
block|{
for|for
control|(
name|Class
name|clazz
range|:
name|classes
control|)
block|{
try|try
block|{
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"equals"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Shape needs to define 'equals' : "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|clazz
operator|.
name|getDeclaredMethod
argument_list|(
literal|"hashCode"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Shape needs to define 'hashCode' : "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|interface|WriteReader
specifier|public
specifier|static
interface|interface
name|WriteReader
block|{
DECL|method|writeThenRead
name|Shape
name|writeThenRead
parameter_list|(
name|Shape
name|s
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|method|checkBasicShapeIO
specifier|public
specifier|static
name|void
name|checkBasicShapeIO
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|WriteReader
name|help
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Simple Point
name|Shape
name|s
init|=
name|ctx
operator|.
name|readShape
argument_list|(
literal|"10 20"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|ctx
operator|.
name|readShape
argument_list|(
literal|"20,10"
argument_list|)
argument_list|)
expr_stmt|;
comment|//check comma for y,x format
name|assertEquals
argument_list|(
name|s
argument_list|,
name|ctx
operator|.
name|readShape
argument_list|(
literal|"20, 10"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test space
name|Point
name|p
init|=
operator|(
name|Point
operator|)
name|s
decl_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|p
operator|.
name|getX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20.0
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|p
operator|=
operator|(
name|Point
operator|)
name|help
operator|.
name|writeThenRead
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|p
operator|.
name|getX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20.0
argument_list|,
name|p
operator|.
name|getY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|s
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
comment|// BBOX
name|s
operator|=
name|ctx
operator|.
name|readShape
argument_list|(
literal|"-10 -20 10 20"
argument_list|)
expr_stmt|;
name|Rectangle
name|b
init|=
operator|(
name|Rectangle
operator|)
name|s
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10.0
argument_list|,
name|b
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|20.0
argument_list|,
name|b
operator|.
name|getMinY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|b
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20.0
argument_list|,
name|b
operator|.
name|getMaxY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|b
operator|=
operator|(
name|Rectangle
operator|)
name|help
operator|.
name|writeThenRead
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|10.0
argument_list|,
name|b
operator|.
name|getMinX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|20.0
argument_list|,
name|b
operator|.
name|getMinY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.0
argument_list|,
name|b
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20.0
argument_list|,
name|b
operator|.
name|getMaxY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
comment|// Point/Distance
name|s
operator|=
name|ctx
operator|.
name|readShape
argument_list|(
literal|"Circle( 1.23 4.56 distance=7.89)"
argument_list|)
expr_stmt|;
name|CircleImpl
name|circle
init|=
operator|(
name|CircleImpl
operator|)
name|s
decl_stmt|;
name|assertEquals
argument_list|(
literal|1.23
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
operator|.
name|getX
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.56
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
operator|.
name|getY
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7.89
argument_list|,
name|circle
operator|.
name|getDistance
argument_list|()
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|s
operator|.
name|hasArea
argument_list|()
argument_list|)
expr_stmt|;
name|Shape
name|s2
init|=
name|ctx
operator|.
name|readShape
argument_list|(
literal|"Circle( 4.56,1.23 d=7.89 )"
argument_list|)
decl_stmt|;
comment|// use lat,lon and use 'd' abbreviation
name|assertEquals
argument_list|(
name|s
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
comment|//--------------------------------------------------------------
comment|// Actual tests
comment|//--------------------------------------------------------------
annotation|@
name|Test
DECL|method|testArgsParser
specifier|public
name|void
name|testArgsParser
parameter_list|()
throws|throws
name|Exception
block|{
name|checkArgParser
argument_list|(
name|getSpatialContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testImplementsEqualsAndHash
specifier|public
name|void
name|testImplementsEqualsAndHash
parameter_list|()
throws|throws
name|Exception
block|{
name|checkShapesImplementEquals
argument_list|(
operator|new
name|Class
index|[]
block|{
name|PointImpl
operator|.
name|class
block|,
name|CircleImpl
operator|.
name|class
block|,
name|RectangleImpl
operator|.
name|class
block|,
name|MultiShape
operator|.
name|class
block|,     }
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleShapeIO
specifier|public
name|void
name|testSimpleShapeIO
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SpatialContext
name|io
init|=
name|getSpatialContext
argument_list|()
decl_stmt|;
name|checkBasicShapeIO
argument_list|(
name|io
argument_list|,
operator|new
name|WriteReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Shape
name|writeThenRead
parameter_list|(
name|Shape
name|s
parameter_list|)
block|{
name|String
name|buff
init|=
name|io
operator|.
name|toString
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|io
operator|.
name|readShape
argument_list|(
name|buff
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|//Looking for more tests?  Shapes are tested in TestShapes2D.
block|}
end_class
end_unit
